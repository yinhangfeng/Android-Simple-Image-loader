package com.damingdan.lib.imageloader;

public final class DisplayImageOptions {

	private final int imageResOnLoading;
	private final int imageResOnFail;
	private final boolean cacheInMemory;

	private DisplayImageOptions(Builder builder) {
		imageResOnLoading = builder.imageResOnLoading;
		imageResOnFail = builder.imageResOnFail;
		cacheInMemory = builder.cacheInMemory;
	}

	public boolean shouldShowImageOnLoading() {
		return imageResOnLoading != 0;
	}

	public boolean shouldShowImageOnFail() {
		return imageResOnFail != 0;
	}

	public int getImageResOnLoading() {
		return imageResOnLoading;
	}

	public int getImageResOnFail() {
		return imageResOnFail;
	}

	public boolean isCacheInMemory() {
		return cacheInMemory;
	}

	public static class Builder {
		private int imageResOnLoading = 0;
		private int imageResOnFail = 0;
		private boolean cacheInMemory = true;

		public Builder() {
		}

		/**
		 * 设置加载时显示的图片资源
		 */
		public Builder showImageOnLoading(int imageRes) {
			imageResOnLoading = imageRes;
			return this;
		}

		/**
		 * 设置加载失败时显示的图片资源
		 */
		public Builder showImageOnFail(int imageRes) {
			imageResOnFail = imageRes;
			return this;
		}

		/** 
		 * 是否缓存到内存
		 */
		public Builder cacheInMemory(boolean cacheInMemory) {
			this.cacheInMemory = cacheInMemory;
			return this;
		}

		public DisplayImageOptions build() {
			return new DisplayImageOptions(this);
		}
	}
}
