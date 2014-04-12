/*******************************************************************************
 * Copyright 2012-2014 Trento RISE
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
package eu.trentorise.smartcampus.inbici.processor;

import it.sayservice.platform.client.InvocationException;
import it.sayservice.platform.client.ServiceBusClient;
import it.sayservice.platform.client.ServiceBusListener;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.trentorise.smartcampus.inbici.listener.Subscriber;
import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.common.exception.NotFoundException;
import eu.trentorise.smartcampus.presentation.storage.sync.BasicObjectSyncStorage;
import eu.trentorise.smartcampus.service.trentinotrack.data.message.Trentinotrack.BikeTrack;

//import eu.trentorise.smartcampus.dt.model.InfoObject;

public class EventProcessorImpl implements ServiceBusListener {

	private static final String BIKE_TRACK = "Pista ciclabile";
	private static final String BIKEWALK_TRACK = "Piste ciclopedonali";

	@Autowired
	private BasicObjectSyncStorage storage;
	
	@Autowired
	ServiceBusClient client;
	

	private static Log logger = LogFactory.getLog(EventProcessorImpl.class);

	public EventProcessorImpl() {
	}
	
	@Override
	public void onServiceEvents(String serviceId, String methodName, String subscriptionId, List<ByteString> data) {
		System.out.println(new Date() + " -> " + methodName + "@" + serviceId);
		try {
			if (Subscriber.TRACKS.endsWith(serviceId)) {
				if (Subscriber.GET_BIKE_TRACKS.equals(methodName)) {
					updateTrack(data, methodName, BIKE_TRACK);
				} else if (Subscriber.GET_WALKBIKE_TRACKS.equals(methodName)) {
					updateTrack(data, methodName, BIKEWALK_TRACK);
				}

			}

		} catch (Exception e) {
			logger.error("Error updating " + methodName);
			e.printStackTrace();
		}
	}
	
	public void updateTrack(List<ByteString> bsl, String methodName, String type) throws InvocationException, InvalidProtocolBufferException, NotFoundException, DataException {
		for (ByteString bs : bsl) {
			BikeTrack bt = BikeTrack.parseFrom(bs);
			String id = encode(methodName + "_" + bt.getId());

			TrackObject newTObj = null, refObj = null;
			try {
				newTObj = storage.getObjectById(id, TrackObject.class);
				refObj = storage.getObjectById(id, TrackObject.class);
			} catch (NotFoundException e) {
				newTObj = new TrackObject();
			}
			newTObj.setTrack(bt.getPolyline());

			newTObj.setType(type);
			newTObj.setSource(Subscriber.TRACKS);

			newTObj.setTitle(bt.getLabel());
			newTObj.setDescription(bt.getAbout());

			newTObj.setId(id);
			newTObj.setTrack_lenght(Integer.parseInt(bt.getLength()));
			//
			Map<String, Object> cd = new TreeMap<String, Object>();
			cd.put("label", bt.getLabel());
			cd.put("about", bt.getAbout());
			if (bt.hasLink()) {
				cd.put("link", bt.getLink());
			}

			newTObj.setCustomData(cd);

			if (!newTObj.equals(refObj)) {
				storage.storeObject(newTObj);
			}
		}
	}

	public BasicObjectSyncStorage getStorage() {
		return storage;
	}

	public void setStorage(BasicObjectSyncStorage storage) {
		this.storage = storage;
	}

	public ServiceBusClient getClient() {
		return client;
	}

	public void setClient(ServiceBusClient client) {
		this.client = client;
	}

	private static String encode(String s) {
		return new BigInteger(s.getBytes()).toString(16);
	}

}
