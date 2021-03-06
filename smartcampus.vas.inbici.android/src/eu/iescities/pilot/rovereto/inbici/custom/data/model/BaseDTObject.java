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
package eu.iescities.pilot.rovereto.inbici.custom.data.model;

import java.util.Arrays;
import java.util.Map;

import eu.trentorise.smartcampus.storage.BasicObject;



public class BaseDTObject extends BasicObject {
	
	private static final long serialVersionUID = 3589900794339644582L;

	private String description = null;
	private String title = null;
	private String source = null; // service 'source' of the object
	private String type = null;
	//common data
	private double[] location;

	private Map<String,Object> customData = null;
	
	public BaseDTObject() {
		super();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public double[] getLocation() {
		return location;
	}

	public void setLocation(double[] location) {
		this.location = location;
	}

	public Map<String, Object> getCustomData() {
		return customData;
	}

	public void setCustomData(Map<String, Object> customData) {
		this.customData = customData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customData == null) ? 0 : customData.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + Arrays.hashCode(location);
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseDTObject other = (BaseDTObject) obj;
		if (customData == null) {
			if (other.customData != null)
				return false;
		} else if (!checkCustomData(customData, other.customData))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (!Arrays.equals(location, other.location))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	private boolean checkCustomData(Map<String,Object> c1, Map<String,Object> c2) {
//		TreeMap<String, Object> tm = new TreeMap<String, Object>(c2);
//		return c1.equals(tm);
		if (c1.keySet().size() != c2.keySet().size()) {
			return false;
		}
		for (String key: c1.keySet()) {
			Object v1 = c1.get(key);
			Object v2 = c2.get(key);
			if (!v1.equals(v2)) {
				return false;
			}
		}
		return true;
	}

}