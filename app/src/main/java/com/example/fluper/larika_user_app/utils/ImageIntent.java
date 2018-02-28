package com.example.fluper.larika_user_app.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageIntent {

	public static final int PICK_CAMERA = 32;
	private String currentPath="";
	public ImageIntent() {
	}


	

	public static String getRealPathFromUri(Context activity, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
			cursor.moveToFirst();
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			return cursor.getString(column_index);
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	    return null;
	}


	
	/*public File createImageFile() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File mFileTemp = null;
		//ContextWrapper contextWrapper=new ContextWrapper(activity);
		//String root=contextWrapper.getFilesDir().getAbsolutePath();
		String root=activity.getDir("my_sub_dir",Context.MODE_PRIVATE).getAbsolutePath();
		File myDir = new File(root + "/Img");
		if(!myDir.exists()){
			myDir.mkdirs();
		}
		try {
			mFileTemp=File.createTempFile(imageFileName,".jpg",myDir.getAbsoluteFile());
			Log.e("path",mFileTemp.getAbsolutePath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return mFileTemp;
	}*/

	public static File createImageFile(Activity activity) {
		String state = Environment.getExternalStorageState();
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File mFileTemp = null;
		 if (Environment.MEDIA_MOUNTED.equals(state)) {
			try {			
		mFileTemp = File.createTempFile(imageFileName, ".jpg", Environment.getExternalStorageDirectory());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				mFileTemp = File.createTempFile(imageFileName, ".jpg", activity.getFilesDir());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mFileTemp;
	}


	public String saveImageToSdcard(Bitmap currentImage, Activity activity) {
		String path="";
		File file = createImageFile(activity);
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(file);
			currentImage.compress(Bitmap.CompressFormat.PNG, 70, fout);
			fout.flush();
			path=file.getPath();
		} catch (Exception e) {
			e.printStackTrace();
			path=null;
		}
		return path;
	}


	


}
