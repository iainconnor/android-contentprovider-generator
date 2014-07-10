package org.jraf.androidcontentprovidergenerator.model;

import com.google.common.base.CaseFormat;

public class ApiParam {
	public static class Json {
		public static final String NAME = "name";
		public static final String TYPE = "type";
		public static final String PLACEMENT = "placement";
	}

	private final String name;
	private final Type type;
	private final String placement;

	public ApiParam ( String name, Type type, String placement ) {
		this.name = name;
		this.type = type;
		this.placement = placement;
	}

	public String getName () {
		return name;
	}

	public String getNameUnderscoreCase() {
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
	}

	public Type getType () {
		return type;
	}

	public String getPlacement () {
		return placement;
	}
}
