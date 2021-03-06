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

package org.jraf.androidcontentprovidergenerator;

import com.beust.jcommander.JCommander;
import com.google.common.base.CaseFormat;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jraf.androidcontentprovidergenerator.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class Main {
	private static String TAG = Constants.TAG + Main.class.getSimpleName();

	private static String FILE_CONFIG = "_config.json";

	public static class Json {
		public static final String TOOL_VERSION = "toolVersion";
		public static final String PROJECT_PACKAGE_ID = "projectPackageId";
		public static final String PROVIDER_JAVA_PACKAGE = "providerJavaPackage";
		public static final String PROVIDER_CLASS_NAME = "providerClassName";
		public static final String BUSINESS_JAVA_PACKAGE = "businessJavaPackage";
		public static final String SQLITE_OPEN_HELPER_CLASS_NAME = "sqliteOpenHelperClassName";
		public static final String SQLITE_OPEN_HELPER_CALLBACKS_CLASS_NAME = "sqliteOpenHelperCallbacksClassName";
		public static final String AUTHORITY = "authority";
		public static final String DATABASE_FILE_NAME = "databaseFileName";
		public static final String DATABASE_VERSION = "databaseVersion";
		public static final String ENABLE_FOREIGN_KEY = "enableForeignKeys";
		public static final String API_BASE_URL = "apiBaseUrl";
		public static final String API_JAVA_PACKAGE = "apiJavaPackage";
		public static final String API_MODEL_JAVA_PACKAGE = "apiModelJavaPackage";
		public static final String API_SERVICE_INTERFACE_NAME = "apiServiceInterfaceName";
	}

	private Configuration mFreemarkerConfig;
	private JSONObject mConfig;

	private Configuration getFreeMarkerConfig () {
		if (mFreemarkerConfig == null) {
			mFreemarkerConfig = new Configuration();
			mFreemarkerConfig.setClassForTemplateLoading(getClass(), "");
			mFreemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
		}
		return mFreemarkerConfig;
	}

	private void loadModel ( File inputDir ) throws IOException, JSONException {
		JSONObject config = getConfig(inputDir);
		String apiModelJavaPackage = config.getString(Json.API_MODEL_JAVA_PACKAGE);

		File[] entityFiles = inputDir.listFiles(new FileFilter() {
			@Override
			public boolean accept ( File pathname ) {
				return !pathname.getName().startsWith("_") && pathname.getName().endsWith(".json");
			}
		});
		for (File entityFile : entityFiles) {
			if (Config.LOGD) {
				Log.d(TAG, entityFile.getCanonicalPath());
			}
			String entityName = FilenameUtils.getBaseName(entityFile.getCanonicalPath());
			if (Config.LOGD) {
				Log.d(TAG, "entityName=" + entityName);
			}
			Entity entity = new Entity(entityName);
			String fileContents = FileUtils.readFileToString(entityFile);
			JSONObject entityJson = new JSONObject(fileContents);

			// Fields
			JSONArray fieldsJson = entityJson.getJSONArray("fields");
			int len = fieldsJson.length();
			for (int i = 0; i < len; i++) {
				JSONObject fieldJson = fieldsJson.getJSONObject(i);
				if (Config.LOGD) {
					Log.d(TAG, "fieldJson=" + fieldJson);
				}
				String name = fieldJson.getString(Field.Json.NAME);
				String type = fieldJson.getString(Field.Json.TYPE);
				boolean isIndex = fieldJson.optBoolean(Field.Json.INDEX, false);
				boolean isNullable = fieldJson.optBoolean(Field.Json.NULLABLE, true);
				String defaultValue = fieldJson.optString(Field.Json.DEFAULT_VALUE);
				String enumName = fieldJson.optString(Field.Json.ENUM_NAME);
				JSONArray enumValuesJson = fieldJson.optJSONArray(Field.Json.ENUM_VALUES);
				List<EnumValue> enumValues = new ArrayList<EnumValue>();
				if (enumValuesJson != null) {
					int enumLen = enumValuesJson.length();
					for (int j = 0; j < enumLen; j++) {
						Object enumValue = enumValuesJson.get(j);
						if (enumValue instanceof String) {
							// Name only
							enumValues.add(new EnumValue((String) enumValue, null));
						} else {
							// Name and Javadoc
							JSONObject enumValueJson = (JSONObject) enumValue;
							String enumValueName = (String) enumValueJson.keys().next();
							String enumValueJavadoc = enumValueJson.getString(enumValueName);
							enumValues.add(new EnumValue(enumValueName, enumValueJavadoc));
						}
					}
				}
				Field field = new Field(name, type, isIndex, isNullable, defaultValue, enumName, enumValues);
				entity.addField(field);
			}

			// API
			JSONObject apiJson = entityJson.optJSONObject("api");
			if (apiJson != null) {
				// Automatic endpoints
				JSONArray autoApiEndpointsJson = apiJson.optJSONArray("autoEndpoints");
				if (autoApiEndpointsJson != null) {
					for (int i = 0; i < autoApiEndpointsJson.length(); i++) {
						String autoApiEndpointJson = autoApiEndpointsJson.getString(i);
						ApiParam idParam = new ApiParam("id", Type.fromJsonName("Integer"), "Path");

						if (autoApiEndpointJson.equals("index")) {
							entity.addApiMethod(new ApiEndpoint("get" + entity.getNameCamelCase() + "List", "/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entity.getNameCamelCase()), "GET", new ArrayList<ApiParam>(), false, false, Type.fromJsonName("java.util.List<" + entity.getNameCamelCase() + ">"), "Retrieves a list of " + entity.getNameCamelCase() + " models."));
						} else if (autoApiEndpointJson.equals("get")) {
							entity.addApiMethod(new ApiEndpoint("get" + entity.getNameCamelCase(), "/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entity.getNameCamelCase()) + "/{id}", "GET", new ArrayList<ApiParam>(Arrays.asList(idParam)), false, false, Type.fromJsonName(entity.getNameCamelCase()), "Retrieves an instance of a " + entity.getNameCamelCase() + " model."));
						} else if (autoApiEndpointJson.equals("post")) {
							ArrayList<ApiParam> postParams = new ArrayList<ApiParam>();

							// @TODO, not the right field
							for (Field field : entity.getFields()) {
								postParams.add(new ApiParam(field.getNameCamelCaseLowerCase(), field.getType(), "Field"));
							}

							entity.addApiMethod(new ApiEndpoint("post" + entity.getNameCamelCase(), "/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entity.getNameCamelCase()), "POST", postParams, true, false, Type.fromJsonName(entity.getNameCamelCase()), "Creates a new instance of a " + entity.getNameCamelCase() + " model."));
						} else if (autoApiEndpointJson.equals("put")) {
							ArrayList<ApiParam> putParams = new ArrayList<ApiParam>();
							putParams.add(idParam);

							// @TODO, not the right field
							for (Field field : entity.getFields()) {
								putParams.add(new ApiParam(field.getNameCamelCaseLowerCase(), field.getType(), "Field"));
							}

							entity.addApiMethod(new ApiEndpoint("put" + entity.getNameCamelCase(), "/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entity.getNameCamelCase()) + "/{id}", "POST", putParams, true, false, Type.fromJsonName(entity.getNameCamelCase()), "Updates an instance of a " + entity.getNameCamelCase() + " model."));
						} else if (autoApiEndpointJson.equals("delete")) {
							entity.addApiMethod(new ApiEndpoint("delete" + entity.getNameCamelCase(), "/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entity.getNameCamelCase()) + "/{id}/delete", "GET", new ArrayList<ApiParam>(Arrays.asList(idParam)), false, false, Type.fromJsonName(entity.getNameCamelCase()), "Deletes an instance of a " + entity.getNameCamelCase() + " model."));
						}
					}
				}

				// Specific endpoints
				JSONArray apiEndpointsJson = apiJson.optJSONArray("endpoints");
				if (apiEndpointsJson != null) {
					for (int i = 0; i < apiEndpointsJson.length(); i++) {
						JSONObject apiEndpointJson = apiEndpointsJson.getJSONObject(i);

						String name = apiEndpointJson.getString(ApiEndpoint.Json.NAME);
						String method = apiEndpointJson.getString(ApiEndpoint.Json.METHOD);
						String endpoint = apiEndpointJson.getString(ApiEndpoint.Json.ENDPOINT);
						String description = apiEndpointJson.getString(ApiEndpoint.Json.DESCRIPTION);
						boolean isFormUrlEncoded = apiEndpointJson.optBoolean(ApiEndpoint.Json.IS_FORM_URL_ENCODED, false);
						boolean isMultipart = apiEndpointJson.optBoolean(ApiEndpoint.Json.IS_MULTIPART, false);
						Type returnType = Type.fromJsonName(apiEndpointJson.optString(ApiEndpoint.Json.RETURN_TYPE, entity.getNameCamelCase()));

						List<ApiParam> params = new ArrayList<ApiParam>();
						JSONArray pathParamsJson = apiEndpointJson.optJSONArray(ApiEndpoint.Json.PARAMETERS);
						if (pathParamsJson != null) {
							for (int j = 0; j < pathParamsJson.length(); j++) {
								JSONObject pathParamJson = pathParamsJson.getJSONObject(j);
								String paramName = pathParamJson.getString(ApiParam.Json.NAME);
								Type paramType = Type.fromJsonName(pathParamJson.getString(ApiParam.Json.TYPE));
								String placement = pathParamJson.optString(ApiParam.Json.PLACEMENT, "Path");
								params.add(new ApiParam(paramName, paramType, placement));
							}
						}

						entity.addApiMethod(new ApiEndpoint(name, endpoint, method, params, isFormUrlEncoded, isMultipart, returnType, description));
					}
				}

				// API Fields
			}

			// Constraints (optional)
			JSONArray constraintsJson = entityJson.optJSONArray("constraints");
			if (constraintsJson != null) {
				len = constraintsJson.length();
				for (int i = 0; i < len; i++) {
					JSONObject constraintJson = constraintsJson.getJSONObject(i);
					if (Config.LOGD) {
						Log.d(TAG, "constraintJson=" + constraintJson);
					}
					String name = constraintJson.getString(Constraint.Json.NAME);
					String definition = constraintJson.getString(Constraint.Json.DEFINITION);
					Constraint constraint = new Constraint(name, definition);
					entity.addConstraint(constraint);
				}
			}

			Model.get().addEntity(entity);
		}
		// Header (optional)
		File headerFile = new File(inputDir, "header.txt");
		if (headerFile.exists()) {
			String header = FileUtils.readFileToString(headerFile).trim();
			Model.get().setHeader(header);
		}
		if (Config.LOGD) {
			Log.d(TAG, Model.get().toString());
		}
	}

	private JSONObject getConfig ( File inputDir ) throws IOException, JSONException {
		if (mConfig == null) {
			File configFile = new File(inputDir, FILE_CONFIG);
			String fileContents = FileUtils.readFileToString(configFile);
			mConfig = new JSONObject(fileContents);
		}

		validateConfig();

		return mConfig;
	}

	private void validateConfig () {
		// Ensure the input files are compatible with this version of the tool
		String configVersion;
		try {
			configVersion = mConfig.getString(Json.TOOL_VERSION);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Could not find 'toolVersion' field in _config.json, which is mandatory and must be equals to '" + Constants.VERSION + "'.");
		}
		if (!Constants.VERSION.startsWith(configVersion)) {
			throw new IllegalArgumentException("Invalid 'toolVersion' value in _config.json: found '" + configVersion + "' but expected '" + Constants.VERSION + "'.");
		}

		// Ensure mandatory fields are present
		ensureString(Json.PROJECT_PACKAGE_ID);
		ensureString(Json.PROVIDER_JAVA_PACKAGE);
		ensureString(Json.BUSINESS_JAVA_PACKAGE);
		ensureString(Json.PROVIDER_CLASS_NAME);
		ensureString(Json.SQLITE_OPEN_HELPER_CLASS_NAME);
		ensureString(Json.SQLITE_OPEN_HELPER_CALLBACKS_CLASS_NAME);
		ensureString(Json.AUTHORITY);
		ensureString(Json.DATABASE_FILE_NAME);
		ensureInt(Json.DATABASE_VERSION);
		ensureBoolean(Json.ENABLE_FOREIGN_KEY);
		ensureString(Json.API_BASE_URL);
		ensureString(Json.API_JAVA_PACKAGE);
		ensureString(Json.API_MODEL_JAVA_PACKAGE);
		ensureString(Json.API_SERVICE_INTERFACE_NAME);
	}

	private void ensureString ( String field ) {
		try {
			mConfig.getString(field);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Could not find '" + field + "' field in _config.json, which is mandatory and must be a string.");
		}
	}

	private void ensureBoolean ( String field ) {
		try {
			mConfig.getBoolean(field);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Could not find '" + field + "' field in _config.json, which is mandatory and must be a boolean.");
		}
	}

	private void ensureInt ( String field ) {
		try {
			mConfig.getInt(field);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Could not find '" + field + "' field in _config.json, which is mandatory and must be an int.");
		}
	}

	private void generateColumns ( Arguments arguments ) throws IOException, JSONException, TemplateException {
		Template template = getFreeMarkerConfig().getTemplate("columns.ftl");
		JSONObject config = getConfig(arguments.inputDir);
		String providerJavaPackage = config.getString(Json.PROVIDER_JAVA_PACKAGE);

		File providerDir = new File(arguments.outputDir, providerJavaPackage.replace('.', '/'));
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("config", getConfig(arguments.inputDir));
		root.put("header", Model.get().getHeader());

		// Entities
		for (Entity entity : Model.get().getEntities()) {
			File outputDir = new File(providerDir, entity.getPackageName());
			outputDir.mkdirs();
			File outputFile = new File(outputDir, entity.getNameCamelCase() + "Columns.java");
			Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));

			root.put("entity", entity);

			template.process(root, out);
			IOUtils.closeQuietly(out);
		}
	}

	private void generateWrappers ( Arguments arguments ) throws IOException, JSONException, TemplateException {
		JSONObject config = getConfig(arguments.inputDir);

		String providerJavaPackage = config.getString(Json.PROVIDER_JAVA_PACKAGE);
		File providerDir = new File(arguments.outputDir, providerJavaPackage.replace('.', '/'));
		File baseClassesDir = new File(providerDir, "base");
		baseClassesDir.mkdirs();

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("config", getConfig(arguments.inputDir));
		root.put("header", Model.get().getHeader());

		// AbstractCursor
		Template template = getFreeMarkerConfig().getTemplate("abstractcursor.ftl");
		File outputFile = new File(baseClassesDir, "AbstractCursor.java");
		Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));
		template.process(root, out);
		IOUtils.closeQuietly(out);

		// AbstractContentValuesWrapper
		template = getFreeMarkerConfig().getTemplate("abstractcontentvalues.ftl");
		outputFile = new File(baseClassesDir, "AbstractContentValues.java");
		out = new OutputStreamWriter(new FileOutputStream(outputFile));
		template.process(root, out);
		IOUtils.closeQuietly(out);

		// AbstractSelection
		template = getFreeMarkerConfig().getTemplate("abstractselection.ftl");
		outputFile = new File(baseClassesDir, "AbstractSelection.java");
		out = new OutputStreamWriter(new FileOutputStream(outputFile));
		template.process(root, out);
		IOUtils.closeQuietly(out);

		// Entities
		for (Entity entity : Model.get().getEntities()) {
			File entityDir = new File(providerDir, entity.getPackageName());
			entityDir.mkdirs();

			// Cursor wrapper
			outputFile = new File(entityDir, entity.getNameCamelCase() + "Cursor.java");
			out = new OutputStreamWriter(new FileOutputStream(outputFile));
			root.put("entity", entity);
			template = getFreeMarkerConfig().getTemplate("cursor.ftl");
			template.process(root, out);
			IOUtils.closeQuietly(out);

			// ContentValues wrapper
			outputFile = new File(entityDir, entity.getNameCamelCase() + "ContentValues.java");
			out = new OutputStreamWriter(new FileOutputStream(outputFile));
			root.put("entity", entity);
			template = getFreeMarkerConfig().getTemplate("contentvalues.ftl");
			template.process(root, out);
			IOUtils.closeQuietly(out);

			// Selection builder
			outputFile = new File(entityDir, entity.getNameCamelCase() + "Selection.java");
			out = new OutputStreamWriter(new FileOutputStream(outputFile));
			root.put("entity", entity);
			template = getFreeMarkerConfig().getTemplate("selection.ftl");
			template.process(root, out);
			IOUtils.closeQuietly(out);

			// Enums (if any)
			for (Field field : entity.getFields()) {
				if (field.isEnum()) {
					outputFile = new File(entityDir, field.getEnumName() + ".java");
					out = new OutputStreamWriter(new FileOutputStream(outputFile));
					root.put("entity", entity);
					root.put("field", field);
					template = getFreeMarkerConfig().getTemplate("enum.ftl");
					template.process(root, out);
					IOUtils.closeQuietly(out);
				}
			}
		}
	}

	private void generateContentProvider ( Arguments arguments ) throws IOException, JSONException, TemplateException {
		Template template = getFreeMarkerConfig().getTemplate("contentprovider.ftl");
		JSONObject config = getConfig(arguments.inputDir);
		String providerJavaPackage = config.getString(Json.PROVIDER_JAVA_PACKAGE);
		File providerDir = new File(arguments.outputDir, providerJavaPackage.replace('.', '/'));
		providerDir.mkdirs();
		File outputFile = new File(providerDir, config.getString(Json.PROVIDER_CLASS_NAME) + ".java");
		Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("config", config);
		root.put("model", Model.get());
		root.put("header", Model.get().getHeader());

		template.process(root, out);
	}

	private void generateSqliteOpenHelper ( Arguments arguments ) throws IOException, JSONException, TemplateException {
		Template template = getFreeMarkerConfig().getTemplate("sqliteopenhelper.ftl");
		JSONObject config = getConfig(arguments.inputDir);
		String providerJavaPackage = config.getString(Json.PROVIDER_JAVA_PACKAGE);
		File providerDir = new File(arguments.outputDir, providerJavaPackage.replace('.', '/'));
		providerDir.mkdirs();
		File outputFile = new File(providerDir, config.getString(Json.SQLITE_OPEN_HELPER_CLASS_NAME) + ".java");
		Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("config", config);
		root.put("model", Model.get());
		root.put("header", Model.get().getHeader());

		template.process(root, out);
	}

	private void generateSqliteOpenHelperCallbacks ( Arguments arguments ) throws IOException, JSONException, TemplateException {
		Template template = getFreeMarkerConfig().getTemplate("sqliteopenhelpercallbacks.ftl");
		JSONObject config = getConfig(arguments.inputDir);
		String providerJavaPackage = config.getString(Json.PROVIDER_JAVA_PACKAGE);
		File providerDir = new File(arguments.outputDir, providerJavaPackage.replace('.', '/'));
		providerDir.mkdirs();
		File outputFile = new File(providerDir, config.getString(Json.SQLITE_OPEN_HELPER_CALLBACKS_CLASS_NAME) + ".java");
		if (outputFile.exists()) {
			if (Config.LOGD) {
				Log.d(TAG, "generateSqliteOpenHelperCallbacks Open helper callbacks class already exists: skip");
			}
			return;
		}
		Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("config", config);
		root.put("model", Model.get());
		root.put("header", Model.get().getHeader());

		template.process(root, out);
	}

	private void generateBusinessObjects ( Arguments arguments ) throws IOException, JSONException, TemplateException {
		JSONObject config = getConfig(arguments.inputDir);

		String businessJavaPackage = config.getString(Json.BUSINESS_JAVA_PACKAGE);
		File businessDir = new File(arguments.outputDir, businessJavaPackage.replace('.', '/'));
		File baseClassesDir = new File(businessDir, "base");
		baseClassesDir.mkdirs();

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("config", getConfig(arguments.inputDir));
		root.put("header", Model.get().getHeader());

		// AbstractBusiness
		Template template = getFreeMarkerConfig().getTemplate("abstractbusiness.ftl");
		File outputFile = new File(baseClassesDir, "AbstractBusiness.java");
		Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));
		template.process(root, out);
		IOUtils.closeQuietly(out);

		// Entities
		for (Entity entity : Model.get().getEntities()) {
			outputFile = new File(businessDir, entity.getNameCamelCase() + ".java");
			out = new OutputStreamWriter(new FileOutputStream(outputFile));
			root.put("entity", entity);
			template = getFreeMarkerConfig().getTemplate("business.ftl");
			template.process(root, out);
			IOUtils.closeQuietly(out);
		}
	}

	private void generateApi ( Arguments arguments ) throws IOException, JSONException, TemplateException {
		JSONObject config = getConfig(arguments.inputDir);

		String apiJavaPackage = config.getString(Json.API_JAVA_PACKAGE);
		File apiDir = new File(arguments.outputDir, apiJavaPackage.replace('.', '/'));
		apiDir.mkdirs();

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("config", getConfig(arguments.inputDir));
		root.put("header", Model.get().getHeader());

		// Service
		Template template = getFreeMarkerConfig().getTemplate("apiservice.ftl");
		File outputFile = new File(apiDir, config.getString(Json.API_SERVICE_INTERFACE_NAME) + ".java");
		Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));
		root.put("entities", Model.get().getEntities());
		root.put("imports", Model.get().getApiImports(config));
		template.process(root, out);
		root.remove("entities");
		root.remove("imports");
		IOUtils.closeQuietly(out);

		String apiModelJavaPackage = config.getString(Json.API_MODEL_JAVA_PACKAGE);
		File apiModelDir = new File(arguments.outputDir, apiModelJavaPackage.replace('.', '/'));
		File baseClassesDir = new File(apiModelDir, "base");
		baseClassesDir.mkdirs();

		// AbstractModel
		template = getFreeMarkerConfig().getTemplate("abstractapi.ftl");
		outputFile = new File(baseClassesDir, "AbstractApi.java");
		out = new OutputStreamWriter(new FileOutputStream(outputFile));
		template.process(root, out);
		IOUtils.closeQuietly(out);

		// Entities
		for (Entity entity : Model.get().getEntities()) {
			outputFile = new File(apiModelDir, entity.getNameCamelCase() + ".java");
			out = new OutputStreamWriter(new FileOutputStream(outputFile));
			root.put("entity", entity);
			template = getFreeMarkerConfig().getTemplate("api.ftl");
			template.process(root, out);
			IOUtils.closeQuietly(out);
		}
	}

	private void printManifest ( Arguments arguments ) throws IOException, JSONException, TemplateException {
		Template template = getFreeMarkerConfig().getTemplate("manifest.ftl");
		JSONObject config = getConfig(arguments.inputDir);
		Writer out = new OutputStreamWriter(System.out);

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("config", config);
		root.put("model", Model.get());
		root.put("header", Model.get().getHeader());

		System.out.println("\nProvider declaration to paste in the AndroidManifest.xml file: ");
		template.process(root, out);
	}

	private void go ( String[] args ) throws IOException, JSONException, TemplateException {
		Arguments arguments = new Arguments();
		JCommander jCommander = new JCommander(arguments, args);
		jCommander.setProgramName("GenerateAndroidProvider");

		if (arguments.help) {
			jCommander.usage();
			return;
		}

		getConfig(arguments.inputDir);

		loadModel(arguments.inputDir);
		generateColumns(arguments);
		generateWrappers(arguments);
		generateContentProvider(arguments);
		generateSqliteOpenHelper(arguments);
		generateSqliteOpenHelperCallbacks(arguments);
		generateBusinessObjects(arguments);
		generateApi(arguments);

		printManifest(arguments);
	}

	public static void main ( String[] args ) throws Exception {
		new Main().go(args);
	}
}
