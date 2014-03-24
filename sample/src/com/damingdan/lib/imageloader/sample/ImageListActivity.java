package com.damingdan.lib.imageloader.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ImageListActivity extends BaseActivity {

	private ListView listView;
	private LayoutInflater inflater;
	private String[] imageUrls;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_list);
		inflater = getLayoutInflater();
		listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(new ItemAdapter());
	}

	class ItemAdapter extends BaseAdapter {

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

			tag.text.setText("Item " + (position + 1));

			return convertView;
		}
	}
}