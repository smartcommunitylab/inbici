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
import java.util.ArrayList;
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
			if (bt.getLength() != null && !bt.getLength().isEmpty()) {
				newTObj.setTrack_lenght((int)Double.parseDouble(bt.getLength()));
			} else {
				List<double[]> list = decodePolyline(bt.getPolyline());
				newTObj.setTrack_lenght((int)computeLength(list));
			}
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

	private List<double[]> decodePolyline(String encoded) {
		List<double[]> polyline = new ArrayList<double[]>();

		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			if (index >= len) {
				break;
			}
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			double[] p = new double[]{(double) lat / 1E5, (double) lng / 1E5};
			polyline.add(p);
		}
		return polyline;
	}
	
	private double computeLength(List<double[]> coords) {
		double[] from, to;
		double dist = 0;
		for (int i = 1; i < coords.size(); i++) {
			from = coords.get(i-1);
			to = coords.get(i);
			dist += computeDistance(from, to);
		}
		return dist;
	}
	
	private double computeDistance(double[] from, double[] to) {
		double d2r =  Math.PI / 180;
	    double dlong = (to[1] - from[1]) * d2r;
	    double dlat = (to[0] - from[0]) * d2r;
	    double a =
	        Math.pow(Math.sin(dlat / 2.0), 2)
	            + Math.cos(from[0] * d2r)
	            * Math.cos(to[0] * d2r)
	            * Math.pow(Math.sin(dlong / 2.0), 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double d = 6367000 * c;
	    return d;
	}
}
