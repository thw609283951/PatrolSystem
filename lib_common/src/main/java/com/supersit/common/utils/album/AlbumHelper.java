package com.supersit.common.utils.album;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;


import com.supersit.common.R;
import com.supersit.common.entity.AlbumDirInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AlbumHelper {
	final String TAG = getClass().getSimpleName();
	Context context;
	ContentResolver cr;
	HashMap<String, AlbumDirInfo> bucketList = new HashMap<>();

	private static AlbumHelper instance;

	private AlbumHelper() {
	}

	public static AlbumHelper getHelper() {
		if (instance == null) {
			instance = new AlbumHelper();
		}
		return instance;
	}

	/**
	 * 初始化
	 *
	 * @param context
	 */
	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
			cr = context.getContentResolver();
		}
	}
	/**
	 * 得到图片集
	 */
	private void buildImagesBucketList() {
		bucketList.clear();

		// 构造相册索引
		String columns[] = new String[] {Media._ID,  Media.BUCKET_ID,
				Media.DATA , Media.TITLE, Media.DATE_MODIFIED,
				Media.BUCKET_DISPLAY_NAME };
		// 得到一个游标
		Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,
				Media.DATE_MODIFIED);
		if (cur.moveToFirst()) {
			// 获取指定列的索引
			int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
			int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
			int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
			int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
			int bucketDateModified = cur.getColumnIndexOrThrow(Media.DATE_MODIFIED);

			// 获取图片总数
			int totalNum = cur.getCount();

			AlbumDirInfo allAlbumInfo= new AlbumDirInfo();
			allAlbumInfo.setDirName(context.getResources().getString(R.string.all_picture).toString());
			allAlbumInfo.setCount(totalNum);

			List<String> allImageInfos=new ArrayList<>();

			do {
				String _id = cur.getString(photoIDIndex);
				String path = cur.getString(photoPathIndex);
				String title = cur.getString(photoTitleIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				String bucketId = cur.getString(bucketIdIndex);
				String dateModified = cur.getString(bucketDateModified);

				if(!new File(path).exists()){
					continue;
				}

				allImageInfos.add(path);

				AlbumDirInfo albumDirInfo = bucketList.get(bucketId);
				List<String> albumImageInfos=null;
				if (albumDirInfo == null) {
					albumDirInfo = new AlbumDirInfo();
					albumImageInfos=new ArrayList<>();
					albumDirInfo.setDirName(bucketName);
				}else{
					albumImageInfos=albumDirInfo.getImageList();
				}
				albumImageInfos.add(path);
				albumDirInfo.setCount(albumImageInfos.size());
				albumDirInfo.setImageList(albumImageInfos);
				bucketList.put(bucketId, albumDirInfo);

			} while (cur.moveToNext());
			allAlbumInfo.setImageList(allImageInfos);
			bucketList.put("AllImage", allAlbumInfo);
		}
		cur.close();
	}


	/**
	 * 得到图片集
	 *
	 * @param refresh
	 * @return
	 */
	public List<AlbumDirInfo> getImagesBucketList(boolean refresh) {
		if(bucketList.size()==0 || refresh){
			buildImagesBucketList();
		}

		List<AlbumDirInfo> tmpList = new ArrayList<>();
		Iterator<Map.Entry<String, AlbumDirInfo>> itr = bucketList.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, AlbumDirInfo> entry = (Map.Entry<String, AlbumDirInfo>) itr.next();
			tmpList.add(entry.getValue());
		}
		return tmpList;
	}

}