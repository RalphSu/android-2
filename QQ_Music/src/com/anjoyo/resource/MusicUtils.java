package com.anjoyo.resource;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.anjoyo.qq_music.R;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class MusicUtils {
	static Uri sArtworkUri = Uri
			.parse("content://media/external/audio/albumart");
	static BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
	static Bitmap mCacheBit;
	/**
	 * 从查询到的歌曲中获取专辑图片
	 * 
	 * @param context
	 *            ：上下文对象
	 * @param song_id
	 *            ：歌曲id
	 * @param album_id
	 *            ：专辑id
	 * @param allowdefault
	 *            :boolean 变量
	 * @return
	 */
	public static Bitmap getArtWork(Context context, long song_id,
			long album_id, boolean allowdefault) {

		if (album_id < 0) {
			if (song_id >= 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if (bm != null) {
					return bm;
				}

			}
			if (allowdefault) {
				return getDefaultArtWork(context);
			}
			return null;
		}
		ContentResolver cResolver = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
		if (uri != null) {
			InputStream in = null;
			try {
				in = cResolver.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, sBitmapOptions);
			} catch (Exception e) {
				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if (bm == null && allowdefault) {
							return getDefaultArtWork(context);
						}
					} else if (allowdefault) {
						bm = getDefaultArtWork(context);
					}
					return bm;
				}

			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}

			}
		}
		return null;
	}

	/**
	 * 设置默认图片
	 * 
	 * @param context
	 *            ：当前上下文对象
	 * @return
	 */
	private static Bitmap getDefaultArtWork(Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;

		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(R.drawable.default_play_activity_bg1), null,
				opts);
	}

	/**
	 * 设置从歌曲中得到的专辑图片
	 * 
	 * @param context
	 *            ：当前上下文对象
	 * @param song_id
	 *            ：歌曲id(musicID)
	 * @param album_id
	 *            :专辑id(musicAlbum_ID)
	 * @return
	 */
	private static Bitmap getArtworkFromFile(Context context, long song_id,
			long album_id) {
		Bitmap bm = null;
		if (album_id < 0 && song_id < 0) {
			throw new IllegalArgumentException("需要一个歌曲id或专辑id");
		}
		try {
			if (album_id < 0) {
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ song_id + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}

			} else {
				Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");

				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (bm != null) {
			mCacheBit = bm;
		}
		return bm;
	}
}
