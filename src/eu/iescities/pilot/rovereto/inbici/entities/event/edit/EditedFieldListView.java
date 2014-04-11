package eu.iescities.pilot.rovereto.inbici.entities.event.edit;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

public class EditedFieldListView extends ListView {
	
	
	private static final int ROW_HEIGHT=89;

	private android.view.ViewGroup.LayoutParams params;
	private int old_count = 0;

	public EditedFieldListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (getCount() != old_count) {
			old_count = getCount();
			params = getLayoutParams();

			//params.height = getCount() * (old_count > 0 ? getChildAt(0).getHeight() : 0);
			if ((old_count > 0) && (getChildAt(0)!=null)){
				params.height = getCount() * getChildAt(0).getHeight();

			}else{
				if ((old_count > 0) && (getChildAt(0)==null))
					params.height = getCount() * ROW_HEIGHT;
				else
					params.height =0;
			}
			setLayoutParams(params);
		}

		super.onDraw(canvas);
	}

}