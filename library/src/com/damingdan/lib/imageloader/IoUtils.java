package com.damingdan.lib.imageloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class IoUtils {
	
	public static final int DEFAULT_BUFFER_SIZE = 32 * 1024;//32KB
	
	public interface CopyStreamListener {
		void onBytesCopied(int current, int total);
	}
	
	public static void copyStreamAndClose(InputStream is, OutputStream os, CopyStreamListener listener, int bufferSize) throws IOException {
		byte[] buf = new byte[bufferSize];
		int current = 0;
		int total = is.available();
		int count;
		try {
			while((count = is.read(buf)) != -1) {
				os.write(buf, 0, count);
				current += count;
				listener.onBytesCopied(current, total);
			}
		} finally {
			try {
				is.close();
			} catch(IOException ig) {}
			os.close();
		}
	}
	
	public static void copyStreamAndClose(InputStream is, OutputStream os, int bufferSize) throws IOException {
		byte[] buf = new byte[bufferSize];
		int count;
		try {
			while((count = is.read(buf)) != -1) {
				os.write(buf, 0, count);
			}
		} finally {
			try {
				is.close();
			} catch(IOException ig) {}
			os.close();
		}
	}
	
	public static void copyStreamToFile(InputStream is, File file, CopyStreamListener listener) throws IOException {
		OutputStream os = new FileOutputStream(file);
		if(listener == null) {
			copyStreamAndClose(is, os, DEFAULT_BUFFER_SIZE);
		} else {
			copyStreamAndClose(is, os, listener, DEFAULT_BUFFER_SIZE);
		}
	}
	
	public static void copyFile(File from, File to) throws IOException {
		if(from.equals(to)) {
			return;
		}
		RandomAccessFile inFile = null;
		RandomAccessFile outFile = null;
		try {
			inFile = new RandomAccessFile(from, "r");
			outFile = new RandomAccessFile(to, "rw");
			FileChannel inChannel = inFile.getChannel();
			FileChannel outChannel = outFile.getChannel();
			long pos = 0;
			long toCopy = inFile.length();
			while (toCopy > 0) {
				long bytes = inChannel
						.transferTo(pos, toCopy, outChannel);
				pos += bytes;
				toCopy -= bytes;
			}
		} finally {
			if(inFile != null) {
				try {
					inFile.close();
				} catch(IOException ig) {}
			}
			if(outFile != null) {
				outFile.close();
			}
		}
	}

}
