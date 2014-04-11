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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import eu.iescities.pilot.rovereto.inbici.custom.DTParamsHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.trentorise.smartcampus.android.common.Utils;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;
import eu.trentorise.smartcampus.storage.db.StorageConfiguration;
import eu.trentorise.smartcampus.storage.sync.ISynchronizer;
import eu.trentorise.smartcampus.storage.sync.SyncData;
import eu.trentorise.smartcampus.storage.sync.SyncStorageHelper;
import eu.trentorise.smartcampus.storage.sync.SyncStorageHelperWithPaging;
import eu.trentorise.smartcampus.storage.sync.SyncStorageWithPaging;


/**
 * Specific storage that deletes the old data upon sync complete
 * 
 * @author raman
 * 
 */
public class DTSyncStorage extends SyncStorageWithPaging {

	private static final Map<String, Object> exclude = new HashMap<String, Object>();
	private static final Map<String, Object> include = new HashMap<String, Object>();

	public DTSyncStorage(Context context, String appToken, String dbName, int dbVersion, StorageConfiguration config) {
		super(context, appToken, dbName, dbVersion, config);
		Map<String, Object> map = null;
		//map = DTParamsHelper.getExcludeArray();
		//if (map != null)
		//	exclude.putAll(map);

		//map = DTParamsHelper.getIncludeArray();
		//if (map != null)
		//	include.putAll(map);
	}

	@Override
	protected SyncStorageHelper createHelper(Context context, String dbName, int dbVersion, StorageConfiguration config) {
		return new DTSyncStorageHelper(context, dbName, dbVersion, config);
	}

	public void synchronize(final String token) throws StorageConfigurationException,
			DataException, SecurityException, ConnectionException, ProtocolException {
		synchronize(new ISynchronizer() {

			@Override
			public SyncData fetchSyncData(Long version, SyncData in) throws SecurityException, ConnectionException,
					ProtocolException {
					SyncData dbData = remoteSynchronize();
					return dbData;

			}
		});

	}

	protected SyncData remoteSynchronize() {
		return null;
	}

	protected Map<String, List<String>> convertToBasicObjectDeleted(Map<String, List<String>> deleted) {
		Map<String, List<String>> returnDTOObjects = new HashMap<String, List<String>>();
		Iterator it = deleted.entrySet().iterator();
		while (it.hasNext()) {
			// for every map element iterate the entire list
			{
				Map.Entry pairs = (Map.Entry) it.next();
				String key = (String) pairs.getKey();
				Class<? extends BaseDTObject> cls = null;
				if ("eu.trentorise.smartcampus.dt.model.EventObject".equals(key)) {
					cls = BaseDTObject.class;

				}

				List<String> dtoObjects = (List<String>) pairs.getValue();

				// //convert the single element
				// basicobjects.add(newObject);
				// //add the element to the return list
				// }
				// add the list to the return map
				// key or the new one???
				returnDTOObjects.put(cls.getCanonicalName(), dtoObjects);
			}
			// System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}
		return returnDTOObjects;
	}

//	protected Map<String, List<Object>> convertToBasicObject(Map<String, List<Object>> map) {
//		Map<String, List<Object>> returnDTOObjects = new HashMap<String, List<Object>>();
//		Iterator it = map.entrySet().iterator();
//		while (it.hasNext()) {
//			// for every map element iterate the entire list
//			{
//				Map.Entry pairs = (Map.Entry) it.next();
//				String key = (String) pairs.getKey();
//				Class<? extends BaseDTObject> cls = null;
//				if ("eu.trentorise.smartcampus.dt.model.EventObject".equals(key)) {
//					cls = ExplorerObject.class;
//
//				}
//
//				List<Object> dtoObjects = (List<Object>) pairs.getValue();
//				List<Object> basicobjects = new ArrayList<Object>();
//				for (Object object : dtoObjects) {
//					BaseDTObject newObject = null;
//					if (ExplorerObject.class.equals(cls)) {
//						newObject = new ExplorerObject();
//						newObject = Utils.convertObjectToData(ExplorerObject.class, object);
//					}
//
//					// convert the single element
//					basicobjects.add(newObject);
//					// add the element to the return list
//				}
//				// add the list to the return map
//				// key or the new one???
//				returnDTOObjects.put(cls.getCanonicalName(), basicobjects);
//			}
//			// System.out.println(pairs.getKey() + " = " + pairs.getValue());
//		}
//		return returnDTOObjects;
//	}

	private static class DTSyncStorageHelper extends SyncStorageHelperWithPaging {
		public DTSyncStorageHelper(Context context, String dbName, int version, StorageConfiguration config) {
			super(context, dbName, version, config);
		}

		@Override
		public SyncData getDataToSync(long version) throws StorageConfigurationException {
			SyncData data = super.getDataToSync(version);
			data.setExclude(exclude);
			data.setInclude(include);
			return data;
		}

	}

}
