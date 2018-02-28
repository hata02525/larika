//this class is used for store pref into local mobile db and amazon server connection.
package com.example.fluper.larika_user_app.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.fluper.larika_user_app.constant.Constants;


public class SharedPreference {
	private static SharedPreference pref;
	private SharedPreferences sharedPreference;
	private Context ctx;
	private Editor editor;
	
	private SharedPreference(Context ctx)
	{
		this.ctx=ctx;
		sharedPreference=ctx.getSharedPreferences(Constants.PrefrenceName, Context.MODE_PRIVATE);
		editor=sharedPreference.edit();
	}
	
	public static SharedPreference getInstance(Context ctx)
	{
		if(pref==null)
		{
			pref=new SharedPreference(ctx);
		}
		return pref;
	}
	
	public void putString(String key,String value)
	{
		editor.putString(key, value);
		editor.commit();
	}
	
	public void putBoolean(String key,boolean value)
	{
		editor.putBoolean(key, value);
		editor.commit();
	}
	public void putInteger(String key,int value)
	{
		editor.putInt(key, value);
		editor.commit();
	}
	
	public void putFloat(String key,Float value)
	{
		editor.putFloat(key, value);
		editor.commit();
	}
	
	public String getString(String key,String defValue)
	{
		return sharedPreference.getString(key, defValue);
	}
	
	public boolean getBoolean(String key,boolean defValue)
	{
		return sharedPreference.getBoolean(key, defValue);
	}
	public int getInteger(String key,int defValue)
	{
		return sharedPreference.getInt(key, defValue);
	}
	public Float getFloat(String key,Float defValue)
	{
		return sharedPreference.getFloat(key, defValue);
	}
	
	public void deletePreference()
	{
		editor.clear();
		editor.commit();
	}

	public void deletePreference(String key)
	{
		editor.remove(key);
		editor.commit();
	}

	/*public static void setAmazon(final Bitmap bitmap, final String keyName) {
		new Thread() {
			@Override
			public void run() {
				try {
					byte[] content;
					String amazonFileUploadLocationOriginal = Constants.AMAZON_S3_BUCKET_NAME;
					Log.d("url", Constants.AMAZON_S3_END_POINT + Constants.AMAZON_S3_BUCKET_NAME + keyName);
					AmazonS3Client amazonClient = new AmazonS3Client(new BasicAWSCredentials(Constants.AMAZON_S3_ACCESS_KEY, Constants.AMAZON_S3_SECRET_KEY));
					amazonClient.setEndpoint(Constants.AMAZON_S3_END_POINT);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					content = stream.toByteArray();
					ByteArrayInputStream bs = new ByteArrayInputStream(content);
					ObjectMetadata objectMetadata = new ObjectMetadata();
					Long contentLength = Long.valueOf(content.length);
					objectMetadata.setContentLength(contentLength);
					objectMetadata.setContentType("image/jpeg");
					PutObjectRequest putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal, keyName, bs, objectMetadata);
					PutObjectResult result = amazonClient.putObject(putObjectRequest);
					Log.e("Etag:", result.getETag() + "-->" + result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	*/

}
