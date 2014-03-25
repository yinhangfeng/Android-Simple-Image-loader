package com.damingdan.lib.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLES10;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

public class ImageDecoder {
	private static final String TAG = "ImageDecoder";
	private static final boolean DEBUG = true;
	
	private static final int DEFAULT_MAX_BITMAP_DIMENSION = 2048;

	private static int maxBitmapDimension;

	static {
		int[] maxTextureSize = new int[1];
		GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
		maxBitmapDimension = Math.max(maxTextureSize[0], DEFAULT_MAX_BITMAP_DIMENSION);
	}

	public Bitmap decode(File imageFile) throws IOException {
		InputStream is = new FileInputStream(imageFile);
		Options decodingOptions = prepareDecodingOptions(is);
		if(DEBUG) Log.i(TAG, "decode inSampleSize=" + decodingOptions.inSampleSize); 
		return BitmapFactory.decodeStream(is, null, decodingOptions);
	}
	
	private Options prepareDecodingOptions(InputStream is) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, options);
		options.inSampleSize = computeImageSampleSize(options);
		options.inJustDecodeBounds = false;
		return options;
	}
	
	private int computeImageSampleSize(Options options) {
		int widthScale = (int) Math.ceil((float) options.outWidth / maxBitmapDimension);
		int heightScale = (int) Math.ceil((float) options.outHeight / maxBitmapDimension);
		return Math.max(widthScale, heightScale);
	}

}