package eu.iescities.pilot.rovereto.inbici.entities.track.logger.Overlay;

import java.util.LinkedList;
import java.util.List;

import org.osmdroid.views.overlay.Overlay;

import android.graphics.Canvas;
import android.os.Handler;

import com.google.android.maps.GeoPoint;

import eu.iescities.pilot.rovereto.inbici.entities.track.logger.SegmentRendering;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.map.LoggerMap;

public class BitmapSegmentsOverlay extends AsyncOverlay
{
   private static final String TAG = "GG.BitmapSegmentsOverlay";

   List<SegmentRendering> mOverlays;
   Handler mOverlayHandler;

   public BitmapSegmentsOverlay(LoggerMap loggermap, Handler handler)
   {
      super(loggermap, handler);
      mOverlays = new LinkedList<SegmentRendering>();
      mOverlayHandler = handler;
   }

   @Override
   synchronized protected void redrawOffscreen(Canvas asyncBuffer, LoggerMap loggermap)
   {
      for (SegmentRendering segment : mOverlays)
      {
         segment.draw(asyncBuffer);
      }
   }

   @Override
   public synchronized void scheduleRecalculation()
   {
      for (SegmentRendering segment : mOverlays)
      {
         segment.calculateMedia();
         segment.calculateTrack();
      }
   }

   @Override
   synchronized protected boolean commonOnTap(GeoPoint tappedGeoPoint)
   {
      boolean handled = false;
      for (SegmentRendering segment : mOverlays)
      {
         if (!handled)
         {
            handled = segment.commonOnTap(tappedGeoPoint);
         }
      }
      return handled;
   }

   synchronized public void addSegment(SegmentRendering segment)
   {
      segment.setBitmapHolder(this);
      mOverlays.add(segment);
   }

   synchronized public void clearSegments()
   {
      for (SegmentRendering segment : mOverlays)
      {
         segment.closeResources();
      }
      mOverlays.clear();
      reset();
   }

   synchronized public void setTrackColoringMethod(int color, double speed, double height)
   {
      for (SegmentRendering segment : mOverlays)
      {
         segment.setTrackColoringMethod(color, speed, height);
      }
      scheduleRecalculation();
   }

   public int size()
   {
      return mOverlays.size();
   }

@Override
public Overlay getOSMOverlay() {
	return super.getOSMOverlay();
}

@Override
public com.mapquest.android.maps.Overlay getMapQuestOverlay() {
	return super.getMapQuestOverlay();
}
}
