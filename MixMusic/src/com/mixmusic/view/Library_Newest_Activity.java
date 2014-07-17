package com.mixmusic.view;

import java.util.HashMap;
import java.util.List;

import com.mixmusic.R;
import com.mixmusic.adapter.LibraryListAdapter;
import com.mixmusic.biz.ApiConfigs;
import com.mixmusic.biz.BizManager;
import com.mixmusic.listview.refresh.PullDownView;
import com.mixmusic.listview.refresh.PullDownView.OnPullDownListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Library_Newest_Activity extends Activity implements
		OnPullDownListener, OnItemClickListener {

	private final static String TAG = "Library_Hot_Activity";
	private Context mContext;
	private Handler resultHandler;
	private PullDownView mPullDownView;
	private ListView listview_newest;
	private List<HashMap<String, Object>> ringList;
	private LibraryListAdapter adapter;
	private int index = 1;
	private boolean isRefer = false;
	private boolean isMore = false;
	private BizManager biz = BizManager.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library_newest_list);
		mContext = Library_Newest_Activity.this;
		initView();
		initHandler();
		initData(index);
	}

	/**
	 * ���ؿؼ�
	 */
	private void initView() {
		// TODO Auto-generated method stub
		mPullDownView = (PullDownView) findViewById(R.id.pull_down_view);
		mPullDownView.setOnPullDownListener(this);
		// ����ȡ���ظ���
		mPullDownView.enableAutoFetchMore(false, 1);

		listview_newest = mPullDownView.getListView();
		listview_newest.setDivider(getResources().getDrawable(R.drawable.line));
		listview_newest.setOnItemClickListener(this);
	}

	/**
	 * ��������
	 */
	private void initData(int page) {
		// TODO Auto-generated method stub
		biz.getRingList(mContext, page, 15, 2, resultHandler);
	}

	/**
	 * ����Handler
	 */
	@SuppressLint("HandlerLeak")
	private void initHandler() {
		// TODO Auto-generated method stub
		resultHandler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {

				if (msg.what == 1) {
					List<HashMap<String, Object>> data = (List<HashMap<String, Object>>) msg.obj;
					if (index == 1) {
						// ���ݸ�ֵ
						ringList = data;
						if (isRefer) {
							adapter.setData(ringList);
							adapter.notifyDataSetChanged();
							// ��������ˢ�����;
							mPullDownView.notifyDidRefresh();
							isRefer = false;
						} else {

							initListAdapter(ringList, listview_newest);
							// �������ݼ������;
							mPullDownView.notifyDidLoad();
						}
					} else {
						ringList.addAll(data);
						adapter.notifyDataSetChanged();
						// ��������ȡ�������
						mPullDownView.notifyDidMore();
						isMore = false;
					}

				}

				super.handleMessage(msg);
			}
		};
	}

	/**
	 * ����Adapter
	 * 
	 * @param ringList
	 * @param listview_hot
	 */
	private void initListAdapter(List<HashMap<String, Object>> ringList,
			ListView listview_hot) {
		// TODO Auto-generated method stub
		if (null == adapter) {
			adapter = new LibraryListAdapter(mContext, ringList);
			listview_hot.setAdapter(adapter);
		} else {
			adapter.setData(ringList);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		// TODO Auto-generated method stub
		HashMap<String, Object> data = ringList.get(position);
		ApiConfigs.selectId=String.valueOf(data.get("id"));
		ApiConfigs.selectName=String.valueOf(data.get("musicName"));
	
		Intent i = new Intent();
		i.setAction("MusicChange");
		mContext.sendBroadcast(i);
		adapter.notifyDataSetChanged();
	
	}


	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				isRefer = true;
				index = 1;
				initData(index);
			}
		}).start();
	}

	@Override
	public void onMore() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				isMore = true;
				index++;

				initData(index);
			}
		}).start();
	}
}
