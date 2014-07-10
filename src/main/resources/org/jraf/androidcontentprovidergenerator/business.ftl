<#if header??>
${header}
</#if>
package ${config.businessJavaPackage};

import ${config.businessJavaPackage}.base.AbstractBusiness;
<#list entity.fields as field>
<#switch field.type.jsonName>
<#case "ENUM">
import ${config.providerJavaPackage}.${entity.packageName}.${field.enumName};
<#break>
</#switch>
</#list>
import ${config.providerJavaPackage}.base.AbstractContentValues;
import ${config.providerJavaPackage}.${entity.packageName}.${entity.nameCamelCase}ContentValues;
import android.net.Uri;
import ${config.providerJavaPackage}.${entity.packageName}.${entity.nameCamelCase}Columns;

import java.util.Date;
import java.util.HashMap;

public class ${entity.nameCamelCase} extends AbstractBusiness<${entity.nameCamelCase}ContentValues> {
    <#list entity.fields as field>
    <#switch field.type.jsonName>
    <#case "STRING">
    private String ${field.nameCamelCaseLowerCase};
    <#break>
    <#case "INTEGER">
    private Integer ${field.nameCamelCaseLowerCase};
    <#break>
    <#case "LONG">
    private Long ${field.nameCamelCaseLowerCase};
    <#break>
    <#case "FLOAT">
    private Float ${field.nameCamelCaseLowerCase};
    <#break>
    <#case "DOUBLE">
    private Double ${field.nameCamelCaseLowerCase};
    <#break>
    <#case "BOOLEAN">
    private Boolean ${field.nameCamelCaseLowerCase};
    <#break>
    <#case "DATE">
    private Boolean ${field.nameCamelCaseLowerCase};
    <#break>
    <#case "BYTE_ARRAY">
    private byte[] ${field.nameCamelCaseLowerCase};
    <#break>
    <#case "ENUM">
    private ${field.enumName} ${field.nameCamelCaseLowerCase};
    <#break>
    </#switch>
    </#list>

    public ${entity.nameCamelCase} ( <#rt>
        <#list entity.fields as field>
            <#switch field.type.jsonName>
                <#case "STRING">
                    String ${field.nameCamelCaseLowerCase}<#t>
                <#break>
                <#case "INTEGER">
                    Integer ${field.nameCamelCaseLowerCase}<#t>
                <#break>
                <#case "LONG">
                    Long ${field.nameCamelCaseLowerCase}<#t>
                <#break>
                <#case "FLOAT">
                    Float ${field.nameCamelCaseLowerCase}<#t>
                <#break>
                <#case "DOUBLE">
                    Double ${field.nameCamelCaseLowerCase}<#t>
                <#break>
                <#case "BOOLEAN">
                    Boolean ${field.nameCamelCaseLowerCase}<#t>
                <#break>
                <#case "DATE">
                    Boolean ${field.nameCamelCaseLowerCase}<#t>
                <#break>
                <#case "BYTE_ARRAY">
                    byte[] ${field.nameCamelCaseLowerCase}<#t>
                <#break>
                <#case "ENUM">
                    ${field.enumName} ${field.nameCamelCaseLowerCase}<#t>
                <#break>
            </#switch>
            <#if field_has_next>, </#if><#t>
        </#list><#lt> ) {
        <#list entity.fields as field>
        this.${field.nameCamelCaseLowerCase} = ${field.nameCamelCaseLowerCase};
        </#list>
    }

    /**
     * Returns the {@code uri} argument to pass to the {@code ContentResolver} methods.
     */
    protected Uri getContentProviderUri() {
        return ${entity.nameCamelCase}Columns.CONTENT_URI;
    }

    /**
     * Returns the content values wrapper for the {@code ${entity.nameCamelCase}} table.
     */
    protected ${entity.nameCamelCase}ContentValues getContentValues() {
        return new ${entity.nameCamelCase}ContentValues();
    }

    /**
     * In the below methods, {@code commitChange} determines whether the set is propagated to the
     * {@code ContentProvider} immediately.
     * If not, changes are queued until a call to {@code commitDirtyElements}.
     */

    <#list entity.fields as field>
    <#switch field.type.jsonName>
    <#case "STRING">
    public String get${field.nameCamelCase}() {
        return ${field.nameCamelCaseLowerCase};
    }

    public void set${field.nameCamelCase} ( String ${field.nameCamelCaseLowerCase} ) {
        set${field.nameCamelCase}, false);
    }

    public void set${field.nameCamelCase} ( String ${field.nameCamelCaseLowerCase}, bool commitChange ) {
        this.${field.nameCamelCaseLowerCase} = ${field.nameCamelCaseLowerCase};

        if ( commitChange ) {
            commitElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        } else {
            addDirtyElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        }
    }
    <#break>
    <#case "INTEGER">
    public Integer get${field.nameCamelCase}() {
        return ${field.nameCamelCaseLowerCase};
    }

    public void set${field.nameCamelCase} ( Integer ${field.nameCamelCaseLowerCase} ) {
            set${field.nameCamelCase}, false);
        }

        public void set${field.nameCamelCase} ( Integer ${field.nameCamelCaseLowerCase}, bool commitChange ) {
            this.${field.nameCamelCaseLowerCase} = ${field.nameCamelCaseLowerCase};

            if ( commitChange ) {
                commitElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
            } else {
                addDirtyElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
            }
        }
    <#break>
    <#case "LONG">
    public Long get${field.nameCamelCase}() {
        return ${field.nameCamelCaseLowerCase};
    }

    public void set${field.nameCamelCase} ( Long ${field.nameCamelCaseLowerCase} ) {
        set${field.nameCamelCase}, false);
    }

    public void set${field.nameCamelCase} ( Long ${field.nameCamelCaseLowerCase}, bool commitChange ) {
        this.${field.nameCamelCaseLowerCase} = ${field.nameCamelCaseLowerCase};

        if ( commitChange ) {
            commitElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        } else {
            addDirtyElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        }
    }
    <#break>
    <#case "FLOAT">
    public Float get${field.nameCamelCase}() {
        return ${field.nameCamelCaseLowerCase};
    }

    public void set${field.nameCamelCase} ( Float ${field.nameCamelCaseLowerCase} ) {
        set${field.nameCamelCase}, false);
    }

    public void set${field.nameCamelCase} ( Float ${field.nameCamelCaseLowerCase}, bool commitChange ) {
        this.${field.nameCamelCaseLowerCase} = ${field.nameCamelCaseLowerCase};

        if ( commitChange ) {
            commitElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        } else {
            addDirtyElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        }
    }
    <#break>
    <#case "DOUBLE">
    public Double get${field.nameCamelCase}() {
       return ${field.nameCamelCaseLowerCase};
    }

    public void set${field.nameCamelCase} ( Double ${field.nameCamelCaseLowerCase} ) {
        set${field.nameCamelCase}, false);
    }

    public void set${field.nameCamelCase} ( Double ${field.nameCamelCaseLowerCase}, bool commitChange ) {
        this.${field.nameCamelCaseLowerCase} = ${field.nameCamelCaseLowerCase};

        if ( commitChange ) {
            commitElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        } else {
            addDirtyElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        }
    }
    <#break>
    <#case "BOOLEAN">
    public Boolean get${field.nameCamelCase}() {
        return ${field.nameCamelCaseLowerCase};
    }

    public Boolean is{field.nameCamelCase}() {
        return get${field.nameCamelCaseLowerCase}();
    }

    public void set${field.nameCamelCase} ( Boolean ${field.nameCamelCaseLowerCase} ) {
        set${field.nameCamelCase}, false);
    }

    public void set${field.nameCamelCase} ( Boolean ${field.nameCamelCaseLowerCase}, bool commitChange ) {
        this.${field.nameCamelCaseLowerCase} = ${field.nameCamelCaseLowerCase};

        if ( commitChange ) {
            commitElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        } else {
            addDirtyElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        }
    }
    <#break>
    <#case "DATE">
    public Date get${field.nameCamelCase}() {
      return ${field.nameCamelCaseLowerCase};
    }

    public void set${field.nameCamelCase} ( Date ${field.nameCamelCaseLowerCase} ) {
        set${field.nameCamelCase}, false);
    }

    public void set${field.nameCamelCase} ( Date ${field.nameCamelCaseLowerCase}, bool commitChange ) {
        this.${field.nameCamelCaseLowerCase} = ${field.nameCamelCaseLowerCase};

        if ( commitChange ) {
            commitElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        } else {
            addDirtyElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        }
    }
    <#break>
    <#case "BYTE_ARRAY">
    public byte[] get${field.nameCamelCase}() {
       return ${field.nameCamelCaseLowerCase};
    }

    public void set${field.nameCamelCase} ( byte[] ${field.nameCamelCaseLowerCase} ) {
        set${field.nameCamelCase}, false);
    }

    public void set${field.nameCamelCase} ( byte[] ${field.nameCamelCaseLowerCase}, bool commitChange ) {
        this.${field.nameCamelCaseLowerCase} = ${field.nameCamelCaseLowerCase};

        if ( commitChange ) {
            commitElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        } else {
            addDirtyElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        }
    }
    <#break>
    <#case "ENUM">
    public ${field.enumName} get${field.nameCamelCase}() {
       return ${field.nameCamelCaseLowerCase};
    }

    public void set${field.nameCamelCase} ( ${field.enumName} ${field.nameCamelCaseLowerCase} ) {
        set${field.nameCamelCase}(false);
    }

    public void set${field.nameCamelCase} ( ${field.enumName} ${field.nameCamelCaseLowerCase}, bool commitChange ) {
        this.${field.nameCamelCaseLowerCase} = ${field.nameCamelCaseLowerCase};

        if ( commitChange ) {
            commitElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        } else {
            addDirtyElement("${field.nameCamelCaseLowerCase}", ${field.nameCamelCaseLowerCase});
        }
    }
    <#break>
    </#switch>
    <#if field_has_next>

    </#if>
    </#list>
}