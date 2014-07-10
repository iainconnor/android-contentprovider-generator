<#if header??>
${header}
</#if>
package ${config.providerJavaPackage};

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import ${config.projectPackageId}.BuildConfig;
<#list model.entities as entity>
import ${config.providerJavaPackage}.${entity.packageName}.${entity.nameCamelCase}Columns;
</#list>

public class ${config.sqliteOpenHelperClassName} extends SQLiteOpenHelper {
    private static final String TAG = ${config.sqliteOpenHelperClassName}.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "${config.databaseFileName}";
    private static final int DATABASE_VERSION = ${config.databaseVersion};
    private final Context context;
    private final ${config.sqliteOpenHelperCallbacksClassName} openHelperCallbacks;

    <#list model.entities as entity>
    private static final String SQL_CREATE_TABLE_${entity.nameUpperCase} = "CREATE TABLE IF NOT EXISTS "
            + ${entity.nameCamelCase}Columns.TABLE_NAME + " ( "
            + ${entity.nameCamelCase}Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            <#list entity.fields as field>
                <#if field.isNullable>
                    <#if field.hasDefaultValue>
            + ${entity.nameCamelCase}Columns.${field.nameUpperCase} + " ${field.type.sqlType} DEFAULT '${field.defaultValue}'<#if field_has_next>,</#if> "
                    <#else>
            + ${entity.nameCamelCase}Columns.${field.nameUpperCase} + " ${field.type.sqlType}<#if field_has_next>,</#if> "
                    </#if>
                <#else>
                    <#if field.hasDefaultValue>
            + ${entity.nameCamelCase}Columns.${field.nameUpperCase} + " ${field.type.sqlType} NOT NULL DEFAULT '${field.defaultValue}'<#if field_has_next>,</#if> "
                    <#else>
            + ${entity.nameCamelCase}Columns.${field.nameUpperCase} + " ${field.type.sqlType} NOT NULL<#if field_has_next>,</#if> "
                    </#if>
                </#if>
            </#list>
            <#list entity.constraints as constraint>
            + ", CONSTRAINT ${constraint.nameUpperCase} ${constraint.definitionUpperCase}"
            </#list>
            + " );";

    <#list entity.fields as field>
    <#if field.isIndex>
    private static final String SQL_CREATE_INDEX_${entity.nameUpperCase}_${field.nameUpperCase} = "CREATE INDEX IDX_${entity.nameUpperCase}_${field.nameUpperCase} "
            + " ON " + ${entity.nameCamelCase}Columns.TABLE_NAME + " ( " + ${entity.nameCamelCase}Columns.${field.nameUpperCase} + " );";
    </#if>
    </#list>
    </#list>

    public static ${config.sqliteOpenHelperClassName} newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */

    private static ${config.sqliteOpenHelperClassName} newInstancePreHoneycomb(Context context) {
        return new ${config.sqliteOpenHelperClassName}(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    private ${config.sqliteOpenHelperClassName}(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        context = context;
        openHelperCallbacks = new ${config.sqliteOpenHelperCallbacksClassName}();
    }


    /*
     * Post Honeycomb.
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static ${config.sqliteOpenHelperClassName} newInstancePostHoneycomb(Context context) {
        return new ${config.sqliteOpenHelperClassName}(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private ${config.sqliteOpenHelperClassName}(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        context = context;
        openHelperCallbacks = new ${config.sqliteOpenHelperCallbacksClassName}();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        openHelperCallbacks.onPreCreate(context, db);
        <#list model.entities as entity>
        db.execSQL(SQL_CREATE_TABLE_${entity.nameUpperCase});
        <#list entity.fields as field>
        <#if field.isIndex>
        db.execSQL(SQL_CREATE_INDEX_${entity.nameUpperCase}_${field.nameUpperCase});
        </#if>
        </#list>
        </#list>
        openHelperCallbacks.onPostCreate(context, db);
    }

    <#if config.enableForeignKeys >
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
        openHelperCallbacks.onOpen(context, db);
    }
    </#if>

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        openHelperCallbacks.onUpgrade(context, db, oldVersion, newVersion);
    }
}
