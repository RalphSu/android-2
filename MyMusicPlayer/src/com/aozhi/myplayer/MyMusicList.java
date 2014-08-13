package com.aozhi.myplayer;

import java.util.List;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.aozhi.myplayer.MusicRetriever.Item;
import com.aozhi.myplayer.PrepareMusicRetrieverTask.MusicRetrieverPreparedListener;
import com.aozhi.myplayer.adapter.MusicListAdapter;
import com.aozhi.myplayer.t2.MusicPlayerService;

public class MyMusicList extends ListActivity implements MusicRetrieverPreparedListener {
	String TAG="MyMusicListTag";
	ListView listView;
	private List<Item> datas;
	private MusicRetriever mRetriever;
	
	private MusicPlayerService service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_music_list);
		listView=getListView();
		
		mRetriever = new MusicRetriever(getContentResolver());
		(new PrepareMusicRetrieverTask(mRetriever, this)).execute();
		
		bindService(new Intent(this,MusicPlayerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
		
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Item item=datas.get(position);
		service.setDataSource(item.getURI());
		Toast.makeText(getApplicationContext(), item.getURI().toString(), Toast.LENGTH_LONG).show();
		service.start();
	}

	@Override
	public void onMusicRetrieverPrepared() {
		datas=mRetriever.getMusicList();
		Log.i(TAG, "===========>"+datas.size());
		setListAdapter(new MusicListAdapter(this,datas));
	}
	
	private ServiceConnection serviceConnection=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			service=((MusicPlayerService.LocalBinder)binder).getService();
		}
	};
}
