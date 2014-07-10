<#if header??>
${header}
</#if>
package ${config.providerJavaPackage}.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.net.Uri;

public abstract class AbstractSelection <T extends AbstractSelection<?>> {
    private static final String EQ = "=?";
    private static final String PAREN_OPEN = "(";
    private static final String PAREN_CLOSE = ")";
    private static final String AND = " and ";
    private static final String OR = " or ";
    private static final String IS_NULL = " is null";
    private static final String IS_NOT_NULL = " is not null";
    private static final String IN = " in(";
    private static final String NOT_IN = " not in(";
    private static final String COMMA = ",";
    private static final String GT = ">?";
    private static final String LT = "<?";
    private static final String GT_EQ = ">=?";
    private static final String LT_EQ = "<=?";
    private static final String NOT_EQ = "<>?";
    private static final String LIKE = " LIKE ?";

    private StringBuilder selection = new StringBuilder();
    private List<String> selectionArgs = new ArrayList<String>(5);

    protected void addEquals(String column, Object[] value) {
        selection.append(column);

        if (value == null) {
            // Single null value
            selection.append(IS_NULL);
        } else if (value.length > 1) {
            // Multiple values ('in' clause)
            selection.append(IN);
            for (int i = 0; i < value.length; i++) {
                selection.append("?");
                if (i < value.length - 1) {
                    selection.append(COMMA);
                }
                selectionArgs.add(valueOf(value[i]));
            }
            selection.append(PAREN_CLOSE);
        } else {
            // Single value
            if (value[0] == null) {
                // Single null value
                selection.append(IS_NULL);
            } else {
                // Single not null value
                selection.append(EQ);
                selectionArgs.add(valueOf(value[0]));
            }
        }
    }

    protected void addNotEquals(String column, Object[] value) {
        selection.append(column);

        if (value == null) {
            // Single null value
            selection.append(IS_NOT_NULL);
        } else if (value.length > 1) {
            // Multiple values ('in' clause)
            selection.append(NOT_IN);
            for (int i = 0; i < value.length; i++) {
                selection.append("?");
                if (i < value.length - 1) {
                    selection.append(COMMA);
                }
                selectionArgs.add(valueOf(value[i]));
            }
            selection.append(PAREN_CLOSE);
        } else {
            // Single value
            if (value[0] == null) {
                // Single null value
                selection.append(IS_NOT_NULL);
            } else {
                // Single not null value
                selection.append(NOT_EQ);
                selectionArgs.add(valueOf(value[0]));
            }
        }
    }

    protected void addLike(String column, String[] values) {
        selection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            selection.append(column);
            selection.append(LIKE);
            selectionArgs.add(values[i]);
            if (i < values.length - 1) {
                selection.append(OR);
            }
        }
        selection.append(PAREN_CLOSE);
    }

    protected void addGreaterThan(String column, Object value) {
        selection.append(column);
        selection.append(GT);
        selectionArgs.add(valueOf(value));
    }

    protected void addGreaterThanOrEquals(String column, Object value) {
        selection.append(column);
        selection.append(GT_EQ);
        selectionArgs.add(valueOf(value));
    }

    protected void addLessThan(String column, Object value) {
        selection.append(column);
        selection.append(LT);
        selectionArgs.add(valueOf(value));
    }

    protected void addLessThanOrEquals(String column, Object value) {
        selection.append(column);
        selection.append(LT_EQ);
        selectionArgs.add(valueOf(value));
    }

    public void addRaw(String raw, Object... args) {
        selection.append(" ");
        selection.append(raw);
        selection.append(" ");
        for (Object arg : args) {
            selectionArgs.add(valueOf(arg));
        }
    }

    private String valueOf(Object obj) {
        if (obj instanceof Date) {
            return String.valueOf(((Date) obj).getTime());
        } else if (obj instanceof Boolean) {
            return (Boolean) obj ? "1" : "0";
        } else if (obj instanceof Enum) {
            return String.valueOf(((Enum<?>) obj).ordinal());
        }
        return String.valueOf(obj);
    }

    @SuppressWarnings("unchecked")
    public T openParen() {
        selection.append(PAREN_OPEN);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T closeParen() {
        selection.append(PAREN_CLOSE);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T and() {
        selection.append(AND);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T or() {
        selection.append(OR);
        return (T) this;
    }


    protected Object[] toObjectArray(int... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    protected Object[] toObjectArray(long... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    protected Object[] toObjectArray(float... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    protected Object[] toObjectArray(double... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    protected Object[] toObjectArray(Boolean value) {
        return new Object[] { value };
    }


    /**
     * Returns the selection produced by this object.
     */
    public String sel() {
        return selection.toString();
    }

    /**
     * Returns the selection arguments produced by this object.
     */
    public String[] args() {
        int size = selectionArgs.size();
        if (size == 0) return null;
        return selectionArgs.toArray(new String[size]);
    }


    /**
     * Returns the {@code uri} argument to pass to the {@code ContentResolver} methods.
     */
    public abstract Uri uri();

    /**
     * Deletes row(s) specified by this selection.
     *
     * @param contentResolver The content resolver to use.
     * @return The number of rows deleted.
     */
    public int delete(ContentResolver contentResolver) {
        return contentResolver.delete(uri(), sel(), args());
    }
}
