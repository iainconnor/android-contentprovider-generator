<#if header??>
${header}
</#if>
package ${config.providerJavaPackage}.${entity.packageName};

/**
 * Possible values for the {@code ${field.nameLowerCase}} column of the {@code ${entity.nameLowerCase}} table.
 */
public enum ${field.enumName} {
    <#list field.enumValues as enumValue>
    <#if !enumValue?? && enumValue.javadoc != "">
    /**
     * ${enumValue.javadoc}
     */
    </#if>
    ${enumValue.name},
    </#list>
}