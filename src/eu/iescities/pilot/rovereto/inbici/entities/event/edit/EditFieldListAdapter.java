package eu.iescities.pilot.rovereto.inbici.entities.event.edit;


import java.util.List;

import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

public class EditFieldListAdapter extends ArrayAdapter<String> {

	Context context;
	List<String> values;

	int edit_field_layout;
	String edit_field_type;
	String event_field_type = null;

	ViewHolder holder = null;

	public EditFieldListAdapter(Context context, int resourceId,
			List<String> items, String edit_field_type) {
		super(context, resourceId, items);
		this.context = context;
		this.values = items;
		this.edit_field_layout = resourceId;
		this.edit_field_type = edit_field_type;

	}


	public EditFieldListAdapter(Context context, int resourceId,
			List<String> items, String edit_field_type, String event_field_type) {
		super(context, resourceId, items);
		this.context = context;
		this.values = items;
		this.edit_field_layout = resourceId;
		this.edit_field_type = edit_field_type;
		this.event_field_type = event_field_type;

	}

	/*private view holder class*/
	private static class ViewHolder {
		protected ImageView delete;
		protected EditText txtName;

	}


	public View getView(int position, View convertView, ViewGroup parent) {


		View view = null;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);  
			view = mInflater.inflate(this.edit_field_layout, null);
			final ViewHolder viewHolder = new ViewHolder();

			viewHolder.txtName = (EditText) view.findViewById(R.id.field_value);

			if(this.edit_field_type.matches(Utils.EDIT_FIELD_PHONE_TYPE)){
				viewHolder.txtName.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
				viewHolder.txtName.setInputType(InputType.TYPE_CLASS_PHONE);
			}else if(this.edit_field_type.matches(Utils.EDIT_FIELD_EMAIL_TYPE)){
				viewHolder.txtName.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			}
			else{
				viewHolder.txtName.setInputType(InputType.TYPE_CLASS_TEXT);
				//viewHolder.txtName.setInputType(InputType.TYPE_CLASS_PHONE);
			}


			viewHolder.delete = (ImageView) view.findViewById(R.id.delete_icon);

			viewHolder.txtName
			.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub
				}


				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					String  element = (String) viewHolder.txtName.getTag();
					//					Log.i("TAG", "TagListAdapter --> TextWatcher --> old edited string: " + element);
					int id = values.indexOf(element);
					element = s.toString();
					if (id!=-1){
						values.set(id, element);
						viewHolder.txtName.setTag(values.get(id));
					}
				}

			});

			view.setTag(viewHolder);
			viewHolder.txtName.setTag(values.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).txtName.setTag(values.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.txtName.setText(values.get(position));
		holder.delete.setOnClickListener(new DeleteIconClickListener(position));

		return view;

	}


	private final class DeleteIconClickListener implements OnClickListener {
		private final int position;


		private DeleteIconClickListener(int position) {
			this.position = position;
		}


		@Override
		public void onClick(View v) {
			Log.i("FRAGMENT LC", "EditFieldListAdapter --> Delete button pressed!");

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			//set msg to show
			builder.setMessage(getFieldTypeRemoveRequest(values.get(position)));
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					values.remove(position);
					notifyDataSetChanged();
				}
			});
			builder.setNegativeButton(R.string.no,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
		}
	}

	private String getFieldTypeRemoveRequest(String field_value){

		String remove_request = null;

		if(this.edit_field_type.matches(Utils.EDIT_FIELD_PHONE_TYPE)){
			remove_request = context.getResources().getString(R.string.phone_remove_request, field_value);
		}else if(this.edit_field_type.matches(Utils.EDIT_FIELD_EMAIL_TYPE)){
			remove_request = context.getResources().getString(R.string.email_remove_request, field_value);

		}

		if (this.event_field_type!= null){
			if(this.event_field_type.equals("Tags")){
				remove_request = context.getResources().getString(R.string.tag_remove_request, field_value);
			}
			else 
				remove_request = context.getResources().getString(R.string.info_remove_request, field_value);
		}



		return remove_request;

	}



}