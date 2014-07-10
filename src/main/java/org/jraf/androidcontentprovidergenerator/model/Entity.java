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
import org.jraf.androidcontentprovidergenerator.Main;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Entity {
	private final String mName;
	private final List<Field> mFields = new ArrayList<Field>();
	private final List<Constraint> mConstraints = new ArrayList<Constraint>();
	private final List<ApiEndpoint> apiEndpoints = new ArrayList<ApiEndpoint>();

	public Entity ( String name ) {
		mName = name;
	}

	public void addApiMethod ( ApiEndpoint apiEndpoint ) {
		apiEndpoints.add(apiEndpoint);
	}

	public List<ApiEndpoint> getApiEndpoints () {
		return Collections.unmodifiableList(apiEndpoints);
	}

	public void addField ( Field field ) {
		mFields.add(field);
	}

	public List<Field> getFields () {
		return Collections.unmodifiableList(mFields);
	}

	public void addConstraint ( Constraint constraint ) {
		mConstraints.add(constraint);
	}

	public List<Constraint> getConstraints () {
		return Collections.unmodifiableList(mConstraints);
	}

	public String getNameCamelCase () {
		return WordUtils.capitalizeFully(mName, new char[] {'_'}).replaceAll("_", "");
	}

	public String getPackageName () {
		return getNameLowerCase().replace("_", "");
	}

	public String getNameLowerCase () {
		return mName.toLowerCase(Locale.US);
	}

	public String getNameUpperCase () {
		return mName.toUpperCase(Locale.US);
	}

	public List<String> getApiImports (JSONObject config) {
		List<String> uniqueIncludes = new ArrayList<String>();

		for (ApiEndpoint apiEndpoint : getApiEndpoints()) {
			for (ApiParam apiParam : apiEndpoint.getParameters()) {
				for (String importClass : apiParam.getType().getImports(config.getString(Main.Json.API_MODEL_JAVA_PACKAGE), config.getString(Main.Json.PROVIDER_JAVA_PACKAGE) + "." + getPackageName())) {
					if (!uniqueIncludes.contains(importClass)) {
						uniqueIncludes.add(importClass);
					}
				}
			}

			Type returnType = apiEndpoint.getReturnType();
			for (String importClass : returnType.getImports(config.getString(Main.Json.API_MODEL_JAVA_PACKAGE), config.getString(Main.Json.PROVIDER_JAVA_PACKAGE) + "." + getPackageName())) {
				if (!uniqueIncludes.contains(importClass)) {
					uniqueIncludes.add(importClass);
				}
			}
		}

		return Collections.unmodifiableList(uniqueIncludes);
	}

	@Override
	public String toString () {
		return "Entity [mName=" + mName + ", mFields=" + mFields + ", mConstraints=" + mConstraints + "]";
	}
}
