package com.anjoyo.netmusic;

import java.util.ArrayList;

import com.anjoyo.qq_music.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MKAdapter extends BaseAdapter {

	Context myContent;

	ArrayList<MusicModel> mylistData;
	String imgpath;

	public MKAdapter(Context context, ArrayList<MusicModel> listData) {
		this.myContent = context;
		this.mylistData = listData;

	}

	@Override
	public int getCount() {
		return mylistData.size();

	}

	@Override
	public Object getItem(int position) {
		return mylistData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	int clickposition = -1;
	MusicFileDown md = new MusicFileDown();

	public View getView(final int position, View convertView, ViewGroup parent) {
		MyHolder myHolder = null;

		if (convertView == null) {

			myHolder = new MyHolder();
			convertView = LayoutInflater.from(myContent).inflate(
					R.layout.netmusiclistitem, null);
			convertView.setTag(myHolder);
			myHolder.txtmp3Name = (TextView) convertView
					.findViewById(R.id.netmusic_title);
			myHolder.txtmp3Artist = (TextView) convertView
					.findViewById(R.id.netmusic_artist);

			myHolder.mp3_album_image = (ImageView) convertView
					.findViewById(R.id.netmusic_album_image);
			myHolder.imgDown = (TextView) convertView
					.findViewById(R.id.netmusic_download);
		} else {
			myHolder = (MyHolder) convertView.getTag();
		}
		myHolder.txtmp3Name.setText(mylistData.get(position).getMp3Name());
		myHolder.txtmp3Artist.setText(mylistData.get(position).getMp3Artist());

		// 歌曲下载事件监听
		myHolder.imgDown.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickposition = position;
				Intent in = new Intent(myContent, DownLoadService.class);
				in.putExtra("musicInfo", mylistData.get(clickposition));
				myContent.startService(in);
				System.out.println("in-------" + in + clickposition);
			}
		});

		// 拼接图片路径(NetMusicFinals.MUSIC_IP)及图片名称
		String imgHead ="http://192.168.133.211:9999/music/songs/"
				+ mylistData.get(position).getMp3Image();

		// 绑定dTask
		myHolder.dTask = new DownLoadTask(myHolder.mp3_album_image);
		// AsyncTask会先执行execute方法并触发异步线程的执行
		myHolder.dTask.execute(imgHead);
		return convertView;
	}

	private final class MyHolder {
		TextView txtmp3Name;
		TextView txtmp3Artist;
		ImageView mp3_album_image;
		TextView imgDown;
		DownLoadTask dTask;
	}

	// 异步加载类
	class DownLoadTask extends AsyncTask<String, Integer, Bitmap> {
		Bitmap bm;
		ImageView imageView;

		DownLoadTask(ImageView v) {
			this.imageView = v;
		}

		// doInBackground函数里面存放耗时操作
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			MusicFileDown md = new MusicFileDown();
			// 接收输入参数和返回计算结果
			bm = md.GetImgToIntent(params[0]);

			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				imageView.setBackgroundResource(0);
				imageView.setImageBitmap(bm);
			} else {

			}

		}

	}
}