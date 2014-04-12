/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.iescities.pilot.rovereto.inbici.custom;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.iescities.pilot.rovereto.inbici.R;

public class CategoryHelper {

	public static final String TYPE_MY = "my";
	public static final String TYPE_OFFICIAL = "official";
	public static final String TYPE_USER = "user";


	public static final String CAT_TRACK_PISTA_CICLABILE = "Pista ciclabile";
	public static final String CAT_TRACK_PASSEGGIATE = "Passeggiate";
	public static final String CAT_TRACK_PISTE_CICLOPEDONALI = "Piste ciclopedonali";

	public static final String CATEGORY_TYPE_TRACKS = "tracks";

	public static CategoryDescriptor[] TRACK_CATEGORIES = new CategoryDescriptor[] {
		/* 1 */new CategoryDescriptor(R.drawable.ic_bici_map, R.drawable.ic_bici, CAT_TRACK_PISTE_CICLOPEDONALI,
				R.string.categories_track_pedestrian),
	};

	private static Map<String, String> categoryMapping = new HashMap<String, String>();

	private static Map<String, CategoryDescriptor> descriptorMap = new LinkedHashMap<String, CategoryHelper.CategoryDescriptor>();
	static {
		for (CategoryDescriptor track : TRACK_CATEGORIES) {
			descriptorMap.put(track.category, track);
		}
		for (String s : descriptorMap.keySet()) {
			categoryMapping.put(s, s);
		}
	}
	
	public static int getMapIconByType(String type) {
		if (categoryMapping.containsKey(type))
			return descriptorMap.get(categoryMapping.get(type)).map_icon;
		return R.drawable.ic_altri_eventi_map;
	}

	public static int getIconByType(String type) {
		if (categoryMapping.containsKey(type))
			return descriptorMap.get(categoryMapping.get(type)).thumbnail;
		return R.drawable.ic_p_other;
	}

	public static class CategoryDescriptor {
		public int map_icon;
		public int thumbnail;
		public String category;
		public int description;

		public CategoryDescriptor(int map_icon, int thumbnail, String category, int description) {
			super();
			this.map_icon = map_icon;
			this.thumbnail = thumbnail;
			this.category = category;
			this.description = description;
		}
	}


	
	public static CategoryDescriptor[] getTrackCategoryDescriptors() {
		return TRACK_CATEGORIES;
	}
	
	public static String[] getTrackCategories() {
		String[] res = new String[TRACK_CATEGORIES.length];
		for (int i = 0; i < TRACK_CATEGORIES.length; i++) {
			res[i] = TRACK_CATEGORIES[i].category;
		}
		return res;
	}
	
	public static CategoryDescriptor getCategoryDescriptorByCategoryFiltered(String type, String cat) {
		return descriptorMap.get(cat);

	}



}
