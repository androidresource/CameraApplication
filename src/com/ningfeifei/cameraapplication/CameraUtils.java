package com.ningfeifei.cameraapplication;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class CameraUtils {

	public static Size getProperSize(List<Size> sizeList, float displayRatio) {
		// 先对传进来的size列表进行排序
		Collections.sort(sizeList, new SizeComparator());

		Size result = null;
		for (Size size : sizeList) {
			float curRatio = ((float) size.width) / size.height;
			if (curRatio - displayRatio == 0) {
				result = size;
			}
		}
		if (null == result) {
			for (Size size : sizeList) {
				float curRatio = ((float) size.width) / size.height;
				if (curRatio == 3f / 4) {
					result = size;
				}
			}
		}
		return result;
	}

	static class SizeComparator implements Comparator<Size> {

		@Override
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			Size size1 = lhs;
			Size size2 = rhs;
			if (size1.width < size2.width || size1.width == size2.width
					&& size1.height < size2.height) {
				return -1;
			} else if (!(size1.width == size2.width && size1.height == size2.height)) {
				return 1;
			}
			return 0;
		}

	}

	/**
	 * <p>
	 * <b>Description:</b>兼容方法获取图片路径
	 * </p>
	 * <p>
	 * <b>Title:</b> uri2filePath
	 * </p>
	 * 
	 * @param uri
	 * @param activity
	 * @return 
	 *         1.content://com.android.providers.media.documents/document/image:3951
	 *         2.content://media/external/images/media/3951
	 *         3.file:///storage/emulated/0/test/123.jpg
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static String uri2filePath(Uri uri, Activity activity) {
		String path = "";
		if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		// 19
		if (Integer.parseInt(Build.VERSION.SDK) >= Build.VERSION_CODES.KITKAT
				&& DocumentsContract.isDocumentUri(activity, uri)) {
			String wholeID = DocumentsContract.getDocumentId(uri);
			String id = wholeID.split(":")[1];
			String[] column = { MediaStore.Images.Media.DATA };
			String sel = MediaStore.Images.Media._ID + "=?";
			Cursor cursor = activity.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
					new String[] { id }, null);
			int columnIndex = cursor.getColumnIndex(column[0]);

			if (cursor.moveToFirst()) {
				path = cursor.getString(columnIndex);
			}
			cursor.close();
		} else {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = activity.getContentResolver().query(uri,
					projection, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			path = cursor.getString(column_index);
		}
		return path;
	}

	// 1.content://com.android.providers.media.documents/document/image:3951
	// 2.content://media/external/images/media/3951
	// 3.file:///storage/emulated/0/test/123.jpg
	@SuppressWarnings("deprecation")
	public static Uri filePath2Uri(Activity context, String dir, String name) {
		if (dir != null && name != null) {
			/*
			 * if(Integer.parseInt(Build.VERSION.SDK)>=Build.VERSION_CODES.KITKAT
			 * ){ Uri uri = Uri.fromFile(new File(dir+name)); String
			 * path1=uri2filePath(uri,TestCameraActivity.this); if(uri!=null){
			 * return uri; } Cursor cur = context.getContentResolver().query(
			 * MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
			 * null); for (cur.moveToFirst(); !cur.isAfterLast(); cur
			 * .moveToNext()) { int index =
			 * cur.getColumnIndex(MediaStore.Images.ImageColumns._ID); // set
			 * _id value index = cur.getInt(index); if(index!=0){ Uri uri_temp =
			 * Uri .parse(
			 * "content://com.android.providers.media.documents/document/image:"
			 * + index); String
			 * path=uri2filePath(uri_temp,TestCameraActivity.this);
			 * if(path.equals(dir+name)){
			 * Toast.makeText(TestCameraActivity.this, uri_temp.toString(),
			 * Toast.LENGTH_SHORT).show(); return uri_temp; } } } }else
			 */{
				String path = Uri.decode(dir + name);
				ContentResolver cr = context.getContentResolver();
				StringBuffer buff = new StringBuffer();
				buff.append("(").append(MediaStore.Images.ImageColumns.DATA)
						.append("=").append("'" + path + "'").append(")");
				Cursor cur = cr.query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Images.ImageColumns._ID },
						buff.toString(), null, null);
				int index = 0;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					index = cur
							.getColumnIndex(MediaStore.Images.ImageColumns._ID);
					// set _id value
					index = cur.getInt(index);
				}
				if (index == 0) {
					// do nothing
				} else {
					Uri uri_temp = Uri
							.parse("content://media/external/images/media/"
									+ index);
					if (uri_temp != null) {
						return uri_temp;
					}
				}
			}

		}
		return null;
	}
}
