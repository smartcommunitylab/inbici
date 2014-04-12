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
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrainingObject;
import eu.trentorise.smartcampus.storage.db.BeanStorageHelper;

public class TrainingStorageHelper implements BeanStorageHelper<TrainingObject> {

	@Override
	public TrainingObject toBean(Cursor cursor) {
		TrainingObject o = new TrainingObject();
		if (cursor != null) {
			o.setId(cursor.getString(cursor.getColumnIndex("id")));
			o.setAvgSpeed(cursor.getDouble(cursor.getColumnIndex("avgspeed")));
			o.setDistance(cursor.getDouble(cursor.getColumnIndex("distance")));
			o.setElevation(cursor.getDouble(cursor.getColumnIndex("elevation")));
			o.setEndTime(cursor.getLong(cursor.getColumnIndex("endtime")));
			o.setMaxSpeed(cursor.getDouble(cursor.getColumnIndex("maxspeed")));
			o.setRunningTime(cursor.getDouble(cursor.getColumnIndex("runningtime")));
			o.setStartTime(cursor.getLong(cursor.getColumnIndex("starttime")));
			o.setTrackId(cursor.getString(cursor.getColumnIndex("trackid")));
		}
		return o;
	}

	@Override
	public ContentValues toContent(TrainingObject t) {
		ContentValues values = new ContentValues();
		values.put("id", t.getId());
		values.put("trackid", t.getTrackId());
		values.put("avgspeed", t.getAvgSpeed());
		values.put("distance", t.getDistance());
		values.put("elevation", t.getElevation());
		values.put("endtime", t.getEndTime());
		values.put("maxspeed", t.getMaxSpeed());
		values.put("runningtime", t.getRunningTime());
		values.put("starttime", t.getStartTime());
		return values;
	}

	@Override
	public Map<String,String> getColumnDefinitions() {
		Map<String, String> defs = new HashMap<String, String>();
		defs.put("trackid", "TEXT");
		defs.put("starttime", "INTEGER");
		defs.put("endtime", "INTEGER");
		defs.put("maxspeed", "DOUBLE");
		defs.put("avgspeed", "DOUBLE");
		defs.put("distance", "DOUBLE");
		defs.put("runningtime", "DOUBLE");
		defs.put("elevation", "DOUBLE");
		return defs;
	}

	@Override
	public boolean isSearchable() {
		return false;
	}
}
