<#if header??>
${header}
</#if>
package ${config.apiJavaPackage};

import retrofit.Callback;
import retrofit.http.*;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

<#list imports as import>
import ${import};
</#list>

public interface ${config.apiServiceInterfaceName} {
    <#list entities as entity>
    <#if entity.apiEndpoints?has_content>
    /**
     * ${entity.nameUpperCase}
     */
    <#list entity.apiEndpoints as apiEndpoint>

    // ${apiEndpoint.description}

    <#if apiEndpoint.formUrlEncoded>
    @FormUrlEncoded
    </#if>
    <#if apiEndpoint.multipart>
    @Multipart
    </#if>
    @${apiEndpoint.method}("${apiEndpoint.endpoint}")
    ${apiEndpoint.returnType.classNameWithGeneric} ${apiEndpoint.name}(<#list apiEndpoint.parameters as parameter>@${parameter.placement}("${parameter.nameUnderscoreCase}") ${parameter.type.classNameWithGeneric} ${parameter.name}<#if parameter_has_next>, </#if></#list>);

    <#if apiEndpoint.formUrlEncoded>
    @FormUrlEncoded
    </#if>
    <#if apiEndpoint.multipart>
    @Multipart
    </#if>
    @${apiEndpoint.method}("${apiEndpoint.endpoint}")
    void ${apiEndpoint.name}ASync(<#list apiEndpoint.parameters as parameter>@${parameter.placement}("${parameter.nameUnderscoreCase}") ${parameter.type.classNameWithGeneric} ${parameter.name}, </#list>Callback<${apiEndpoint.returnType.classNameWithGeneric}> callback);
    </#list>
    </#if>
    <#if entity_has_next>

    </#if>
    </#list>
}