package org.jraf.androidcontentprovidergenerator.model;

import java.util.List;

public class ApiEndpoint {
	public static class Json {
		public static final String NAME = "name";
		public static final String ENDPOINT = "endpoint";
		public static final String METHOD = "method";
		public static final String PARAMETERS = "parameters";
		public static final String IS_FORM_URL_ENCODED = "isFormUrlEncoded";
		public static final String IS_MULTIPART = "isMultipart";
		public static final String RETURN_TYPE = "returnType";
		public static final String DESCRIPTION = "description";
	}

	private final String name;
	private final String endpoint;
	private final String method;
	private final List<ApiParam> parameters;
	private final boolean isFormUrlEncoded;
	private final boolean isMultipart;
	private final Type returnType;
	private final String description;

	public ApiEndpoint ( String name, String endpoint, String method, List<ApiParam> parameters, boolean isFormUrlEncoded, boolean isMultipart, Type returnType, String description ) {
		this.name = name;
		this.endpoint = endpoint;
		this.method = method;
		this.parameters = parameters;
		this.isFormUrlEncoded = isFormUrlEncoded;
		this.isMultipart = isMultipart;
		this.returnType = returnType;
		this.description = description;
	}

	public String getName () {
		return name;
	}

	public String getEndpoint () {
		return endpoint;
	}

	public String getMethod () {
		return method;
	}

	public List<ApiParam> getParameters () {
		return parameters;
	}

	public boolean getFormUrlEncoded () {
		return isFormUrlEncoded;
	}

	public boolean getMultipart () {
		return isMultipart;
	}

	public Type getReturnType () {
		return returnType;
	}

	public String getDescription () {
		return description;
	}
}
