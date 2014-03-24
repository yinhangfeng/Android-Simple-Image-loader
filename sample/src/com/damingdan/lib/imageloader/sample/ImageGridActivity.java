package com.damingdan.lib.imageloader.sample;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageGridActivity extends BaseActivity {
	
	private GridView gridView;

	private String[] imageUrls;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_grid);

		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter());
	}

	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Tag tag;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
				tag = new Tag();
				tag.imageView = (ImageView) convertView.findViewById(R.id.image);
				tag.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
				convertView.setTag(tag);
			} else {
				tag = (Tag) convertView.getTag();
			}
			return convertView;
		}

		private class Tag {
			ImageView imageView;
			ProgressBar progressBar;
		}
	}
}