package com.damingdan.lib.imageloader.sample;

import com.damingdan.lib.imageloader.DisplayImageOptions;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ImageListActivity extends BaseActivity {

	private String[] imageUrls = Constants.REPEAT_IMAGES;
	private ListView listView;
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true).showImageOnLoading(R.drawable.ic_launcher)
			.showImageOnFail(R.drawable.xxx).build();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_list);
		listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(new ItemAdapter());
	}

	class ItemAdapter extends BaseAdapter {
		
		private LayoutInflater inflater = getLayoutInflater();

		private class Tag {
			public TextView text;
			public ImageView image;
		}

		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Tag tag;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_list_image, parent, false);
				tag = new Tag();
				tag.text = (TextView) convertView.findViewById(R.id.text);
				tag.image = (ImageView) convertView.findViewById(R.id.image);
				convertView.setTag(tag);
			} else {
				tag = (Tag) convertView.getTag();
			}
			String url = imageUrls[position];
			tag.text.setText("" + (position + 1) + " url " + url);
			imageLoader.displayImage(url, tag.image, options, null, null);
			return convertView;
		}
	}
}