package eu.iescities.pilot.rovereto.inbici.entities.event.multimedia;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.Multimedia;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;

public class Fragment_EvDetail_Multimedia extends Fragment {

	private Context mContext;
	private String mEventId;
	private ExplorerObject mEvent = null;

	private GridView gridView;
	private EventDetailMultimediaAdapter adapter;

	public static Fragment_EvDetail_Multimedia newInstance(String event_id) {
		Fragment_EvDetail_Multimedia f = new Fragment_EvDetail_Multimedia();
		Bundle b = new Bundle();
		b.putString(Utils.ARG_EVENT_ID, event_id);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.mContext = this.getActivity();

		if (savedInstanceState == null && getArguments() != null) {
			mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
			mEvent = DTHelper.findEventById(mEventId);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_ev_detail_multimedia, container, false);
		gridView = (GridView) view.findViewById(R.id.multimedia_gridView);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		mEvent = getEvent();

		adapter = new EventDetailMultimediaAdapter(getActivity(), R.layout.multimedia_entry, mEventId);
		gridView.setAdapter(adapter);

		/* FOR TEST PURPOSES ONLY */
		List<String> videosUrls = Arrays.asList("http://www.youtube.com/watch?v=UaN5O7pym6A",
				"http://www.youtube.com/watch?v=mTxyzmTeZaQ", "http://www.youtube.com/watch?v=RnqT_LPBf1A",
				"http://www.youtube.com/watch?v=Co3w_ZxcO1U", "http://youtu.be/BmKGpKRRyTQ?t=3s");

		for (String url : videosUrls) {
			String videoId = null;
			String thumbnailUrl = null;

			if (url.contains("youtube")) {
				videoId = url.substring(url.indexOf("v=") + ("v=").length());
				if (videoId.indexOf("&") > -1) {
					videoId = videoId.substring(0, videoId.indexOf("&"));
				}
				thumbnailUrl = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
			} else if (url.contains("youtu.be")) {
				videoId = url.substring(url.indexOf("youtu.be") + ("youtu.be/").length());
				if (videoId.indexOf("?") > -1) {
					videoId = videoId.substring(0, videoId.indexOf("?"));
				}
				thumbnailUrl = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
			}

			if (videoId != null && thumbnailUrl != null) {
				Multimedia m = new Multimedia();
				m.setName(videoId);
				m.setThumbnailUrl(thumbnailUrl);
				m.setUrl(url);
				adapter.add(m);
			}
		}

		adapter.notifyDataSetChanged();
		/* /FOR TEST PURPOSES ONLY */
	}

	private ExplorerObject getEvent() {
		if (mEventId == null) {
			mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
		}

		if (mEventId != null) {
			mEvent = DTHelper.findEventById(mEventId);
		}

		return mEvent;
	}

}
