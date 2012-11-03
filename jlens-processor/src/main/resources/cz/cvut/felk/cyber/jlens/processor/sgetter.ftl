package ${package};

import cz.cvut.felk.cyber.jlens.*;

<#assign keywords = {
"abstract":true, "continue":true, "for":true, "new":true, "switch":true,
"assert":true, "default":true, "goto":true, "package":true, "synchronized":true,
"boolean":true, "do":true, "if":true, "private":true, "this":true,
"break":true, "double":true, "implements":true, "protected":true, "throw":true,
"byte":true, "else":true, "import":true, "public":true, "throws":true,
"case":true, "enum":true, "instanceof":true, "return":true, "transient":true,
"catch":true, "extends":true, "int":true, "short":true, "try":true,
"char":true, "final":true, "interface":true, "static":true, "void":true,
"class":true, "finally":true, "long":true, "strictfp":true, "volatile":true,
"const":true, "float":true, "native":true, "super":true, "while":true
}>

<#function gsclass class>
  <#return class + classSuffix />
</#function>
<#function objclass class>
  <#if (class?index_of(".") lt 0) && (class?index_of("[") lt 0)>
    <#switch class>
      <#case "int">
        <#return "Integer" />
        <#break>
      <#default>
        <#return class?cap_first />
    </#switch>
  </#if>
  <#return class/>
</#function>

<#function attrOf m>
  <#if m?starts_with("get")>
    <#local result = m?substring(3) />
  <#elseif m?starts_with("is")>
    <#local result = m?substring(2) />
  <#else>
    <#local result = m />
  </#if>

  <#local result = result?uncap_first />
  <#if keywords[result]?? >
    <#return result + "_"/>
  <#else>
    <#return result />
  </#if>
</#function>

<#assign theclass = gsclass(class) />

${generated}
public abstract class ${theclass}<R,F extends ${class},G extends IGetter<R,?>>
<#assign parent = entityClassNames["${e.superclass}"]! />
<#if parent?has_content>
    extends ${gsclass(parent)}<R,F,G>
<#else>
    extends WrappedGetter<R,F,G>
</#if>
{
    public static final ${theclass}<${class},${class},IGetter<${class},${class}>> INSTANCE
        = new ${theclass}<${class},${class},IGetter<${class},${class}>>(new IGetters.Identity<${class}>(${class}.class), ${class}.class) {
            @Override public ${class} get(${class} target) {
              return target;
            }
          };

    public ${theclass}(G getter, Class<F> fieldClass) {
        super(getter, fieldClass);
    }

    public abstract F get(R target);

<#list getterMethods as m>
    <#assign r = objclass(m.returnType) />
    <#assign n = attrOf(m.simpleName) />
    <#if settersMap[n]??>
        public static final AbstractSetter<${class},${r}> ${n} = INSTANCE.${n}(${class}.class);
        public AbstractSetter<R,${r}> ${n}(Class<R> recordClass) {
          return new AbstractSetter<R,${r}>(recordClass, ${r}.class) {
            @Override public ${r} get(R target) {
              return ${theclass}.this.get(target).${m.simpleName}();
            }
            @Override public void set(R target, ${r} value) {
              ${theclass}.this.get(target).${settersMap[n].simpleName}(value);
            }
          };
        };
    <#else>
        public static final AbstractGetter<${class},${r}> ${n} = INSTANCE.${n}(${class}.class);
        public AbstractGetter<R,${r}> ${n}(Class<R> recordClass) {
          return new AbstractGetter<R,${r}>(recordClass, ${r}.class) {
            @Override public ${r} get(R target) {
              return ${theclass}.this.get(target).${m.simpleName}();
            }
          };
        }
    </#if>
</#list>

<#list getterEntityMethods as m>
    <#assign r = objclass(m.returnType) />
    <#assign n = attrOf(m.simpleName) />
  <#-- <#if n != "roleType"> -->
    <#if settersMap[n]??>
        public static class Class_${n}<S,F extends ${class}>
            extends ${gsclass(r)}<S,${r},${theclass}<S,F,?>>
            implements ISetter<S,${r}>
        {
            Class_${n}(${theclass}<S,F,?> p) {
                super(p, ${r}.class);
            }

            @Override public ${r} get(S target) {
              return getter.get(target).${m.simpleName}();
            }
            @Override public void set(S target, ${r} value) {
              getter.get(target).${settersMap[n].simpleName}(value);
            }
        }
    <#else>
        public static class Class_${n}<S,F extends ${class}>
            extends ${gsclass(r)}<S,${r},${theclass}<S,F,?>>
        {
            Class_${n}(${theclass}<S,F,?> p) {
                super(p, ${r}.class);
            }

            @Override public ${r} get(S target) {
              return getter.get(target).${m.simpleName}();
            }
        }
    </#if>
        public static final Class_${n}<${class},${class}> ${n} = INSTANCE.${n}();
        public Class_${n}<R,F> ${n}() {
          return new Class_${n}<R,F>(this);
        }
<#-- </#if> -->
</#list>
}
