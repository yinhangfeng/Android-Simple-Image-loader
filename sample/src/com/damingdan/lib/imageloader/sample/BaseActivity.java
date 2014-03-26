package com.damingdan.lib.imageloader.sample;

import com.damingdan.lib.imageloader.ImageLoader;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

public abstract class BaseActivity extends Activity {

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected String[] imageUrls = Constants.IMAGES;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_log_status:
			imageLoader.logStatus();
			return true;
		default:
			return false;
		}
	}
	
}
