package mahmud.picosoft.islamiclecturecollection.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import mahmud.picosoft.islamiclecturecollection.pojo.Actors;
import mahmud.picosoft.islamiclecturecollection.R;

public class ActorAdapter extends ArrayAdapter<Actors> {
	ArrayList<Actors> actorList;
	LayoutInflater vi;
	int Resource;
	ViewHolder holder;

	public ActorAdapter(Context context, int resource, ArrayList<Actors> objects) {
		super(context, resource, objects);
		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Resource = resource;
		actorList = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// convert view = design
		View v = convertView;
		if (v == null) {
			holder = new ViewHolder();
			v = vi.inflate(Resource, null);

			holder.id = (TextView) v.findViewById(R.id.lectureId);
			holder.tvName = (TextView) v.findViewById(R.id.tvName);
			//holder.tvLink = (TextView) v.findViewById(R.id.tvLink);
			holder.contributor = (TextView) v.findViewById(R.id.tvContributor);
			holder.time = (TextView) v.findViewById(R.id.tvTime);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		String lectureZero = "Lecture#";
		int id = Integer.parseInt(actorList.get(position).getId());
		if (id < 10){
			lectureZero = "Lecture#0";
		}

		holder.id.setText(lectureZero + actorList.get(position).getId());
		holder.tvName.setText(actorList.get(position).getName());
		holder.contributor.setText(actorList.get(position).getContributor());
		holder.time.setText(actorList.get(position).getTime());

		return v;

	}

	static class ViewHolder {
		public TextView id;
		public TextView tvName;
		public TextView tvLink;
		public TextView contributor;
		public TextView time;

	}

	// private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	// ImageView bmImage;
	//
	// public DownloadImageTask(ImageView bmImage) {
	// this.bmImage = bmImage;
	// }
	//
	// protected Bitmap doInBackground(String... urls) {
	// String urldisplay = urls[0];
	// Bitmap mIcon11 = null;
	// try {
	// InputStream in = new java.net.URL(urldisplay).openStream();
	// mIcon11 = BitmapFactory.decodeStream(in);
	// } catch (Exception e) {
	// Log.e("Error", e.getMessage());
	// e.printStackTrace();
	// }
	// return mIcon11;
	// }
	//
	// protected void onPostExecute(Bitmap result) {
	// bmImage.setImageBitmap(result);
	// }
	//
	// }

}
