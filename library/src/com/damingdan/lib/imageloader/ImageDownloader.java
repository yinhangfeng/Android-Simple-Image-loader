package com.damingdan.lib.imageloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader {
	
	private static final int HTTP_CONNECT_TIMEOUT = 5 * 1000;
	private static final int HTTP_READ_TIMEOUT = 20 * 1000;
	
	public InputStream getStream(String imageUri) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(imageUri).openConnection();
		conn.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
		conn.setReadTimeout(HTTP_READ_TIMEOUT);
		InputStream is = conn.getInputStream();
		int contentLength = conn.getContentLength();
		if(contentLength < 0) {
			return is;
		}
		return new ContentLengthInputStream(is, contentLength);
	}

}
