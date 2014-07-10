package org.jraf.androidcontentprovidergenerator.model;

import org.jraf.androidcontentprovidergenerator.Log;
import org.jraf.androidcontentprovidergenerator.Main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Type {
	public static class Json {
		public static final String TYPE_STRING = "String";
		public static final String TYPE_INTEGER = "Integer";
		public static final String TYPE_LONG = "Long";
		public static final String TYPE_FLOAT = "Float";
		public static final String TYPE_DOUBLE = "Double";
		public static final String TYPE_BOOLEAN = "Boolean";
		public static final String TYPE_DATE = "Date";
		public static final String TYPE_BYTE_ARRAY = "byte[]";
		public static final String TYPE_ENUM = "enum";
		public static final String TYPE_ENTITY = "entity";
	}

	private String jsonName;
	private String sqlType;
	private String className;
	private Class<?> nullableJavaType;
	private Class<?> notNullableJavaType;

	private Type ( String jsonName, String className, String sqlType, Class<?> nullableJavaType, Class<?> notNullableJavaType ) {
		this.className = className;
		this.jsonName = jsonName;
		this.sqlType = sqlType;
		this.nullableJavaType = nullableJavaType;
		this.notNullableJavaType = notNullableJavaType;
	}

	public static Type fromJsonName ( String jsonName ) {
		return fromJsonName(jsonName, "ABC");
	}

	public static Type fromJsonName ( String jsonName, String enumName ) {
		if ( jsonName.equals(Json.TYPE_STRING) ) {
			return new Type(Json.TYPE_STRING, String.class.getCanonicalName(), "TEXT", String.class, String.class);
		} else if ( jsonName.equals(Json.TYPE_INTEGER) ) {
			return new Type(Json.TYPE_INTEGER, Integer.class.getCanonicalName(), "INTEGER", Integer.class, int.class);
		} else if ( jsonName.equals(Json.TYPE_LONG) ) {
			return new Type(Json.TYPE_LONG, Long.class.getCanonicalName(), "INTEGER", Long.class, long.class);
		} else if ( jsonName.equals(Json.TYPE_FLOAT) ) {
			return new Type(Json.TYPE_FLOAT, Float.class.getCanonicalName(), "REAL", Float.class, float.class);
		} else if ( jsonName.equals(Json.TYPE_DOUBLE) ) {
			return new Type(Json.TYPE_DOUBLE, Double.class.getCanonicalName(), "REAL", Double.class, double.class);
		} else if ( jsonName.equals(Json.TYPE_BOOLEAN) ) {
			return new Type(Json.TYPE_BOOLEAN, Boolean.class.getCanonicalName(), "INTEGER", Boolean.class, boolean.class);
		} else if ( jsonName.equals(Json.TYPE_DATE) ) {
			return new Type(Json.TYPE_DATE, Date.class.getCanonicalName(), "INTEGER", Date.class, Date.class);
		} else if ( jsonName.equals(Json.TYPE_BYTE_ARRAY) ) {
			return new Type(Json.TYPE_BYTE_ARRAY, byte[].class.getCanonicalName(), "BLOB", byte[].class, byte[].class);
		} else if ( jsonName.equals(Json.TYPE_ENUM) ) {
			return new Type(Json.TYPE_ENUM, enumName, "INTEGER", null, null);
		} else {
			// Relationship with another entity or a custom parameter.
			return new Type(Json.TYPE_ENTITY, jsonName, "INTEGER", Integer.class, int.class);
		}
	}

	public String getSqlType () {
		return sqlType;
	}

	public Class<?> getNullableJavaType () {
		return nullableJavaType;
	}

	public Class<?> getNotNullableJavaType () {
		return notNullableJavaType;
	}

	public boolean hasNotNullableJavaType () {
		if (this.jsonName.equals(Json.TYPE_ENUM)) {
			return false;
		}

		return !nullableJavaType.equals(notNullableJavaType);
	}

	public String getJsonName () {
		return jsonName;
	}

	public ArrayList<String> getImports (String classesPackage, String enumPackage) {
		ArrayList<String> imports = new ArrayList<String>();

		String fqClassName = getFqClassName();
		String className = getClassName();

		if ( fqClassName != null && !fqClassName.contains(".") ) {
			if (jsonName.equals(Json.TYPE_ENUM)) {
				fqClassName = enumPackage + "." + fqClassName;
			} else if (jsonName.equals(Json.TYPE_ENTITY)) {
				fqClassName = classesPackage + "." + fqClassName;
			}
		}

		if (!fqClassName.equals(className) && requiresImport(fqClassName)) {
			imports.add(fqClassName);
		}

		String fqGenericClassName = getFqGenericClassName();
		String genericClassName = getGenericClassName();

		if ( fqGenericClassName != null && !fqGenericClassName.contains(".") ) {
			if (jsonName.equals(Json.TYPE_ENUM)) {
				fqGenericClassName = enumPackage + "." + fqGenericClassName;
			} else if (jsonName.equals(Json.TYPE_ENTITY)) {
				fqGenericClassName = classesPackage + "." + fqGenericClassName;
			}
		}

		if (fqGenericClassName != null && !fqClassName.equals(genericClassName) && requiresImport(fqGenericClassName)) {
			imports.add(fqGenericClassName);
		}

		return imports;
	}

	public boolean requiresImport ( String fqClassName ) {
		return !fqClassName.startsWith("java.lang.");
	}

	public String getFqClassName () {
		return className.contains("<") ? className.substring(0, className.lastIndexOf("<")) : className;
	}

	public String getFqGenericClassName () {
		String generic = null;

		if (className.contains("<") && className.contains(">")) {
			generic = className.substring(className.lastIndexOf("<") + 1, className.lastIndexOf(">"));
		}

		return generic;
	}

	public String getClassName () {
		String withoutGeneric = getFqClassName();
		return withoutGeneric.contains(".") ? withoutGeneric.substring(withoutGeneric.lastIndexOf(".") + 1) : withoutGeneric;
	}

	public String getGenericClassName () {
		String generic = getFqGenericClassName();

		if (generic != null) {
			generic = generic.contains(".") ? generic.substring(generic.lastIndexOf(".") + 1) : generic;
		}

		return generic;
	}

	public String getClassNameWithGeneric () {
		return getClassName() + (getGenericClassName() != null ? ("<" + getGenericClassName() + ">") : "");
	}
}
