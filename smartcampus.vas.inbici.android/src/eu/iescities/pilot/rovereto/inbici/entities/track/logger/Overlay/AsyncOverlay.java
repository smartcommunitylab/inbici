package eu.iescities.pilot.rovereto.inbici.entities.track.logger.Overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import eu.iescities.pilot.rovereto.inbici.entities.track.logger.LoggerMap;

public abstract class AsyncOverlay extends Overlay implements OverlayProvider
{
   private static final int OFFSET = 20;

   private static final String TAG = "GG.AsyncOverlay";

   /**
    * Handler provided by the MapActivity to recalculate graphics
    */
   private Handler mHandler;

   private GeoPoint mGeoTopLeft;

   private GeoPoint mGeoBottumRight;

   private int mWidth;

   private int mHeight;

   private Bitmap mActiveBitmap;

   private GeoPoint mActiveTopLeft;

   private Point mActivePointTopLeft;

   private Bitmap mCalculationBitmap;

   private Paint mPaint;

   private LoggerMap mLoggerMap;



   private int mActiveZoomLevel;

   private Runnable mBitmapUpdater = new Runnable()
   {
      @Override
      public void run()
      {
         postedBitmapUpdater = false;
         mCalculationBitmap.eraseColor(Color.TRANSPARENT);
         mGeoTopLeft = mLoggerMap.fromPixels(0, 0);
         mGeoBottumRight = mLoggerMap.fromPixels(mWidth, mHeight);
         Canvas calculationCanvas = new Canvas(mCalculationBitmap);
         redrawOffscreen(calculationCanvas, mLoggerMap);
         synchronized (mActiveBitmap)
         {
            Bitmap oldActiveBitmap = mActiveBitmap;
            mActiveBitmap = mCalculationBitmap;
            mActiveTopLeft = mGeoTopLeft;
            mCalculationBitmap = oldActiveBitmap;
         }
         mLoggerMap.postInvalidate();
      }
   };

   private boolean postedBitmapUpdater;

   AsyncOverlay(LoggerMap loggermap, Handler handler)
   {
      mLoggerMap = loggermap;
      mHandler = handler;
      mWidth = 1;
      mHeight = 1;
      mPaint = new Paint();
      mActiveZoomLevel = -1;
      mActiveBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
      mActiveTopLeft = new GeoPoint(0, 0);
      mActivePointTopLeft = new Point();
      mCalculationBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

   }

   protected void reset()
   {
      synchronized (mActiveBitmap)
      {
         mCalculationBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
         mActiveBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
      }
   }

   protected void considerRedrawOffscreen()
   {
      int oldZoomLevel = mActiveZoomLevel;
      mActiveZoomLevel = mLoggerMap.getZoomLevel();

      boolean needNewCalculation = false;

      if (mCalculationBitmap.getWidth() != mWidth || mCalculationBitmap.getHeight() != mHeight)
      {
         mCalculationBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
         needNewCalculation = true;
      }

      boolean unaligned = isOutAlignment();
      if (needNewCalculation || mActiveZoomLevel != oldZoomLevel || unaligned)
      {
         scheduleRecalculation();
      }
   }

   private boolean isOutAlignment()
   {
      Point screenPoint = new Point(0, 0);
      if (mGeoTopLeft != null)
      {
         mLoggerMap.toPixels(mGeoTopLeft, screenPoint);
      }
      return mGeoTopLeft == null || mGeoBottumRight == null || screenPoint.x > OFFSET || screenPoint.y > OFFSET || screenPoint.x < -OFFSET
            || screenPoint.y < -OFFSET;
   }

   public void onDateOverlayChanged()
   {
      if (!postedBitmapUpdater)
      {
         postedBitmapUpdater = true;
         mHandler.post(mBitmapUpdater);
      }
   }

   protected abstract void redrawOffscreen(Canvas asyncBuffer, LoggerMap loggermap);

   protected abstract void scheduleRecalculation();

   /**
    * {@inheritDoc}
    */
   @Override
   public void draw(Canvas canvas, MapView mapView, boolean shadow)
   {
      if (!shadow)
      {
         draw(canvas);
      }
   }

   private void draw(Canvas canvas)
   {
      mWidth = canvas.getWidth();
      mHeight = canvas.getHeight();
      considerRedrawOffscreen();

      if (mActiveBitmap.getWidth() > 1)
      {
         synchronized (mActiveBitmap)
         {
            mLoggerMap.toPixels(mActiveTopLeft, mActivePointTopLeft);
            canvas.drawBitmap(mActiveBitmap, mActivePointTopLeft.x, mActivePointTopLeft.y, mPaint);
         }
      }
   }

   protected boolean isPointOnScreen(Point point)
   {
      return point.x < 0 || point.y < 0 || point.x > mWidth || point.y > mHeight;
   }

   @Override
   public boolean onTap(GeoPoint tappedGeoPoint, MapView mapview)
   {
      return commonOnTap(tappedGeoPoint);
   }
   
   /**************************************/
   /** Multi map support **/
   /**************************************/

   @Override
   public Overlay getGoogleOverlay()
   {
      return this;
   }

  
   protected abstract boolean commonOnTap(GeoPoint tappedGeoPoint);

  
}
