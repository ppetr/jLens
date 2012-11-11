<#--
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
-->
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
      <#case "int">     <#return "Integer" /> <#break>
      <#case "char">    <#return "Character" /> <#break>
	  <#-- All other primitive data types can be simply constructed by
		   upper-casing the first letter. -->
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

/* Parent: ${parentClass} */
<#assign lensParentClass = lensClass[parentClass]! />
/* Parent lens class: ${lensParentClass!} */

${generated}
public final class ${theclass}
{
	private ${theclass}() { }

	public static final ${theclass}.Get<${class},${class},Getter<${class},${class}>> ID
		= new ${theclass}.Get<${class},${class},Getter<${class},${class}>>(new Lenses.Identity<${class}>(${class}.class), ${class}.class) {
			@Override public ${class} get(${class} target) {
			  return target;
			}
		  };


	public static abstract class Get<R,F extends ${class},G extends Getter<R,?>>
	<#if lensParentClass?has_content >
		extends ${gsclass(lensParentClass)}.Get<R,F,G>
	<#else>
		extends WrappedGetter<R,F,G>
	</#if>
	{
		public Get(G getter, Class<F> fieldClass) {
			super(getter, fieldClass);
		}

		public abstract F get(R target);

		<#list getterMethods as m>
		  <#assign r = objclass(m.returnType) />
		  <#assign n = attrOf(m.simpleName) />
		  <#assign lc = lensClass[r] />
			// ${n} ________________________________________
		  <#if !( lc?has_content )>
			<#if settersMap[n]??>
				public AbstractLens<R,${r}> ${n}() {
				  return new AbstractLens<R,${r}>(${theclass}.Get.this.recordClass(), ${r}.class) {
					@Override public ${r} get(R target) {
					  return ${theclass}.Get.this.get(target).${m.simpleName}();
					}
					@Override public void set(R target, ${r} value) {
					  ${theclass}.Get.this.get(target).${settersMap[n].simpleName}(value);
					}
				  };
				};
			<#else>
				public AbstractGetter<R,${r}> ${n}() {
				  return new AbstractGetter<R,${r}>(${theclass}.Get.this.recordClass(), ${r}.class) {
					@Override public ${r} get(R target) {
					  return ${theclass}.Get.this.get(target).${m.simpleName}();
					}
				  };
				}
			</#if>
		  <#else>
			<#if settersMap[n]??>
				public static class Class_${n}<S,F extends ${class}>
					extends ${gsclass(lc)}.Get<S,${r},${theclass}.Get<S,F,?>>
					implements Lens<S,${r}>
				{
					Class_${n}(${theclass}.Get<S,F,?> p) {
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
					extends ${gsclass(lc)}.Get<S,${r},${theclass}.Get<S,F,?>>
				{
					Class_${n}(${theclass}.Get<S,F,?> p) {
						super(p, ${r}.class);
					}

					@Override public ${r} get(S target) {
					  return getter.get(target).${m.simpleName}();
					}
				}
			</#if>
				public Class_${n}<R,F> ${n}() {
				  return new Class_${n}<R,F>(this);
				}
		  </#if>
		</#list>
	}

<#list getterMethods as m>
  <#assign r = objclass(m.returnType) />
  <#assign n = attrOf(m.simpleName) />
  <#assign lc = lensClass[r] />
    // ${n} ________________________________________
  <#if !( lc?has_content )>
    <#if settersMap[n]??>
        public static final AbstractLens<${class},${r}> ${n} = ID.${n}();
    <#else>
        public static final AbstractGetter<${class},${r}> ${n} = ID.${n}();
    </#if>
  <#else>
        public static final Get.Class_${n}<${class},${class}> ${n} = ID.${n}();
  </#if>
</#list>
}
