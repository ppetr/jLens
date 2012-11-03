package cz.cvut.felk.cyber.jlens.processor;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
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


/**
 * TODO: Options - class suffix.
 */
@SupportedSourceVersion(javax.lang.model.SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("cz.cvut.felk.cyber.jlens.Lens")
@SupportedOptions({})
public class JLensProcessor
    extends AbstractProcessor
{
    //private final org.slf4j.Logger log =
    //    org.slf4j.LoggerFactory.getLogger(JLensProcessor.class);

    public static final String CLASS_SUFFIX = "_L";


 
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

            final HashMap<String,TypeElement> entityClassNames
                = new HashMap<String,TypeElement>();
            for(TypeElement a : annotations)
                for(TypeElement e : typesIn(roundEnv.getElementsAnnotatedWith(a)))
                    entityClassNames.put(e.getQualifiedName().toString(), e);

            final Elements elms = processingEnv.getElementUtils();
            final Filer filer = processingEnv.getFiler();
            for(TypeElement a : annotations)
                for(TypeElement e : typesIn(roundEnv.getElementsAnnotatedWith(a)))
                {
                    final PackageElement pkg = elms.getPackageOf(e);

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
                    final ArrayList<ExecutableElement> getterEntityMethods
                        = new ArrayList<ExecutableElement>();
                    final HashSet<String> privacyAttrs
                        = new HashSet<String>();
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

                        if (entityClassNames.containsKey(m.getReturnType().toString()))
                            getterEntityMethods.add(m);
                        else
                            getterMethods.add(m);

//                        final String pa = getPrivacyAttribute(m);
  //                      if (pa != null)
    //                        privacyAttrs.add(pa);
                    }

                    /*
                    String privacyClass = "java.lang.Object";
                    for(Map.Entry<String,ExecutableElement> entry : getters.entrySet())
                        if (privacyAttrs.contains(entry.getKey()))
                            privacyClass = entry.getValue().getReturnType().toString();
                            */
                 //   String privacyClass = getPrivacyClass(pkg, Object.class);

                    JavaFileObject file =
                        filer.createSourceFile(pkg.getQualifiedName() + "." + gsname, e);

                    HashMap<String,Object> params = new HashMap<String,Object>();
                    params.put("package", pkg.getQualifiedName());
                    params.put("class", name);
                    params.put("classSuffix", CLASS_SUFFIX);
                    params.put("generated", generated);
                    params.put("e", e);
                    params.put("methods", methodsIn(e.getEnclosedElements()));
                    params.put("getterMethods", getterMethods);
                    params.put("settersMap", setters);
                    params.put("getterEntityMethods", getterEntityMethods);
                    params.put("entityClassNames", entityClassNames);
                   // params.put("privacyAttrs", privacyAttrs);
                   // params.put("privacyClass", privacyClass);

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
 
    protected static Object getAnnotation(Element m, Class<?> clazz)
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

 
}
