/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2012-2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.androidcontentprovidergenerator.model;

import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Field {
	public static class Json {
		public static final String NAME = "name";
		public static final String TYPE = "type";
		public static final String INDEX = "index";
		public static final String NULLABLE = "nullable";
		public static final String DEFAULT_VALUE = "defaultValue";
		public static final String ENUM_NAME = "enumName";
		public static final String ENUM_VALUES = "enumValues";
	}

	private final String name;
	private final Type type;
	private final boolean isIndex;
	private final boolean isNullable;
	private final String defaultValue;
	private final String enumName;
	private final List<EnumValue> enumValues = new ArrayList<EnumValue>();

	public Field ( String name, String type, boolean isIndex, boolean isNullable, String defaultValue, String enumName, List<EnumValue> enumValues ) {
		this.name = name;
		this.isIndex = isIndex;
		this.isNullable = isNullable;
		this.defaultValue = defaultValue;
		this.enumName = enumName;
		this.enumValues.addAll(enumValues);

		this.type = Type.fromJsonName(type, enumName);
	}

	public String getNameUpperCase () {
		return name.toUpperCase(Locale.US);
	}

	public String getNameLowerCase () {
		return name.toLowerCase(Locale.US);
	}

	public String getNameCamelCase () {
		return WordUtils.capitalizeFully(name, new char[] {'_'}).replaceAll("_", "");
	}

	public String getNameCamelCaseLowerCase () {
		return WordUtils.uncapitalize(getNameCamelCase());
	}

	public String getEnumName () {
		return enumName;
	}

	public List<EnumValue> getEnumValues () {
		return enumValues;
	}

	public Type getType () {
		return type;
	}

	public boolean getIsIndex () {
		return isIndex;
	}

	public boolean getIsNullable () {
		return isNullable;
	}

	public String getDefaultValue () {
		return defaultValue;
	}

	public boolean getHasDefaultValue () {
		return defaultValue != null && defaultValue.length() > 0;
	}

	public String getJavaTypeSimpleName () {
		if (type.getJsonName().equals(Type.Json.TYPE_ENUM)) {
			return enumName;
		}
		if (isNullable) {
			return type.getNullableJavaType().getSimpleName();
		}
		return type.getNotNullableJavaType().getSimpleName();
	}

	public boolean getIsConvertionNeeded () {
		return !isNullable && type.hasNotNullableJavaType();
	}

	public boolean isEnum () {
		return type.getJsonName().equals(Type.Json.TYPE_ENUM);
	}

	@Override
	public String toString () {
		return "Field [name=" + name + ", type=" + type + ", isIndex=" + isIndex + ", isNullable=" + isNullable + ", defaultValue=" + defaultValue + ", enumName=" + enumName + ", enumValues=" + enumValues + "]";
	}
}
