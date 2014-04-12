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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Constants {

	/** Should be set in App metadata in order to properly manage the sync */
	public static final String DT_SYNC_AUTHORITY = "dt-sync-authority";

//	public static final String APP_TOKEN = "discovertrento";
	public static final String EVENTS = "events";
	public static final String EVENTS_P = "events/%s";
	public static final String RATE = "objects/%s/rate";
	public static final String ATTEND = "objects/%s/attend";
	public static final String NOT_ATTEND = "objects/%s/notAttend";
	public static final String FOLLOW = "objects/%s/follow";
	public static final String UNFOLLOW = "objects/%s/unfollow";
	public static final String SYNC_SERVICE = "sync";
	public static final String SERVICE = "/inbici";
//	public static final String SYNC_SERVICE = "/smartcampus.vas.discovertrento.web/sync";
	public static final int SYNC_INTERVAL = 5;
	public static final String SYNC_DB_NAME = "discovertrentodb";
	public static final String PREFS = "eu.trentorise.smartcampus.dt.preferences";
	public static final String PREFS_USER_ID = "user.profile.id";
	public static final String PREFS_USER_SOCIAL_ID = "user.profile.socialId";
	public static final String PREFS_USER_NAME = "user.profile.name";
	public static final String PREFS_USER_SURNAME = "user.profile.surname";

	public static final String CM_SERVICE = "/smartcampus.vas.community-manager.web";
	
	
	public static final String TYPE_EVENT = "event";
	public static final String TYPE_LOCATION = "location";
	public static final String TYPE_INFO = "info";
	public static final String TYPE_TRACK = "track";
	
	public static final String CUSTOM_TOKNOW = "toknow";
	public static final String CUSTOM_TOKNOW_PLACE_TYPE = "_toknow_place_type";
	public static final String CUSTOM_TOKNOW_ACCESS = "_toknow_access";
	public static final String CUSTOM_TOKNOW_CHANCE = "_toknow_chance";
	public static final String CUSTOM_TOKNOW_LANGUAGE_MAIN = "_toknow_language_main";
	public static final String CUSTOM_TOKNOW_CLOTHING = "_toknow_clothing";
	public static final String CUSTOM_TOKNOW_TO_BRING = "_toknow_to_bring";
	
	public static final String CUSTOM_TOKNOW_TYPE_ATTRIBUTE = "attribute";
	public static final String CUSTOM_TOKNOW_TYPE_VALUE= "value";
	

	public static final String ARG_EVENT_CATEGORY = "event category";
	public static final String ARG_POI_CATEGORY = "poi category";
	public static final String ARG_OBJECTS = "objects";
	public static final String ARG_TRACK_CATEGORY = "track_category";

	
	
	
	

	
	public static String getAuthority(Context ctx) throws NameNotFoundException {
		ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
		return ai.metaData.getString(DT_SYNC_AUTHORITY);
	}

}
