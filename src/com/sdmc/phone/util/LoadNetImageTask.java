package com.sdmc.phone.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.sdmc.dlna.filebrowser.FileItem;
/**
 * 异步加载 网络图片
 * @author fee
 *
 */
 public class LoadNetImageTask extends AsyncTask<ArrayList<FileItem>, BitmapDrawable, ArrayList<BitmapDrawable>>{

	@Override
	protected void onPreExecute() {
		Log.i("info"," the load image from net is prepare to begin...");
	}
	@Override
	protected ArrayList<BitmapDrawable> doInBackground(ArrayList<FileItem>... params) {
		ArrayList<BitmapDrawable> loadResult = new ArrayList<BitmapDrawable>();
		ArrayList<FileItem> imagesFileItem = params[0]; 
		//当前播放图片
		FileItem curPicFileItem = imagesFileItem.get(0);
		String urlPath = curPicFileItem.getAbsPath();
		BitmapDrawable curShow = 
		getNetPic(urlPath);
		loadResult.add(curShow);
		publishProgress(curShow);
		imagesFileItem.remove(0);
		int restListSize = imagesFileItem.size();
		for(int i = 0; i <10; i++	){
			FileItem image = imagesFileItem.get(i);
			String path = image.getAbsPath();
			BitmapDrawable drawable = getNetPic(path);
			publishProgress(drawable);
			loadResult.add(drawable);
		}
		return loadResult;
	}
	 
	@Override
	protected void onProgressUpdate(BitmapDrawable... values) {
		 
		
	}
	@Override
	protected void onPostExecute(ArrayList<BitmapDrawable> result) {
	}
	@SuppressWarnings("deprecation")
	private BitmapDrawable getNetPic(String picUrlPath){
		BitmapDrawable icon = null;
		URL imgUrl =null;
		try {
			imgUrl = new URL(picUrlPath);
			HttpURLConnection httpConnect = (HttpURLConnection) imgUrl.openConnection();
			icon = new BitmapDrawable(httpConnect.getInputStream());
			httpConnect.disconnect();
		} catch (MalformedURLException e) {
			Log.e("info"," the load image task when get pic : occur MalformedURLException");
			return null;
		} catch (IOException e) {
			Log.e("info"," the load image task when get pic : occur IOException");
			return null;
		}
		return icon;		
	}
} 

	/*private class LoadNetImageTask extends AsyncTask< FileItem, BitmapDrawable,  BitmapDrawable >{

		@Override
		protected void onPreExecute() {
			Log.i("info"," the load image from net is prepare to begin...");
		}
		@Override
		protected BitmapDrawable doInBackground(FileItem... params) {
			 
			 FileItem  imagesFileItem = params[0]; 
			//当前播放图片
			 
			String urlPath = imagesFileItem.getAbsPath();
			BitmapDrawable curShow = 
			getNetPic(urlPath);
			 
			publishProgress(curShow);
			 
			return curShow;
		}
		 *//** 
      * 在publishProgress()被调用以后执行，publishProgress()用于更新进度 
      *//*  
		@Override
		protected void onProgressUpdate(BitmapDrawable... values) {
			imageAdapter.updataOneByOne(values[0]);
			mHandler.sendEmptyMessage(MSG_HAS_PIC_UPDATE);
		}
		@Override
		protected void onPostExecute( BitmapDrawable  result) {
			
		}
		@SuppressWarnings("deprecation")
		private BitmapDrawable getNetPic(String picUrlPath){
			BitmapDrawable icon = null;
			URL imgUrl =null;
			try {
				imgUrl = new URL(picUrlPath);
				HttpURLConnection httpConnect = (HttpURLConnection) imgUrl.openConnection();
				icon = new BitmapDrawable(httpConnect.getInputStream());
				httpConnect.disconnect();
			} catch (MalformedURLException e) {
				Log.e("info"," the load image task when get pic : occur MalformedURLException");
				return null;
			} catch (IOException e) {
				Log.e("info"," the load image task when get pic : occur IOException");
				return null;
			}
			return icon;		
		}
	}
	
}*/
