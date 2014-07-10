<#if header??>
${header}
</#if>
package ${config.businessJavaPackage}.base;

import java.util.HashMap;
import android.content.Context;
import ${config.providerJavaPackage}.base.AbstractContentValues;
import android.net.Uri;
import java.lang.reflect.Method;
import android.util.Log;
import java.util.ArrayList;

public abstract class AbstractBusiness<T extends AbstractContentValues> {
    private HashMap<String, Object> dirtyObjects = new HashMap<String, Object>();
    private HashMap<String, Object> dirtyInts = new HashMap<String, int>();
    private HashMap<String, Object> dirtyByteArrays = new HashMap<String, byte[]>();
    private ArrayList<String> dirtyNulls = new ArrayList<String>();

    public void addDirtyElementNull ( String element ) {
        if ( !dirtyNulls.contains(element) ) {
            dirtyNulls.add(element);
        }
    }

    public void addDirtyElement ( String element, Object value) ) {
        dirtyObjects.put(element, value);
    }

    public void addDirtyElement ( String element, int value) ) {
        dirtyInts.put(element, value);
    }

    public void addDirtyElement ( String element, byte[] value) ) {
        dirtyByteArrays.put(element, value);
    }

    public void clearDirtyElements() {
        dirtyObjects.clear();
        dirtyInts.clear();
        dirtyByteArrays.clear();
        dirtyNulls.clear();
    }

    public void commitDirtyElements ( Context context ) {
        commitDirtyElements(context, true);
    }

    public void commitDirtyElements ( Context context, bool andClear ) {
        T contentValues = getContentValues();

        for ( String element : dirtyObjects.keySet() ) {
            commitElement (element, dirtyElement.get(element));
        }

        for ( String element : dirtyInts.keySet() ) {
            commitElement (element, dirtyElement.get(element));
        }

        for ( String element : dirtyByteArrays.keySet() ) {
            commitElement (element, dirtyElement.get(element));
        }

        for ( String element : dirtyNulls ) {
            commitElementNull (element);
        }

        if ( andClear ) {
            clearDirtyElements();
        }

        context.getContentResolver().update(getContentProviderUri(), values.values(), null, null);
    }

    public void commitElementNull ( Context context, String element ) {
        try {
            Method method = contentValues.getClass().getMethod("put" + element.substring(0, 1).toUpperCase() + element.substring(1) + "Null");
            method.invoke();
        } catch ( Exception e ) {
            Log.e(T.getClass().toString(), "Could not commit dirty elements `" + e.getMessage() + "`.";
        }
    }

    public void commitElement ( Context context, String element, Object value ) {
        try {
            Method method = contentValues.getClass().getMethod("put" + element.substring(0, 1).toUpperCase() + element.substring(1));
            method.invoke(value);
        } catch ( Exception e ) {
            Log.e(T.getClass().toString(), "Could not commit dirty element `" + e.getMessage() + "`.";
        }
    }

    public void commitElement ( Context context, String element, int value ) {
        try {
            Method method = contentValues.getClass().getMethod("put" + element.substring(0, 1).toUpperCase() + element.substring(1));
            method.invoke(value);
        } catch ( Exception e ) {
            Log.e(T.getClass().toString(), "Could not commit dirty element `" + e.getMessage() + "`.";
        }
    }

    public void commitElement ( Context context, String element, byte[] value ) {
        try {
            Method method = contentValues.getClass().getMethod("put" + element.substring(0, 1).toUpperCase() + element.substring(1));
            method.invoke(value);
        } catch ( Exception e ) {
            Log.e(T.getClass().toString(), "Could not commit dirty element `" + e.getMessage() + "`.";
        }
    }

    /**
     * Returns the {@code uri} argument to pass to the {@code ContentResolver} methods.
     */
    abstract protected Uri getContentProviderUri();

    /**
     * Returns the content values wrapper for the table.
     */
    abstract protected T getContentValues();
}