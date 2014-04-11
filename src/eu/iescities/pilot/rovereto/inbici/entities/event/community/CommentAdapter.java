package eu.iescities.pilot.rovereto.inbici.entities.event.community;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.inbici.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.inbici.custom.ReviewHelper;
import eu.iescities.pilot.rovereto.inbici.custom.ReviewHelper.ReviewHandler;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.CommunityData;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.Review;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class CommentAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private Activity activity;
	private ExplorerObject mEvent;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<Review>> _listDataChild;
	private RefreshComments refreshcomment;

	public CommentAdapter(Context context, List<String> listDataHeader, HashMap<String, List<Review>> listChildData,
			Activity activity, ExplorerObject event, RefreshComments refreshComments) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this.activity = activity;
		this.mEvent = event;
		this.refreshcomment = refreshComments;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {

		final Review review = (Review) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.event_comment_item, parent, false);
			// convertView =
			// inflater.inflate(R.layout.forum_list_child_item_row,parent,
			// false);

		}

		TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);

		txtListChild.setText(review.getComment());
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public void getGroupClear(int groupPosition) {

	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.event_comments_group, null);
		}

		TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);
		// setlistener
		setCommentAddInteraction(convertView);

		return convertView;
	}

	private void setCommentAddInteraction(View convertView) {
		// set listener on the button
		ImageView image = (ImageView) convertView.findViewById(R.id.event_comment_action);

		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ReviewHelper.reviewDialog(_context, 0, new ReviewProcessor(activity),
						R.string.comment_event_dialog_title);

			}
		});

	}

	private class ReviewProcessor extends AbstractAsyncTaskProcessor<Review, CommunityData> implements ReviewHandler {

		public ReviewProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public CommunityData performAction(Review... params) throws SecurityException, Exception {
			return DTHelper.writeReview(mEvent, params[0]);
		}

		@Override
		public void handleResult(CommunityData result) {
			mEvent.setCommunityData(result);
			if (_context != null)
				Toast.makeText(_context, R.string.comment_success, Toast.LENGTH_SHORT).show();
			// return community data instead a review
//			notifyDataSetChanged();
			if (refreshcomment != null)
				refreshcomment.refresh();

		}

		@Override
		public void onReviewChanged(Review review) {
			new SCAsyncTask<Review, Void, CommunityData>(activity, this).execute(review);
			// update list of comment
		}
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
