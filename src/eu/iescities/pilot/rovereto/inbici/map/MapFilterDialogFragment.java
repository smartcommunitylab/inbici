package eu.iescities.pilot.rovereto.inbici.map;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.inbici.custom.MapFilterAdapter;
import eu.iescities.pilot.rovereto.inbici.R;

public class MapFilterDialogFragment extends DialogFragment implements OnItemClickListener {

	public static enum REQUEST_TYPE {
		POI, EVENT, NONE
	}

	private static final String TAG_LABEL = "labels";
	private static final String TAG_ICONS = "icons";
	private static final String TAG_CATEGORIES = "categories";
	private static final String TAG_PREVIOUS_CATEGORIES = "previous categories";
	private static final String TAG_REQUEST = "request";

	private static final int TARGET_FRAG_REQUEST_CODE = 0;

	private Button mCancel;
	private Button mShow;
	private MapItemsHandler mCallback;
	private ListView mListView;
	private static String[] previousCategories;
	private String[] mCategories;
	private boolean[] mItemStatus;
	private REQUEST_TYPE mReqType;

	public static MapFilterDialogFragment istantiate(MapFragment mapFrag, int labelResId, int iconResId,
			REQUEST_TYPE type, String[] eventsCategories, String... categories) {
		Bundle args = new Bundle();
		args.putInt(TAG_LABEL, labelResId);
		args.putInt(TAG_ICONS, iconResId);
		args.putStringArray(TAG_PREVIOUS_CATEGORIES, eventsCategories);
		args.putStringArray(TAG_CATEGORIES, categories);
		args.putSerializable(TAG_REQUEST, type);
		MapFilterDialogFragment psf = new MapFilterDialogFragment();
		if (mapFrag instanceof MapItemsHandler)
			psf.setTargetFragment(mapFrag, TARGET_FRAG_REQUEST_CODE);
		else
			throw new IllegalArgumentException("The passed MapFragment should implement MapItemsHandler");
		psf.setArguments(args);
		return psf;
	}

	public static MapFilterDialogFragment istantiate(MapFragment mapFrag, int labelResId, int iconResId,
			String[] eventsCategories, String... categories) {
		Bundle args = new Bundle();
		args.putInt(TAG_LABEL, labelResId);
		args.putInt(TAG_ICONS, iconResId);
		args.putStringArray(TAG_PREVIOUS_CATEGORIES, eventsCategories);
		args.putStringArray(TAG_CATEGORIES, categories);
		args.putSerializable(TAG_REQUEST, REQUEST_TYPE.NONE);
		MapFilterDialogFragment psf = new MapFilterDialogFragment();
		if (mapFrag instanceof MapItemsHandler)
			psf.setTargetFragment(mapFrag, TARGET_FRAG_REQUEST_CODE);
		else
			throw new IllegalArgumentException("The passed MapFragment should implement MapItemsHandler");
		psf.setArguments(args);
		return psf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCallback = (MapItemsHandler) getTargetFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO externalize string
		getDialog().setTitle(getString(R.string.filter_dialog_title));
		// getDialog().set
		View v = inflater.inflate(R.layout.map_filter_dialog, container, false);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();

		Bundle b = getArguments();
		int labels = b.getInt(TAG_LABEL);
		previousCategories = b.getStringArray(TAG_PREVIOUS_CATEGORIES);
		mCategories = b.getStringArray(TAG_CATEGORIES);
		mReqType = (REQUEST_TYPE) b.getSerializable(TAG_REQUEST);
		List<String> elements = Arrays.asList(getResources().getStringArray(labels));
		// mItemStatus = new boolean[elements.size()-1];

		mItemStatus = new boolean[elements.size()];
		ArrayList<String> arrayListCategories = new ArrayList<String>(Arrays.asList(mCategories));
		if (previousCategories != null) {
			for (String prevCat : previousCategories) {
				mItemStatus[arrayListCategories.indexOf(prevCat)] = true;
			}
		}
		setupView(b, elements);
	}

	private void setupView(Bundle b, List<String> elements) {
		int icons = b.getInt(TAG_ICONS);
		mListView = (ListView) getView().findViewById(R.id.select_poi_listview);
		mListView.setOnItemClickListener(this);

		// TextView tv = (TextView)
		// getView().findViewById(R.id.select_poi_header);
		// tv.setText(elements.get(0));

		// mListView.setAdapter(new MapFilterAdapter(getActivity(),
		// elements.subList(1, elements.size()), icons));

		mListView.setAdapter(new MapFilterAdapter(getActivity(), elements, icons, mItemStatus));

		mCancel = (Button) getView().findViewById(R.id.select_poi_cancel);
		mCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});
		mShow = (Button) getView().findViewById(R.id.select_poi_confirm);
		mShow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				List<String> toLoad = new ArrayList<String>();
				for (int i = 0; i < mCategories.length; i++) {
					if (mItemStatus[i])
						toLoad.add(mCategories[i]);
				}
//				if (mReqType == REQUEST_TYPE.EVENT)
//					mCallback.setEventCategoriesToLoad(toLoad.toArray(new String[toLoad.size()]));
//				else
//					mCallback.setMiscellaneousCategoriesToLoad(toLoad.toArray(new String[toLoad.size()]));
//				getDialog().dismiss();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		CheckedTextView ctv = (CheckedTextView) arg1.findViewById(R.id.select_poi_checkTv);
		ctv.setChecked(!ctv.isChecked());
		ctv.setTag(ctv.isChecked());
		mItemStatus[arg2] = ctv.isChecked();
	}
}
