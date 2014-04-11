package eu.iescities.pilot.rovereto.inbici.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;

import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.Constants;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.event.ToKnow;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.trentorise.smartcampus.android.common.follow.model.Concept;
import eu.trentorise.smartcampus.android.common.geo.OSMAddress;
import eu.trentorise.smartcampus.android.common.tagging.SemanticSuggestion;
import eu.trentorise.smartcampus.android.common.tagging.SemanticSuggestion.TYPE;

public class Utils {
	public static final String userPoiObject = "eu.trentorise.smartcampus.dt.model.UserPOIObject";
	public static final String servicePoiObject = "eu.trentorise.smartcampus.dt.model.ServicePOIObject";

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat DATETIME_FORMAT_WITH_SEC = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public static final SimpleDateFormat DATE_FORMAT_2 = new SimpleDateFormat("dd MMM. yyyy");
	public static final DateFormat DATE_FORMAT_2_with_dayweek = new SimpleDateFormat("EEEEEE dd MMM. yyyy");
	public static final SimpleDateFormat DATE_FORMAT_2_with_time = new SimpleDateFormat("dd MMM. yyyy HH:mm");
	public static final DateFormat DATE_FORMAT_2_with_dayweek_time = new SimpleDateFormat("EEEEEE dd MMM. yyyy HH:mm");

	public static final SimpleDateFormat FORMAT_DATE_UI = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
	public static final SimpleDateFormat FORMAT_DATE_UI_LONG = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	public static final SimpleDateFormat FORMAT_TIME_UI = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
	public static final int PAST_MINUTES_SPAN = -5; // has to be negative

	public static final String ARG_EVENT_ID = "event_id";
	public static final String ARG_EVENT_FIELD_TYPE = "event_field_type";
	public static final String ARG_EVENT_FIELD_TYPE_IS_MANDATORY = "event_field_type_is_mandatory";
	public static final String ARG_EVENT_IMAGE_URL = "event_image";

	public static final String ROVERETO_REGION = "it";
	public static final String ROVERETO_COUNTRY = "IT";
	public static final String ROVERETO_ADM_AREA = "TN";

	public static final String ADDRESS = "address";

	public static final String EDIT_FIELD_PHONE_TYPE = "phone";
	public static final String EDIT_FIELD_EMAIL_TYPE = "email";
	public static final String EDIT_FIELD_TEXT_TYPE = "text";

	public static final String EMAIL_CONTACT_TYPE = "email";
	public static final String PHONE_CONTACT_TYPE = "phone";


	public static final String[] stopWordsForOrigin = new String[]{"a cura", "acura"};






	// public static List<ExplorerObject> appEvents = getFakeEventObjects();

	public static List<Concept> conceptConvertSS(Collection<SemanticSuggestion> tags) {
		List<Concept> result = new ArrayList<Concept>();
		for (SemanticSuggestion ss : tags) {
			if (ss.getType() == TYPE.KEYWORD) {
				result.add(new Concept(null, ss.getName()));
			} else if (ss.getType() == TYPE.SEMANTIC) {
				Concept c = new Concept();
				// c.setId(ss.getId());
				c.setName(ss.getName());
				c.setDescription(ss.getDescription());
				c.setSummary(ss.getSummary());
				result.add(c);
			}
		}
		return result;
	}

	public static ArrayList<SemanticSuggestion> conceptConvertToSS(List<Concept> tags) {
		if (tags == null)
			return new ArrayList<SemanticSuggestion>();
		ArrayList<SemanticSuggestion> result = new ArrayList<SemanticSuggestion>();
		for (Concept c : tags) {
			SemanticSuggestion ss = new SemanticSuggestion();
			if (c.getId() == null) {
				ss.setType(TYPE.KEYWORD);
			} else {
				// ss.setId(c.getId());
				ss.setDescription(c.getDescription());
				ss.setSummary(c.getSummary());
				ss.setType(TYPE.SEMANTIC);
			}
			ss.setName(c.getName());
			result.add(ss);
		}
		return result;
	}

	public static String conceptToSimpleString(List<Concept> tags) {
		if (tags == null)
			return null;
		String content = "";
		for (Concept s : tags) {
			if (content.length() > 0)
				content += ", ";
			content += s.getName();
		}
		return content;
	}

	/**
	 * @param mTrack
	 * @return
	 */

	// public static boolean isCreatedByUser(BaseDTObject obj) {
	// if (obj.getDomainType() == null ||
	// userPoiObject.equals(obj.getDomainType())) {
	// return true;
	// } else
	// return false;
	// }

	// public static Collection<LocalExplorerObject>
	// convertToLocalEventFromBean(
	// Collection<ExplorerObjectForBean> searchInGeneral) {
	// Collection<LocalExplorerObject> returnCollection = new
	// ArrayList<LocalExplorerObject>();
	// for (ExplorerObjectForBean event : searchInGeneral) {
	// LocalExplorerObject localEvent =
	// DTHelper.findEventById(event.getObjectForBean().getId());
	// if (localEvent != null) {
	//
	// returnCollection.add(localEvent);
	// }
	// }
	// return returnCollection;
	// }

	// public static Collection<LocalExplorerObject>
	// convertToLocalEvent(Collection<ExplorerObject> events) {
	// Collection<ExplorerObjectForBean> beanEvents = new
	// ArrayList<ExplorerObjectForBean>();
	// Collection<LocalExplorerObject> returnEvents = new
	// ArrayList<LocalExplorerObject>();
	//
	// for (ExplorerObject event : events) {
	// ExplorerObjectForBean newObject = new ExplorerObjectForBean();
	// LocalExplorerObject localObject = new LocalExplorerObject();
	// newObject.setObjectForBean(event);
	// localObject.setEventFromExplorerObjectForBean(newObject);
	// returnEvents.add(localObject);
	// }
	//
	// return returnEvents;
	// }

	public static List<LatLng> decodePolyline(String encoded) {
		List<LatLng> polyline = new ArrayList<LatLng>();

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

			LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			polyline.add(p);
		}
		return polyline;
	}

	/**
	 * @param event
	 * @return
	 */
	public static String getEventShortAddress(ExplorerObject event) {
		if (event.getCustomData() != null && event.getCustomData().get("place") != null) {
			return event.getCustomData().get("place").toString();
		} else {
			return null;
		}
	}

	public static Long toDateTimeLong(SimpleDateFormat sdf, String date_string) {
		Date date;
		Long mills = null;
		try {
			date = (Date) sdf.parse(date_string);
			mills = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mills;
	}

	public static CharSequence eventDatesString(DateFormat sdf, Long fromTime, Long toTime) {
		String res = sdf.format(new Date(fromTime));
		if (toTime != null && toTime != fromTime) {
			Calendar f = Calendar.getInstance();
			f.setTimeInMillis(fromTime);
			Calendar t = Calendar.getInstance();
			t.setTimeInMillis(toTime);
			if (t.get(Calendar.DATE) != f.get(Calendar.DATE)) {
				res += " - " + sdf.format(new Date(toTime));
			}
		}
		return res;
	}

	// public static ArrayList<String> createFakeDateGroupList(){
	// ArrayList<String> groupList = new ArrayList<String>();
	// groupList.add("Oggi 29/10/2013");
	// groupList.add("Domani 30/10/2013");
	// groupList.add("Giovedi 31/10/2013");
	// return groupList;
	// }
	//
	//
	// public static Map<String, List<ExplorerObject>>
	// createFakeEventCollection(List<String> dateGroupList ) {
	//
	// List<ExplorerObject> eventList = getFakeExplorerObjects();
	// Map<String, List<ExplorerObject>> eventCollection = new
	// LinkedHashMap<String, List<ExplorerObject>>();
	// List<ExplorerObject> childList;
	// try {
	// eventList = new
	// ArrayList<ExplorerObject>(DTHelper.getEventsByCategories(0, 10,
	// CategoryHelper.CAT_SOCIALE));
	// // eventList = new ArrayList<ExplorerObject>(DTHelper.getEvents(0, 10,
	// CategoryHelper.CAT_SOCIALE));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// // preparing laptops collection(child)
	// for (String event_date : dateGroupList) {
	// childList = new ArrayList<ExplorerObject>();
	// if (event_date.equals("Oggi 29/10/2013")) {
	// childList.add(eventList.get(0));
	// childList.add(eventList.get(1));
	// } else if (event_date.equals("Domani 30/10/2013"))
	// childList.add(eventList.get(2));
	// else if (event_date.equals("Giovedi 31/10/2013")){
	// childList.add(eventList.get(3));
	// childList.add(eventList.get(4));
	// }
	//
	// eventCollection.put(event_date, childList);
	// }
	//
	// return eventCollection;
	// }

	private static List<ExplorerObject> loadChild(ExplorerObject[] eventsByDate) {
		List<ExplorerObject> childList = new ArrayList<ExplorerObject>();
		for (ExplorerObject event : eventsByDate)
			childList.add(event);
		return childList;
	}

	// public static List<ExplorerObject> getFakeExplorerObjects(){
	//
	// List<ExplorerObject> fake_events = new ArrayList<ExplorerObject>();
	// ExplorerObject fake_event; new ExplorerObject();
	// Map<String, Object> customData = new HashMap<String, Object>();
	// Map<String,Object> contacts = new HashMap<String, Object>();
	// CommunityData communityData = new CommunityData();
	//
	// //create fake event object 1
	// fake_event = new ExplorerObject();
	//
	//
	// //set basic info
	// fake_event.setTitle("Roverunning training");
	// fake_event.setWhenWhere("tutti i marted� con inizio il 14 - 21 - 28 gennaio, 4 - 11 - 18 - 25 febbraio, 4 - 11 - 18 - 25 marzo, ritrovo nella piazza del Mart ore 18.00");
	// fake_event.setOrigin("Assessorato allo Sport, US Quercia, NW Arcobaleno");
	// //the source
	// fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT,
	// "17/1/2014 08:30 PM"));
	// //fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT,
	// "17/1/2014 10:30 PM"));
	// fake_event.setId("1");
	// fake_event.setDescription("percorrerovereto. Vuoi imparare a correre? A camminare? Vuoi migliorare la tua attivit� di runner? Cerchi un'opportunit� per correre/camminare in compagnia? "
	// +
	// "La partecipazione � gratuita e aperta a tutti i principianti, amatori e agonisti");
	// String site_url = new
	// String("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/Roverunning-training6");
	// String img_url = new
	// String("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/roverunning-training6/124779-4-ita-IT/Roverunning-training_medium.jpg");
	// fake_event.setImage(img_url);
	// fake_event.setWebsiteUrl(site_url);
	//
	// //set location and address
	// double[] loc = {45.890960000000000000,11.040139899999986};
	// fake_event.setLocation(loc);
	// Address address = new Address();
	// address.setCitta("Rovereto");
	// address.setLuogo("Palasport Marchetti");
	// address.setVia("Corso Bettini, 52");
	// fake_event.setAddress(address);
	//
	// //set contacts
	// String[] telefono = new String[]{"0461235678", "3345678910"};
	// String[] email = new String[]{"xxx@comune.rovereto.it"};
	// contacts.put("telefono", telefono);
	// contacts.put("email", email);
	// fake_event.setContacts(contacts);
	//
	// //set community data
	// List<String> tags = Arrays.asList(new String[]{"sport", "calcio"});
	// communityData.setTags(tags);
	// communityData.setAttendees(5);
	// communityData.setAverageRating(3);
	// fake_event.setCommunityData(communityData);
	//
	//
	// //set custom data
	// customData.put("Tipo di luogo", "aperto");
	// customData.put("Accesso", "libero");
	// customData.put("Propabilita dell'evento", "confermato");
	// customData.put("Lingua principale ", "Italiano");
	// customData.put("Abbigliamento consigliato", "sportivo");
	// customData.put("Abbigliamento consigliato", "sportivo");
	// fake_event.setCustomData(customData);
	//
	// fake_events.add(fake_event);
	//
	// //create fake event object 2
	// telefono = new String[]{"0464565880"};
	// email = new String[]{"xxx@comune.rovereto.it"};
	// tags = Arrays.asList(new String[]{"sport", "pallavolo"});
	// site_url = new
	// String("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/24-TORNEO-DI-NATALE-Pallavolo-Femminile");
	// img_url = new
	// String("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/24-torneo-di-natale-pallavolo-femminile/123469-1-ita-IT/24-TORNEO-DI-NATALE-Pallavolo-Femminile_medium.jpg");
	//
	//
	// address = new Address();
	// address.setCitta("Rovereto");
	// address.setLuogo("Palasport e palestre");
	// address.setVia("Corso Bettini, 52");
	//
	// customData.clear();
	// customData.put("Tipo di luogo", "chiuso");
	// customData.put("Accesso", "a pagamento");
	// customData.put("Propabilita dell'evento", "confermato");
	// customData.put("Lingua principale ", "Italiano");
	// customData.put("Abbigliamento consigliato", "sportivo");
	//
	// //set community data
	// tags = Arrays.asList(new String[]{"sport", "calcio"});
	// communityData.setTags(tags);
	// communityData.setAttendees(5);
	// communityData.setAverageRating(3);
	// fake_event.setCommunityData(communityData);
	//
	// fake_event = new ExplorerObject();
	// fake_event.setAddress(address);
	// fake_event.setAddress(address);
	//
	// fake_event.setWhenWhere("whenwhere a");
	// fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
	// fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "28/12/2013"));
	// fake_event.setId("2");
	// fake_event.setDescription("description 1");
	// fake_event.setTitle("24esimo TORNEO DI NATALE Pallavolo Femminile");
	// fake_event.setCustomData(customData);
	// fake_event.setImage(img_url);
	// fake_event.setWebsiteUrl(site_url);
	// contacts.put("telefono", telefono);
	// contacts.put("email", email);
	// contacts.put("tags", tags);
	// fake_event.setContacts(contacts);
	// fake_events.add(fake_event);
	//
	// //create fake event object 3
	// telefono = new String[]{"0464565880"};
	// email = new String[]{"xxx@comune.rovereto.it"};
	// tags = Arrays.asList(new String[]{"sport", "pallavolo"});
	// site_url = new
	// String("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/24-TORNEO-DI-NATALE-Pallavolo-Femminile");
	// img_url = new
	// String("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/24-torneo-di-natale-pallavolo-femminile/123469-1-ita-IT/24-TORNEO-DI-NATALE-Pallavolo-Femminile_medium.jpg");
	// address = new Address();
	// address.setCitta("Rovereto");
	// address.setLuogo("Palasport e palestre");
	// address.setVia("Corso Bettini, 52");
	//
	// customData.put("Tipo di luogo", "chiuso");
	// customData.put("Accesso", "a pagamento");
	// customData.put("Propabilita dell'evento", "confermato");
	// customData.put("Lingua principale ", "Italiano");
	// customData.put("Abbigliamento consigliato", "sportivo");
	//
	// <<<<<<< HEAD
	// fake_event = new EventObject();
	// =======
	// //set community data
	// tags = Arrays.asList(new String[]{"sport", "calcio"});
	// communityData.setTags(tags);
	// communityData.setAttendees(5);
	// communityData.setAverageRating(3);
	// fake_event.setCommunityData(communityData);
	//
	// fake_event = new ExplorerObject();
	// >>>>>>> branch 'master' of
	// https://github.com/smartcampuslab/smartcampus.vas.inbici.android.git
	// fake_event.setAddress(address);
	// fake_event.setAddress(address);
	//
	// fake_event.setWhenWhere("whenwhere a");
	// fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
	// fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "28/12/2013"));
	// fake_event.setId("3");
	// fake_event.setDescription("description 1");
	// fake_event.setTitle("titolo 3");
	// fake_event.setCustomData(customData);
	// fake_event.setImage(img_url);
	// fake_event.setWebsiteUrl(site_url);
	// contacts.put("telefono", telefono);
	// contacts.put("email", email);
	// contacts.put("tags", tags);
	// fake_event.setContacts(contacts);
	// fake_events.add(fake_event);
	//
	// //create fake event object 4
	// telefono = new String[]{"0464565880"};
	// email = new String[]{"xxx@comune.rovereto.it"};
	// tags = Arrays.asList(new String[]{"sport", "pallavolo"});
	// site_url = new
	// String("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/24-TORNEO-DI-NATALE-Pallavolo-Femminile");
	// img_url = new
	// String("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/24-torneo-di-natale-pallavolo-femminile/123469-1-ita-IT/24-TORNEO-DI-NATALE-Pallavolo-Femminile_medium.jpg");
	// address = new Address();
	// address.setCitta("Rovereto");
	// address.setLuogo("Palasport e palestre");
	// address.setVia("Corso Bettini, 52");
	//
	// customData.put("Tipo di luogo", "chiuso");
	// customData.put("Accesso", "a pagamento");
	// customData.put("Propabilita dell'evento", "confermato");
	// customData.put("Lingua principale ", "Italiano");
	// customData.put("Abbigliamento consigliato", "sportivo");
	//
	//
	// //set community data
	// tags = Arrays.asList(new String[]{"sport", "calcio"});
	// communityData.setTags(tags);
	// communityData.setAttendees(5);
	// communityData.setAverageRating(3);
	// fake_event.setCommunityData(communityData);
	// fake_event = new ExplorerObject();
	// fake_event.setAddress(address);
	// fake_event.setAddress(address);
	//
	// fake_event.setWhenWhere("whenwhere a");
	// fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
	// fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "28/12/2013"));
	// fake_event.setId("4");
	// fake_event.setDescription("description 1");
	// fake_event.setTitle("saggio di danza");
	// fake_event.setCustomData(customData);
	// fake_event.setImage(img_url);
	// fake_event.setWebsiteUrl(site_url);
	// contacts.put("telefono", telefono);
	// contacts.put("email", email);
	// contacts.put("tags", tags);
	// fake_event.setContacts(contacts);
	// fake_events.add(fake_event);
	//
	// // //create fake event object 5
	// telefono = new String[]{"0464565880"};
	// email = new String[]{"xxx@comune.rovereto.it"};
	// tags = Arrays.asList(new String[]{"sport", "pallavolo"});
	// site_url = new
	// String("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/24-TORNEO-DI-NATALE-Pallavolo-Femminile");
	// img_url = new
	// String("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/24-torneo-di-natale-pallavolo-femminile/123469-1-ita-IT/24-TORNEO-DI-NATALE-Pallavolo-Femminile_medium.jpg");
	// address = new Address();
	// address.setCitta("Rovereto");
	// address.setLuogo("Palasport e palestre");
	// address.setVia("Corso Bettini, 52");
	//
	// customData.put("Tipo di luogo", "chiuso");
	// customData.put("Accesso", "a pagamento");
	// customData.put("Propabilita dell'evento", "confermato");
	// customData.put("Lingua principale ", "Italiano");
	// customData.put("Abbigliamento consigliato", "sportivo");
	//
	// //set community data
	// tags = Arrays.asList(new String[]{"sport", "calcio"});
	// communityData.setTags(tags);
	// communityData.setAttendees(5);
	// communityData.setAverageRating(3);
	// fake_event.setCommunityData(communityData);
	//
	// <<<<<<< HEAD
	// fake_event = new EventObject();
	// =======
	// fake_event = new ExplorerObject();
	// >>>>>>> branch 'master' of
	// https://github.com/smartcampuslab/smartcampus.vas.inbici.android.git
	// fake_event.setAddress(address);
	// fake_event.setAddress(address);
	//
	// fake_event.setWhenWhere("whenwhere a");
	// fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
	// fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "29/12/2013"));
	// fake_event.setId("5");
	// fake_event.setDescription("description 1");
	// fake_event.setTitle("Hockey su ghiaccio");
	// fake_event.setCustomData(customData);
	// fake_event.setImage(img_url);
	// fake_event.setWebsiteUrl(site_url);
	// contacts.put("telefono", telefono);
	// contacts.put("email", email);
	// contacts.put("tags", tags);
	// fake_event.setContacts(contacts);
	// fake_events.add(fake_event);
	//
	// return fake_events;
	//
	// }

	// public static ExplorerObject
	// getFakeLocalExplorerObject(List<ExplorerObject> events, String id){
	//
	// ExplorerObject fake_event = null;
	// for (ExplorerObject event: events){
	// if (event.getId()==id) {
	// return event;
	// }
	// }
	// return fake_event;
	// }

	// public static String getDateString(Context context, Long fromTime,
	// SimpleDateFormat sdf, boolean uppercase, boolean dayweek) {
	//
	// String stringEvent = (sdf.format(new Date(fromTime)));
	//
	// if (dayweek){
	// Date dateToday = new Date();
	// String stringToday = (sdf.format(dateToday));
	// Calendar cal = Calendar.getInstance();
	// cal.setTime(dateToday);
	// cal.add(Calendar.DAY_OF_YEAR, 1); // <--
	// Date tomorrow = cal.getTime();
	// String stringTomorrow = (sdf.format(tomorrow));
	// // check actual date
	// if (stringToday.equals(stringEvent)) {
	// // if equal put the Today string
	// return getDateFormatted(context.getString(R.string.list_event_today) +
	// " " + stringToday, uppercase);
	// } else if (stringTomorrow.equals(stringEvent)) {
	// // else if it's tomorrow, cat that string
	// return getDateFormatted(context.getString(R.string.list_event_tomorrow) +
	// " " + stringTomorrow, uppercase);
	// }
	// // else put the day's name
	// else
	// return getDateFormatted(DATE_FORMAT_2_with_dayweek.format(new
	// Date(fromTime)), uppercase);
	// }
	// else{
	// Log.i("FORMAT", "Utils --> no dayweek date formatted: " + stringEvent +
	// "!!");
	// return stringEvent;
	// }
	// }

	public static String[] getDateTimeString(Context context, Long fromTime, SimpleDateFormat sdf, boolean uppercase,
			boolean dayweek) {

		String[] date_time = { "", "" };
		date_time[1] = "";
		String dateAndTime = sdf.format(new Date(fromTime));
		String date = dateAndTime;

		if (dateAndTime.contains(":")) {
			// there is a time
			date = dateAndTime.substring(0, dateAndTime.lastIndexOf(" "));
			date_time[1] = dateAndTime.substring(dateAndTime.lastIndexOf(" ") + 1);
		}

		if (dayweek) {
			Date dateToday = new Date();
			String stringToday = (sdf.format(dateToday));
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateToday);
			cal.add(Calendar.DAY_OF_YEAR, 1); // <--
			Date tomorrow = cal.getTime();
			String stringTomorrow = (sdf.format(tomorrow));
			// check actual date
			if (stringToday.equals(date)) {
				// if equal put the Today string
				date_time[0] = getDateFormatted(context.getString(R.string.list_event_today) + " " + stringToday,
						uppercase);
			} else if (stringTomorrow.equals(date)) {
				// else if it's tomorrow, cat that string
				date_time[0] = getDateFormatted(context.getString(R.string.list_event_tomorrow) + " " + stringTomorrow,
						uppercase);
			}
			// else put the day's name
			else {
				date_time[0] = getDateFormatted(DATE_FORMAT_2_with_dayweek.format(new Date(fromTime)), uppercase);
			}
		} else {
			date_time[0] = date;
		}

		//Log.i("FORMAT", "Utils --> date formatted: " + date_time[0] + "!!");
		//Log.i("FORMAT", "Utils --> time formatted: " + date_time[1] + "!!");

		return date_time;

	}

	private static String getDateFormatted(String date, boolean uppercase) {
		// Log.i("FORMAT", "Utils --> initial string : " + date + "!!");
		String date_formatted = new String("");
		String[] dateformatted_split = date.split(" ");
		for (int i = 0; i < dateformatted_split.length; i++) {
			String piece = dateformatted_split[i];
			if (uppercase) {
				piece = (i == 0) ? piece.substring(0, 1).toUpperCase() + piece.substring(1) + "," : piece;
				piece = (i == 2) ? piece.substring(0, 1).toUpperCase() + piece.substring(1) : piece;
			} else
				piece = (i == 0) ? piece + "," : piece;

			// Log.i("FORMAT", "Utils --> string split: " + piece + "!!");
			date_formatted = date_formatted + piece + " ";
		}
		return date_formatted;
	}

	public static boolean validFromDateTime(Date fromDate, Date fromTime) {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		// minutes in the past span
		now.add(Calendar.MINUTE, Utils.PAST_MINUTES_SPAN);

		Calendar time = Calendar.getInstance();
		time.setTime(fromTime);
		Calendar from = Calendar.getInstance();
		from.setTime(fromDate);
		from.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		from.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);

		return from.compareTo(now) < 0 ? false : true;
	}

	//	public static List<ToKnow> toKnowMapToList(Map<String, String> map) {
	//		List<ToKnow> list = new ArrayList<ToKnow>();
	//
	//		for (Entry<String, String> entry : map.entrySet()) {
	//			ToKnow toKnow = new ToKnow(entry.getKey(), entry.getValue());
	//			list.add(toKnow);
	//		}
	//
	//		return list;
	//	}



	public static List<ToKnow> toKnowMapToList(Map<String, List<String>> map) {


		List<ToKnow> list = new ArrayList<ToKnow>();

		for (Entry<String, List<String>> entry : map.entrySet()) {

			List<String> values = new LinkedList<String>((List<String>) entry.getValue());
			values.remove("");

			if (entry.getKey().startsWith("_toknow_"))
				list.add((values.size()!=0) ? ToKnow.newCustomDataAttributeField(entry.getKey(), false, 2) : 
					ToKnow.newCustomDataAttributeField(entry.getKey(), false, 3));
			else			
				list.add((values.size()!=0) ? ToKnow.newCustomDataAttributeField(entry.getKey(), true, 2) :
					ToKnow.newCustomDataAttributeField(entry.getKey(), true, 3));


			for (int i = 0; i < values.size(); i++) {
				String value = values.get(i);

				if (i == (values.size() - 1)) {
					// Last item...
					list.add(ToKnow.newCustomDataValueField(value,3));
				}else{
					list.add(ToKnow.newCustomDataValueField(value,2));
				}
			}


		}

		return list;
	}













	public static Map<String, List<String>> toKnowListToMap(List<ToKnow> list) {

		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();

		String previousAttrName = null;

		if (list != null) {

			previousAttrName = list.get(0).getName();
			List<String> values = new ArrayList<String>();		
			for (ToKnow toKnow : list) {

				String currentAttrName = (toKnow.getType().matches(Constants.CUSTOM_TOKNOW_TYPE_ATTRIBUTE)) ? toKnow.getName() : previousAttrName;  

				if ((currentAttrName.matches(previousAttrName)) && (toKnow.getType().matches(Constants.CUSTOM_TOKNOW_TYPE_VALUE)))
					values.add(toKnow.getName());

				if (!currentAttrName.matches(previousAttrName)){
					map.put(previousAttrName, values);
					values = new ArrayList<String>();		
					previousAttrName = currentAttrName;
				} 
			}
		}

		return map;
	}



	public static   Map<String, List<String>> convert(Map<String, String> oldMap) {
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		for (String key : oldMap.keySet()) {
			ret.put(key, Arrays.asList(new String[]{oldMap.get(key)}));
		}
		return ret;
	}


	public static boolean isOldMapType(Map<String,Object> map){

		boolean isOld = false;

		for (Entry<String, Object> entry : map.entrySet()) {
			if(!(entry.getValue() instanceof List<?>)) {
				isOld=true;
				break;
			}
		}
		return isOld;
	}


	public static Map<String,List<String>> getCustomToKnowDataFromEvent(ExplorerObject event){
		Map<String,List<String>> toKnowMap = null;
		if (event.getCustomData().containsKey(Constants.CUSTOM_TOKNOW)){

			//eventually convert the old map type with the new one
			if (Utils.isOldMapType((Map<String,Object>) event.getCustomData().get(Constants.CUSTOM_TOKNOW))){
				toKnowMap = Utils.convert((Map<String,String>) event.getCustomData().get(Constants.CUSTOM_TOKNOW));
			}
			else{
				toKnowMap = (Map<String,List<String>>) event.getCustomData().get(Constants.CUSTOM_TOKNOW);
			}
		}


		return toKnowMap;
	}



	/**
	 * This is used to check the given email is valid or not.
	 * 
	 * @param url
	 * @return
	 */

	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}

	/**
	 * This is used to check the given URL is valid or not.
	 * 
	 * @param url
	 * @return
	 */

	public final static boolean isValidUrl(String url) {
		Pattern p = Patterns.WEB_URL;
		Matcher m = p.matcher(url);
		if (m.matches())
			return true;
		else
			return false;
	}

	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isSameDay(cal1, cal2);
	}

	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
				cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
				cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}

	public static List<Date> getDatesBetweenInterval(Date dateInitial, Date dateFinal) {
		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dateInitial);

		while (!calendar.getTime().after(dateFinal)) {
			Date result = calendar.getTime();
			dates.add(result);
			calendar.add(Calendar.DATE, 1);
		}
		return dates;
	}



	//delete an unwanted word from a sentence
	public static String removeWord(String unwanted, String sentence)
	{
		return (sentence.indexOf(unwanted) != -1) ? sentence.replace(unwanted,"").trim() : sentence;

	}


	//delete a list of unwanted words from a sentence
	public static String removeWords(List<String> unwanted, String sentence)
	{
		for (String word : unwanted)
			sentence = removeWord(word, sentence);
		return sentence;
	}

	public static OSMAddress getOsmAddressFromAddress(android.location.Address address) {
		OSMAddress returnAddress = new OSMAddress();
		if (address!=null){

			//city
			Map<String,String> cities = new HashMap<String, String>();
			cities.put("", address.getLocality());
			returnAddress.setCity(cities);

			//name
			returnAddress.setName(address.getLocality());
			//street
			returnAddress.setStreet(address.getAddressLine(0));
			//location
			double[] addressLocation = {address.getLatitude(),address.getLongitude()};
			returnAddress.setLocation(addressLocation);

			return returnAddress;
		}
		return null;
	}


	public static void hideKeyboard (Activity activity){
		// hide keyboard if it is still open
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				activity.findViewById(R.id.content_frame).getWindowToken(),
				0);
	}
	

	/**
	 * @param mTrack
	 * @return
	 */
	public static Address getTrackAsGoogleAddress(TrackObject mTrack) {
		Address a = new Address(Locale.getDefault());
		a.setLatitude(mTrack.startingPoint().latitude);
		a.setLongitude(mTrack.startingPoint().longitude);
		a.setAddressLine(0, mTrack.getTitle());
		return a;
	}

}
