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
package eu.iescities.pilot.rovereto.inbici.custom.data;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.trentorise.smartcampus.android.common.Utils;

public class BaseDTStorageHelper {

	public static void setCommonFields(Cursor cursor, BaseDTObject o) {
		if (cursor != null) {
			o.setId(cursor.getString(cursor.getColumnIndex("id")));
			o.setDescription(cursor.getString(cursor.getColumnIndex("description")));
			o.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			o.setSource(cursor.getString(cursor.getColumnIndex("source")));
			
			//from BasicObject
			o.setVersion(cursor.getLong(cursor.getColumnIndex("version")));

			o.setType(cursor.getString(cursor.getColumnIndex("type")));
			o.setLocation(new double[] { cursor.getDouble(cursor.getColumnIndex("latitude")),
					cursor.getDouble(cursor.getColumnIndex("longitude")) });

			@SuppressWarnings("unchecked")
			Map<String, Object> map = Utils.convertJSONToObject(cursor.getString(cursor.getColumnIndex("customData")),
					Map.class);
			if (map != null && !map.isEmpty())
				o.setCustomData(map);
			else o.setCustomData(new HashMap<String, Object>());
		}
	}

	public static ContentValues toCommonContent(BaseDTObject bean) {
		ContentValues values = new ContentValues();
		values.put("id", bean.getId());
		values.put("description", bean.getDescription());

		values.put("title", bean.getTitle());
		values.put("source", bean.getSource());

		//from BasicObject
		values.put("version", bean.getVersion());
		

		values.put("type", bean.getType());

		if (bean.getLocation() != null) {
			values.put("latitude", bean.getLocation()[0]);
			values.put("longitude", bean.getLocation()[1]);
		}

		
		if (bean.getCustomData() != null && !bean.getCustomData().isEmpty()) {
			values.put("customData", Utils.convertToJSON(bean.getCustomData()));
		}
		return values;
	}

	public static Map<String, String> getCommonColumnDefinitions() {
		Map<String, String> defs = new HashMap<String, String>();
		defs.put("description", "TEXT");
		defs.put("title", "TEXT");
		defs.put("source", "TEXT");
		defs.put("version", "DOUBLE");
		defs.put("type", "TEXT");
		defs.put("latitude", "DOUBLE");
		defs.put("longitude", "DOUBLE");
		defs.put("customData", "TEXT");

		return defs;
	}

}
