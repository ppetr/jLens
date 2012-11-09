/*
    This file is part of jLens.

    jLens is free software: you can redistribute it and/or modify it under the
    terms of the GNU Lesser General Public License as published by the Free
    Software Foundation, either version 3 of the License, or (at your option)
    any later version.

    jLens is distributed in the hope that it will be useful, but WITHOUT ANY
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
    FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
    more details.

    You should have received a copy of the GNU Lesser General Public License
    along with jLens.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cvut.felk.cyber.jlens.processor;

import java.lang.annotation.Annotation;
import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.*;
import static javax.lang.model.util.Types.*;
import static javax.lang.model.util.ElementFilter.*;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.*;
import java.io.*;

import java.text.SimpleDateFormat;
import java.util.*;
import static java.util.Collections.*;

import freemarker.template.*;

import cz.cvut.felk.cyber.jlens.*;


/**
 * TODO: Options - class suffix.
 */
@SupportedSourceVersion(javax.lang.model.SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("cz.cvut.felk.cyber.jlens.LensProperties")
@SupportedOptions({})
public class JLensProcessor
    extends AbstractProcessor
{
    //private final org.slf4j.Logger log =
    //    org.slf4j.LoggerFactory.getLogger(JLensProcessor.class);

    public static final String CLASS_SUFFIX = "_L";

    protected static final Class<LensProperties> ANNOTATION_CLASS = LensProperties.class;

 
    private final Configuration fmc;

    public JLensProcessor()
    {
        fmc = new Configuration();
        fmc.setClassForTemplateLoading(this.getClass(), "");
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (roundEnv.processingOver())
            return false;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final String generated = "@javax.annotation.Generated(value=\"" + getClass().getName() + "\",date=\"" + sdf.format(new Date()) + "\")";

        try {
            final Template template = fmc.getTemplate("sgetter.ftl");

            final Elements elms = processingEnv.getElementUtils();
            final Filer filer = processingEnv.getFiler();
            for(TypeElement a : annotations)
                for(TypeElement e : typesIn(roundEnv.getElementsAnnotatedWith(a)))
                {
                    final PackageElement pkg = elms.getPackageOf(e);

                    final TypeElement parent = parent(e);
                    final String name = e.getSimpleName().toString();
                    final String gsname = name + CLASS_SUFFIX;

                    processingEnv.getMessager().printMessage(
                            Kind.NOTE, "Generating " + gsname );

                    final HashMap<String,ExecutableElement> setters
                        = new HashMap<String,ExecutableElement>();
                    for(ExecutableElement m : methodsIn(e.getEnclosedElements()))
                    {
                        String mname = m.getSimpleName().toString();
                        if (mname.startsWith("set"))
                            setters.put(uncapFirst(mname, 3), m);
                    }

                    final HashMap<String,ExecutableElement> getters
                        = new HashMap<String,ExecutableElement>();
                    final ArrayList<ExecutableElement> getterMethods
                        = new ArrayList<ExecutableElement>();
                    for(ExecutableElement m : methodsIn(e.getEnclosedElements()))
                    {
                        String mname = m.getSimpleName().toString();
                        if (!m.getParameters().isEmpty())
                            continue;
                        if (m.getModifiers().contains(Modifier.ABSTRACT))
                            continue;
                        if (mname.startsWith("get"))
                            mname = uncapFirst(mname, 3);
                        else if (mname.startsWith("is"))
                            mname = uncapFirst(mname, 2);
                        else
                            continue;
                        getters.put(mname, m);

                        getterMethods.add(m);
                    }

                    JavaFileObject file =
                        filer.createSourceFile(pkg.getQualifiedName() + "." + gsname, e);

                    HashMap<String,Object> params = new HashMap<String,Object>();
                    params.put("package", pkg.getQualifiedName());
                    params.put("class", name);
                    if (parent != null)
                        params.put("parentClass", parent.getQualifiedName().toString());
                    params.put("classSuffix", CLASS_SUFFIX);
                    params.put("generated", generated);
                    params.put("e", e);
                    params.put("methods", methodsIn(e.getEnclosedElements()));
                    params.put("getterMethods", getterMethods);
                    params.put("settersMap", setters);
                    params.put("lensClass", new LensAbstractClass());

                    final Writer w = file.openWriter();
                    template.process(params, w);
                    w.close();
                }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (TemplateException ex) {
            throw new RuntimeException(ex);
        }
        return false;
    }

    /*
    protected TypeMirror isSetter(ExecutableElement e)
    {
        final List<? extends VariableElement> args = e.getParameters();
        if (args.size() != 1)
            return null;

        if (e.get
    }
    */

    protected static String uncapFirst(String s, int offset)
    {
        if (s.length() <= offset)
            return s;
        return Character.toLowerCase(s.charAt(offset)) + s.substring(offset + 1);
    }
 
    // not used atm
    protected static Object getAnnotationValue(Element m, Class<?> clazz)
    {
        final String cname = clazz.getName();
        for(AnnotationMirror am : m.getAnnotationMirrors())
        {
            if (am.getAnnotationType().toString().equals(cname))
            {
                for(AnnotationValue av : am.getElementValues().values())
                    return av.getValue();
            }
        }
        return null;
    }

    protected static boolean hasAnnotation(TypeElement m, Class<?> clazz) {
        final String cname = clazz.getName();
        for(AnnotationMirror am : m.getAnnotationMirrors())
            if (am.getAnnotationType().toString().equals(cname))
                return true;
        return false;
    }

    protected TypeElement parent(TypeElement m) {
        if (m == null)
            return null;
        final TypeMirror tm = m.getSuperclass();
        final Element e = (tm != null) ? processingEnv.getTypeUtils().asElement(tm) : null;
        if (e == null)
            return null;
        return e.accept(
                new SimpleElementVisitor6<TypeElement,Void>() {
                    @Override public TypeElement visitType(TypeElement e, Void p) {
                        return e;
                    }
                }, null );
    }

    // TODO Caching?
    protected Element annotated(TypeElement m, Class<? extends Annotation> clazz) {
        for(; m != null; m = parent(m)) {
            if (hasAnnotation(m, clazz))
                return m;
        }
        return null;
    }

    public final class LensAbstractClass
            implements TemplateHashModel
        {
            public LensAbstractClass() {
            }
            @Override
            public TemplateModel get(String key) {
                final Element parent =
                    annotated(processingEnv.getElementUtils().getTypeElement(key), ANNOTATION_CLASS);
                return (parent != null) ? new SimpleScalar(parent.toString())
                                        : TemplateModel.NOTHING;
            }
            @Override
            public boolean isEmpty() {
                return false;
            }
        }
}
