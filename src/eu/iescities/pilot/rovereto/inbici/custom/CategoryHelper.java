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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.iescities.pilot.rovereto.inbici.R;



import android.util.Log;

public class CategoryHelper {


	public static final String CAT_TRACK_PISTA_CICLABILE = "Pista ciclabile";
	public static final String CAT_TRACK_PASSEGGIATE = "Passeggiate";
	public static final String CAT_TRACK_PISTE_CICLOPEDONALI = "Piste ciclopedonali";

	public static final String CATEGORY_TYPE_TRACKS = "tracks";


	public static final String CAT_CULTURA = "Cultura";
	public static final String CAT_SOCIALE = "Sociale";
	public static final String CAT_SPORT = "Sport";

	private final static String TAG = "CategoryHelper";
	public static final String EVENT_NONCATEGORIZED = "Other event";

	public static final String CATEGORY_TYPE_EVENTS = "events";

	public static final String CATEGORY_TODAY = "Today";
	public static final String CATEGORY_MY = "My";

	public static CategoryDescriptor EVENTS_TODAY = new CategoryDescriptor(R.drawable.ic_altri_eventi,
			R.drawable.ic_altri_eventi_map, CATEGORY_TODAY, R.string.categories_event_today);

	public static CategoryDescriptor EVENTS_MY = new CategoryDescriptor(R.drawable.ic_altri_eventi,
			R.drawable.ic_altri_eventi_map, CATEGORY_MY, R.string.categories_event_my);

	public static CategoryDescriptor[] EVENT_CATEGORIES = new CategoryDescriptor[] {

		/* 1 */new CategoryDescriptor(R.drawable.ic_cultura_map, R.drawable.ic_cultura, CAT_CULTURA,
				R.string.categories_event_cultura),
				/* 2 */new CategoryDescriptor(R.drawable.ic_sport_map, R.drawable.ic_sport, CAT_SPORT,
						R.string.categories_event_sport),
						/* 3 */new CategoryDescriptor(R.drawable.ic_svago_map, R.drawable.ic_person, CAT_SOCIALE,
								R.string.categories_event_social),
								/* 4 */new CategoryDescriptor(R.drawable.ic_altri_eventi_map, R.drawable.ic_altri_eventi,
										EVENT_NONCATEGORIZED, R.string.categories_event_altri_eventi),
	};



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

	
	
	public static String[] getAllCategories(Set<String> set) {
		List<String> result = new ArrayList<String>();
		for (String key : categoryMapping.keySet()) {
			if (set.contains(categoryMapping.get(key))) {
				if (key.equals(EVENT_NONCATEGORIZED)) {

					result.add(null);
				}
				result.add(key);
				// set.remove(categoryMapping.get(key));
			}
		}
		return result.toArray(new String[result.size()]);
	}
	
	
	

	public static String getMainCategory(String category) {
		return categoryMapping.get(category);
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
	

	public static CategoryDescriptor[] getEventCategoryDescriptors() {
		return EVENT_CATEGORIES;
	}

	
	public static String[] getEventCategories() {
		String[] res = new String[EVENT_CATEGORIES.length];
		for (int i = 0; i < EVENT_CATEGORIES.length; i++) {
			res[i] = EVENT_CATEGORIES[i].category;
		}

		Log.i("MENU", "EVENT CATEGORIES: " + res.toString() + "\n--- lenght: " + res.length);
		return res;
	}

	public static String[] getEventCategoriesForMapFilters() {
		String[] res = new String[EVENT_CATEGORIES.length + 2];
		res[0] = EVENTS_MY.category;
		res[1] = EVENTS_TODAY.category;

		for (int i = 2; i < EVENT_CATEGORIES.length + 2; i++) {
			res[i] = EVENT_CATEGORIES[i - 2].category;
		}

		Log.i("MENU", "EVENT CATEGORIES: " + res.toString() + "\n--- lenght: " + res.length);
		return res;
	}


	public static CategoryDescriptor[] getEventCategoryDescriptorsFiltered() {
		return DTParamsHelper.getInstance().getFilteredArrayByParams(EVENT_CATEGORIES, CATEGORY_TYPE_EVENTS);
	}
	
	public static CategoryDescriptor[] getTrackCategoryDescriptorsFiltered() {
		return DTParamsHelper.getInstance().getFilteredArrayByParams(TRACK_CATEGORIES, CATEGORY_TYPE_TRACKS);
	}


	public static CategoryDescriptor getCategoryDescriptorByCategoryFiltered(String type, String cat) {
		return descriptorMap.get(cat);

	}



}
