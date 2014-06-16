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

import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.trentorise.smartcampus.storage.db.BeanStorageHelper;

public class TrackStorageHelper implements BeanStorageHelper<TrackObject> {

	@Override
	public TrackObject toBean(Cursor cursor) {
		TrackObject track = new TrackObject();
		BaseDTStorageHelper.setCommonFields(cursor, track);
		track.setTrack(cursor.getString(cursor.getColumnIndex("track")));
		track.setTrack_lenght(cursor.getInt(cursor.getColumnIndex("track_lenght")));
		track.setTrack_lenght_descriptive(cursor.getString(cursor.getColumnIndex("track_lenght_descriptive")));
		track.setWind(cursor.getString(cursor.getColumnIndex("wind")));
		track.setAverage_travel_time(cursor.getString(cursor.getColumnIndex("average_travel_time")));
		track.setAltitude_gap(cursor.getString(cursor.getColumnIndex("altitude_gap")));
		track.setTotal_elevation(cursor.getDouble(cursor.getColumnIndex("total_elevation")));
		track.setElapsed_time(cursor.getLong(cursor.getColumnIndex("elapsed_time")));
		track.setTraveled_distance(cursor.getDouble(cursor.getColumnIndex("traveled_distance")));
		track.setAvg_speed(cursor.getDouble(cursor.getColumnIndex("avg_speed")));
		track.setMax_speed(cursor.getDouble(cursor.getColumnIndex("max_speed")));
		track.setNumber_of_registered_uses(cursor.getInt(cursor.getColumnIndex("number_of_registered_uses")));
		track.setCreator(cursor.getString(cursor.getColumnIndex("creator")));
		track.setType_of_surface(cursor.getString(cursor.getColumnIndex("type_of_surface")));
		track.setCrossing_with_other_paths(cursor.getString(cursor.getColumnIndex("crossing_with_other_paths")));
		track.setAdvised_season(cursor.getString(cursor.getColumnIndex("advised_season")));
		track.setTraffic(cursor.getString(cursor.getColumnIndex("traffic")));

		return track;
	}

	@Override
	public ContentValues toContent(TrackObject track) {
		ContentValues values = BaseDTStorageHelper.toCommonContent(track);
		values.put("track", track.getTrack());

		values.put("track_lenght", track.getTrack_lenght());
		values.put("track_lenght_descriptive", track.getTrack_lenght_descriptive());
		values.put("wind", track.getWind());
		values.put("average_travel_time", track.getAverage_travel_time());
		values.put("altitude_gap", track.getAltitude_gap());
		values.put("elapsed_time", track.getElapsed_time());
		values.put("traveled_distance", track.getTraveled_distance());
		values.put("avg_speed", track.getAvg_speed());
		values.put("max_speed", track.getMax_speed());
		values.put("total_elevation", track.getTotal_elevation());
		values.put("number_of_registered_uses", track.getNumber_of_registered_uses());
		values.put("last_training_date", track.getLast_training_date());
		values.put("creator", track.getCreator());
		values.put("type_of_surface", track.getType_of_surface());
		values.put("crossing_with_other_paths", track.getCrossing_with_other_paths());
		values.put("advised_season", track.getAdvised_season());
		values.put("traffic", track.getTraffic());

		return values;
	}

	@Override
	public Map<String,String> getColumnDefinitions() {
		Map<String,String> defs = BaseDTStorageHelper.getCommonColumnDefinitions();
		defs.put("track", "TEXT");
		defs.put("track_lenght", "INTEGER");
		defs.put("track_lenght_descriptive", "TEXT");
		defs.put("wind", "TEXT");
		defs.put("average_travel_time", "TEXT");
		defs.put("altitude_gap", "STRING");
		defs.put("elapsed_time", "DOUBLE");
		defs.put("traveled_distance", "DOUBLE");
		defs.put("avg_speed", "DOUBLE");
		defs.put("max_speed", "DOUBLE");
		defs.put("total_elevation", "DOUBLE");
		defs.put("number_of_registered_uses", "INTEGER");
		defs.put("last_training_date", "INTEGER");
		defs.put("creator", "TEXT");
		defs.put("type_of_surface", "TEXT");
		defs.put("crossing_with_other_paths", "TEXT");
		defs.put("advised_season", "TEXT");
		defs.put("traffic", "TEXT");
		return defs;
	}

	@Override
	public boolean isSearchable() {
		return false;
	}
}
