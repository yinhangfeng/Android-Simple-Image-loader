package com.damingdan.lib.imageloader.sample;

import com.damingdan.lib.imageloader.DisplayImageOptions;
import com.damingdan.lib.imageloader.ImageLoadingListener;
import com.damingdan.lib.imageloader.ImageLoadingProgressListener;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImagePagerActivity extends BaseActivity {

	private ViewPager viewPager;
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
	.cacheInMemory(true).showImageOnLoading(R.drawable.ic_launcher)
	.showImageOnFail(R.drawable.xxx).build();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_pager);

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new ImagePagerAdapter());
	}

	private class ImagePagerAdapter extends PagerAdapter {
		
		private LayoutInflater inflater = getLayoutInflater();

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.progress);
			view.addView(imageLayout, 0);
			imageLoader.displayImage(imageUrls[position], imageView, options, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String url, View view) {
					progressBar.setVisibility(View.VISIBLE);
					progressBar.setProgress(0);
					
				}

				@Override
				public void onLoadingFailed(String url, View view, Exception ex) {
					progressBar.setProgress(0);
					progressBar.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String url, View view, Bitmap bm) {
					progressBar.setProgress(100);
					progressBar.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingCancelled(String url, View view) {
				}
				
			}, new ImageLoadingProgressListener() {

				@Override
				public void onProgressUpdate(String url, View view,
						int current, int total) {
					progressBar.setProgress((int) (((float) current / total) * 100));
				}
				
			});
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}
}