package eu.iescities.pilot.rovereto.inbici.entities.track;


public interface ListByOrder {
	public static final int ORDER_BY_ALPHABETICAL = 0;
	public static final int ORDER_BY_DISTANCE = 1;
	public static final int ORDER_BY_LENGHT = 2;
	public static final int ORDER_BY_ALTITUDE_GAP = 3;
	public static final int ORDER_BY_AVG_TIME = 4;
	
	public static final String ACTUAL_ORDER="actual order";
	public static final String NEW_ORDER="new number";
}
