package eu.iescities.pilot.rovereto.inbici.entities.event.multimedia;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.inbici.InBiciApplication;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.Multimedia;
import eu.iescities.pilot.rovereto.inbici.R;

public class EventDetailMultimediaAdapter extends ArrayAdapter<Multimedia> {

	private Context mContext;
	private int layoutResourceId;
	private String mEventId;

	public EventDetailMultimediaAdapter(Context mContext, int layoutResourceId, String mEventId) {
		super(mContext, layoutResourceId);
		this.mContext = mContext;
		this.layoutResourceId = layoutResourceId;
		this.mEventId = mEventId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		MultimediaHolder holder = null;

		final Multimedia multimedia = getItem(position);

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new MultimediaHolder();
			holder.multimediaThumbnail = (ImageView) row.findViewById(R.id.multimediaThumbnail);
			holder.multimediaName = (TextView) row.findViewById(R.id.multimediaName);
			row.setTag(holder);
		} else {
			holder = (MultimediaHolder) row.getTag();
		}

		InBiciApplication.imageLoader.displayImage(multimedia.getThumbnailUrl(), holder.multimediaThumbnail);
		holder.multimediaName.setText(multimedia.getName());

		return row;
	}

	public static class MultimediaHolder {
		ImageView multimediaThumbnail;
		TextView multimediaName;
	}

	// private class UpdateEventProcessor extends
	// AbstractAsyncTaskProcessor<ExplorerObject, Boolean> {
	//
	// private ToKnow toKnow;
	//
	// public UpdateEventProcessor(Activity activity, ToKnow toKnow) {
	// super(activity);
	// this.toKnow = toKnow;
	// }
	//
	// @Override
	// public Boolean performAction(ExplorerObject... params) throws
	// SecurityException, Exception {
	// // to be enabled when the connection with the server is ok
	// return DTHelper.saveEvent(params[0]);
	// }
	//
	// @Override
	// public void handleResult(Boolean result) {
	// if (getContext() != null) {
	// if (result) {
	// Toast.makeText(getContext(), R.string.event_create_success,
	// Toast.LENGTH_SHORT).show();
	// } else {
	// Toast.makeText(getContext(), R.string.update_success,
	// Toast.LENGTH_SHORT).show();
	// }
	// remove(toKnow);
	// // getActivity().getSupportFragmentManager().popBackStack();
	// }
	// }
	// }

}
