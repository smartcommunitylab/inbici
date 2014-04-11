package eu.iescities.pilot.rovereto.inbici.entities.event.community;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import eu.iescities.pilot.rovereto.inbici.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.inbici.custom.RatingHelper;
import eu.iescities.pilot.rovereto.inbici.custom.RatingHelper.RatingHandler;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.Rating;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.CommunityData;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.Review;
import eu.iescities.pilot.rovereto.inbici.entities.event.Fragment_EventDetails;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class Fragment_EvDetail_Community extends Fragment implements RefreshComments {
	CommentAdapter listAdapter;
	ExpandableListView expListView;
	View header;
	List<String> listCommentsHeader;
	HashMap<String, List<Review>> listCommentsChild;
	boolean rating_is_open = true;
	boolean attending_is_open = true;
	public ExplorerObject mEvent = null;
	private String mEventId;
	private float rate = 0;

	public static Fragment_EvDetail_Community newInstance(String event_id) {
		Fragment_EvDetail_Community f = new Fragment_EvDetail_Community();
		Bundle b = new Bundle();
		b.putString(Utils.ARG_EVENT_ID, event_id);
		f.setArguments(b);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_ev_detail_community, container, false);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser)
			updateCommentsList();
	}
	@Override
	public void onStart() {
		super.onStart();
		// get param
		// Bundle bundle = this.getArguments();
		setLayoutInteraction();
	}

	@Override
	public void onResume() {
		super.onResume();

		//		if (getParentFragment() instanceof Fragment_EventDetails) {
		//			Fragment_EventDetails parentFragment = (Fragment_EventDetails) getParentFragment();
		//			if (parentFragment.isCommentsRefreshNeeded()) {
		//				updateCommentsList();
		//			}
		//		}
	}

	private void setLayoutInteraction() {
		// header part
		header = getActivity().getLayoutInflater().inflate(R.layout.frag_ev_detail_community_header, null);
		// get the listview
		expListView = (ExpandableListView) getActivity().findViewById(R.id.list_comments);

		// setting list adapter
		if (expListView.getHeaderViewsCount() == 0) {
			expListView.addHeaderView(header);
		}
		expListView.setAdapter(listAdapter);
		setHeaderInteraction(header);
		setCommentListInteraction(expListView);
	}

	private void setCommentListInteraction(ExpandableListView expListView2) {
		listCommentsHeader = new ArrayList<String>();
		listCommentsChild = new HashMap<String, List<Review>>();
		listCommentsHeader.add(getString(R.string.label_comments));
		// get comment list
		List<Review> comments = new ArrayList<Review>();
		// setCommentAddInteraction();
		listCommentsChild.put(listCommentsHeader.get(0), comments);
		// Header, Child data

		listAdapter = new CommentAdapter(getActivity(), listCommentsHeader, listCommentsChild, getActivity(), mEvent,
				Fragment_EvDetail_Community.this);
	}

	public void updateCommentsList() {
		new SCAsyncTask<Void, Void, List<Review>>(getActivity(), new LoadCommentsProcessor(getActivity())).execute();
	}

	private class LoadCommentsProcessor extends AbstractAsyncTaskProcessor<Void, List<Review>> {

		public LoadCommentsProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public List<Review> performAction(Void... params) throws SecurityException, Exception {
			return DTHelper.loadReviews(getEvent().getId());
		}

		@Override
		public void handleResult(List<Review> result) {
			if (result == null || result.isEmpty()) {
				// ViewHelper.addEmptyListView(container);
			}
			listCommentsChild.clear();
			listCommentsChild.put(listCommentsHeader.get(0), result); // Header,
			listAdapter = new CommentAdapter(getActivity(), listCommentsHeader, listCommentsChild, getActivity(), mEvent,
					Fragment_EvDetail_Community.this);
			expListView.setAdapter(listAdapter);

			listAdapter.notifyDataSetChanged();
			//
			// listAdapter.getGroup(0).clear();
			// for (Review r : result) {
			// listAdapter.getGroup(0).add(r);
			// }
			// adapter.notifyDataSetChanged();
		}
	}

	private void setHeaderInteraction(View header) {
		setAttendingInteraction(header);
		setRatingInteraction(header);
	}

	private ExplorerObject getEvent() {
		if (mEventId == null) {
			mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
		}

		if (mEvent == null) {
			mEvent = DTHelper.findEventById(mEventId);
		}

		return mEvent;
	}

	private void setAttendingInteraction(View header) {
		// set attendeees number
		TextView mAttendees = (TextView) getActivity().findViewById(R.id.attending_number);
		if (getEvent().getCommunityData() != null) {
			mAttendees.setText(getEvent().getCommunityData().getAttendees().toString());
		}
		// set attending button
		ToggleButton mAttending_my = (ToggleButton) getActivity().findViewById(R.id.attending_my);
		if (getEvent().getCommunityData().getAttending() == null || getEvent().getCommunityData().getAttending().isEmpty()) {
			mAttending_my.setBackgroundResource(R.drawable.ic_monitor_off);
			mAttending_my.setChecked(false);
		} else {
			mAttending_my.setBackgroundResource(R.drawable.ic_monitor_on);
			mAttending_my.setChecked(true);
		}

		mAttending_my.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				new SCAsyncTask<Boolean, Void, ExplorerObject>(getActivity(), new AttendProcessor(getActivity(), buttonView))
				.execute(getEvent().getCommunityData().getAttending() == null
				|| getEvent().getCommunityData().getAttending().isEmpty());
			}
		});

		// set listener for rating
		RatingBar rating = (RatingBar) getView().findViewById(R.id.event_my_rating);
		rating.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					{
						ratingDialog();
					}
				}
				return true;
			}
		});

		updateRating();
	}

	private void ratingDialog() {
		float rating = (getEvent() != null && getEvent().getCommunityData() != null && getEvent().getCommunityData()
				.getAverageRating() > 0) ? getEvent().getCommunityData().getAverageRating() : 2.5f;
				RatingHelper
				.ratingDialog(getActivity(), rating, new RatingProcessor(getActivity()), R.string.rating_event_dialog_title);
	}

	private class RatingProcessor extends AbstractAsyncTaskProcessor<Integer, Integer> implements RatingHandler {
		public RatingProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public Integer performAction(Integer... params) throws SecurityException, Exception {
			return DTHelper.rate(getEvent(), params[0]);
		}

		@Override
		public void handleResult(Integer result) {
			mEvent = null;
			getEvent();
			updateRating();
			if (getActivity() != null)
				Toast.makeText(getActivity(), R.string.rating_success, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onRatingChanged(float rating) {
			new SCAsyncTask<Integer, Void, Integer>(getActivity(), this).execute((int) rating);
		}
	}

	private void updateRating() {
		if (this.getView() != null) {
			// set my rating
			RatingBar myRating = (RatingBar) getActivity().findViewById(R.id.event_my_rating);
			if (getEvent().getCommunityData() != null) {
				CommunityData cd = getEvent().getCommunityData();

				if (cd.getRatings() != null && !cd.getRatings().isEmpty()) {
					rate = 0;
					for (Rating rating : cd.getRatings()) {
						rate = rating.getValue();
					}
					myRating.setRating(rate);
				}

			}
			// set avg rating
			RatingBar avgRating = (RatingBar) getActivity().findViewById(R.id.event_avg_rating);
			if (getEvent().getCommunityData() != null) {
				avgRating.setRating(getEvent().getCommunityData().getAverageRating());
			}
			// set total rating
			TextView totRating = (TextView) getActivity().findViewById(R.id.event_total_rating);
			if (getEvent().getCommunityData() != null) {
				totRating.setText(Integer.toString(getEvent().getCommunityData().getRatingsCount()));
			}
			// set visible or invisible
			final ImageView attending_open = (ImageView) header.findViewById(R.id.attending_is_open);
			final ImageView attending_close = (ImageView) header.findViewById(R.id.attending_is_close);
			final LinearLayout attending_layout = (LinearLayout) header.findViewById(R.id.attending_layout);
			attending_open.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					attending_is_open = false;
					attending_layout.setVisibility(View.GONE);
					attending_open.setVisibility(View.GONE);
					attending_close.setVisibility(View.VISIBLE);
				}
			});
			attending_close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					attending_is_open = true;
					attending_layout.setVisibility(View.VISIBLE);
					attending_open.setVisibility(View.VISIBLE);
					attending_close.setVisibility(View.GONE);
				}
			});
		}
	}

	private void setRatingInteraction(View header2) {
		final ImageView rating_open = (ImageView) header.findViewById(R.id.rating_is_open);
		final ImageView rating_close = (ImageView) header.findViewById(R.id.rating_is_close);
		final LinearLayout rating_layout = (LinearLayout) header.findViewById(R.id.rating_layout);
		rating_open.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rating_is_open = false;
				rating_layout.setVisibility(View.GONE);
				rating_open.setVisibility(View.GONE);
				rating_close.setVisibility(View.VISIBLE);
			}
		});
		rating_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rating_is_open = true;
				rating_layout.setVisibility(View.VISIBLE);
				rating_open.setVisibility(View.VISIBLE);
				rating_close.setVisibility(View.GONE);
			}
		});
	}

	private class AttendProcessor extends AbstractAsyncTaskProcessor<Boolean, ExplorerObject> {
		private CompoundButton buttonView;
		private Boolean attend;

		public AttendProcessor(Activity activity, CompoundButton buttonView) {
			super(activity);
			this.buttonView = buttonView;
		}

		@Override
		public ExplorerObject performAction(Boolean... params) throws SecurityException, Exception {
			attend = params[0];
			if (attend) {
				return DTHelper.attend(getEvent());
			}
			return DTHelper.notAttend(getEvent());
		}

		private void updateAttending() {
			TextView tv;
			if (getActivity() != null) {
				// attendees
				tv = (TextView) getActivity().findViewById(R.id.attending_number);
				if (getEvent().getCommunityData().getAttendees() != null) {
					tv.setText(getEvent().getCommunityData().getAttendees() + " ");
				} else {
					tv.setText("0 ");
				}
			}
		}

		@Override
		public void handleResult(ExplorerObject result) {
			mEvent = result;
			updateAttending();
			// getSherlockActivity().invalidateOptionsMenu();
			// LocalEventObject event = getEvent();
			if (getActivity() != null) {
				if (mEvent.getCommunityData().getAttending() == null || mEvent.getCommunityData().getAttending().isEmpty()) {
					Toast.makeText(getActivity(), R.string.not_attend_success, Toast.LENGTH_SHORT).show();
					buttonView.setBackgroundResource(R.drawable.ic_monitor_off);
				} else {
					Toast.makeText(getActivity(), R.string.attend_success, Toast.LENGTH_SHORT).show();
					buttonView.setBackgroundResource(R.drawable.ic_monitor_on);
				}
			}
		}

	}

	@Override
	public void refresh() {
		// listAdapter.notifyDataSetChanged();
		updateCommentsList();
	}

}
