package com.damingdan.lib.imageloader.sample;

import com.damingdan.lib.imageloader.DisplayImageOptions;
import com.damingdan.lib.imageloader.ImageLoadingListener;
import com.damingdan.lib.imageloader.ImageLoadingProgressListener;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageGridActivity extends BaseActivity {
	
	private GridView gridView;
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
	.cacheInMemory(true).showImageOnLoading(R.drawable.ic_launcher)
	.showImageOnFail(R.drawable.xxx).build();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_grid);

		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter());
	}

	public class ImageAdapter extends BaseAdapter {
		
		private LayoutInflater inflater = getLayoutInflater();
		
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
				convertView = inflater.inflate(R.layout.item_grid_image, parent, false);
				tag = new Tag();
				tag.imageView = (ImageView) convertView.findViewById(R.id.image);
				tag.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
				convertView.setTag(tag);
			} else {
				tag = (Tag) convertView.getTag();
			}
			imageLoader.displayImage(imageUrls[position], tag.imageView, options, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String url, View view) {
					tag.progressBar.setProgress(0);
					
				}

				@Override
				public void onLoadingFailed(String url, View view, Exception ex) {
					tag.progressBar.setProgress(0);
					
				}

				@Override
				public void onLoadingComplete(String url, View view, Bitmap bm) {
					tag.progressBar.setProgress(100);
					
				}

				@Override
				public void onLoadingCancelled(String url, View view) {
					// TODO Auto-generated method stub
					
				}
				
			}, new ImageLoadingProgressListener() {

				@Override
				public void onProgressUpdate(String url, View view,
						int current, int total) {
					tag.progressBar.setProgress((int) (((float) current / total) * 100));
				}
				
			});
			return convertView;
		}

		private class Tag {
			ImageView imageView;
			ProgressBar progressBar;
		}
	}
}