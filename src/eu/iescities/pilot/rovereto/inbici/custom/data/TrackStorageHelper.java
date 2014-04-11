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
		

		TrackObject returnTrackObjectForBean = new TrackObject();
		TrackObject track = new TrackObject();
		BaseDTStorageHelper.setCommonFields(cursor, track);
		
		track.setTrack(cursor.getString(cursor.getColumnIndex("track")));
		

		return returnTrackObjectForBean;
	}

	@Override
	public ContentValues toContent(TrackObject track) {
		ContentValues values = BaseDTStorageHelper.toCommonContent(track);
		values.put("track", track.getTrack());
		
		return values;
	}

	@Override
	public Map<String,String> getColumnDefinitions() {
		Map<String,String> defs = BaseDTStorageHelper.getCommonColumnDefinitions();

		defs.put("fromTime", "INTEGER");
		defs.put("toTime", "INTEGER");
		defs.put("timing", "TEXT");
		defs.put("track", "TEXT");
		return defs;
	}

	@Override
	public boolean isSearchable() {
		return true;
	}

	
}
