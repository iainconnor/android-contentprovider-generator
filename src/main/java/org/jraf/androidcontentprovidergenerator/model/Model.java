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

import org.json.JSONObject;

import java.util.*;

public class Model {
	private static final Model INSTANCE = new Model();

	public static Model get () {
		return INSTANCE;
	}

	private Model () {
	}

	private final List<Entity> mEntities = new ArrayList<Entity>();
	private String mHeader;

	public void addEntity ( Entity entity ) {
		mEntities.add(entity);
	}

	public List<String> getApiImports (JSONObject config) {
		List<String> uniqueIncludes = new ArrayList<String>();

		for (Entity entity : getEntities()) {
			Set<String> set = new HashSet<String>(uniqueIncludes);
			set.addAll(entity.getApiImports(config));
			uniqueIncludes = new ArrayList<String>(set);
		}

		return Collections.unmodifiableList(uniqueIncludes);
	}

	public List<Entity> getEntities () {
		return Collections.unmodifiableList(mEntities);
	}

	public void setHeader ( String header ) {
		mHeader = header;
	}

	public String getHeader () {
		return mHeader;
	}

	@Override
	public String toString () {
		return mEntities.toString();
	}
}
