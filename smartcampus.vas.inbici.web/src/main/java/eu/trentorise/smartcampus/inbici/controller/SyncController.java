/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.inbici.controller;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrainingObject;
import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.common.util.Util;
import eu.trentorise.smartcampus.presentation.data.BasicObject;
import eu.trentorise.smartcampus.presentation.data.SyncData;
import eu.trentorise.smartcampus.presentation.data.SyncDataRequest;

@Controller
public class SyncController extends AbstractObjectController {

	@RequestMapping(method = RequestMethod.POST, value = "/sync")
	public ResponseEntity<SyncData> synchronize(HttpServletRequest request, @RequestParam long since, @RequestBody Map<String,Object> obj) throws Exception{
		try {
			String userId = null;
			try {
				userId = getUserId();
			} catch (SecurityException e) {
				
			}
			SyncDataRequest syncReq = Util.convertRequest(obj, since);
			List<TrackObject> toUpdate = new ArrayList<TrackObject>();
			if (syncReq.getSyncData().getUpdated() != null) {
				List<BasicObject> trainings = syncReq.getSyncData().getUpdated().get(TrainingObject.class.getName());
				if (trainings != null) {
					for (Iterator<BasicObject> iterator = trainings.iterator(); iterator.hasNext();) {
						TrainingObject to = (TrainingObject) iterator.next();
						to.setUser(userId);
						// check track object exists
						try {
							TrackObject old = storage.getObjectById(to.getTrackId(), TrackObject.class);
							if (old == null) {
								iterator.remove();
								continue;
							}
							toUpdate.add(old);
						} catch (Exception e) {
							iterator.remove();
							continue;
						}
					}
				}
				List<BasicObject> userTrips = syncReq.getSyncData().getUpdated().get(TrackObject.class.getName());
				if (userTrips != null) {
					for (Iterator<BasicObject> iterator = userTrips.iterator(); iterator.hasNext();) {
						TrackObject to = (TrackObject) iterator.next();
						// support only creation here
						try {
							TrackObject old = storage.getObjectById(to.getId(), TrackObject.class);
							if (old != null) {
								iterator.remove();
								continue;
							}
						} catch (Exception e) {
							iterator.remove();
							continue;
						}
						to.setUser(userId);
					}
				}
			}
			storage.cleanSyncData(syncReq.getSyncData(), userId);
			for (TrackObject to : toUpdate) {
				updateTrackStatistics(to);
			}
			
			SyncData result = storage.getSyncData(syncReq.getSince(), userId, syncReq.getSyncData().getInclude(), syncReq.getSyncData().getExclude());
			return new ResponseEntity<SyncData>(result,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void updateTrackStatistics(TrackObject to) throws DataException {
		List<TrainingObject> trainings = storage.searchObjects(TrainingObject.class, java.util.Collections.<String,Object>singletonMap("trackId", to.getId()));
		
		if (trainings != null && ! trainings.isEmpty()){
			long avgTime = 0, lastTime = 0;
			double maxSpeed = 0, avgSpeed = 0, elevation = 0;
			for (TrainingObject training : trainings) {
				avgTime += training.getRunningTime();
				avgSpeed += training.getAvgSpeed();
				if (training.getEndTime() > lastTime) lastTime = training.getEndTime();
				if (training.getMaxSpeed() > maxSpeed) maxSpeed = training.getMaxSpeed();
				elevation += training.getElevation();
			}
			avgTime = avgTime / trainings.size();
			avgSpeed = avgSpeed / trainings.size();
			elevation = elevation / trainings.size();
			to.setElapsed_time(avgTime);
			to.setLast_training_date(lastTime);
			to.setMax_speed(maxSpeed);
			to.setAvg_speed(avgSpeed);
			to.setTotal_elevation(elevation);
			to.setNumber_of_registered_uses(trainings.size());
			storage.storeObject(to);
		}
	}
	
}
