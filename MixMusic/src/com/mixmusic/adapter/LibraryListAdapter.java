package com.mixmusic.adapter;

import java.util.HashMap;
import java.util.List;

import com.mixmusic.R;
import com.mixmusic.biz.ApiConfigs;
import com.mixmusic.biz.AppConstant;
import com.mixmusic.service.PlayerService;
import com.mixmusic.utils.DialogUtil;
import com.mixmusic.view.FindActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LibraryListAdapter extends BaseAdapter {

	private Context context;
	private List<HashMap<String, Object>> data;
	private boolean mBusy = false;
	private int currItemIndex = -1;
	private boolean isPlaying = false;
	private int current = -1;

	public LibraryListAdapter(Context context, List<HashMap<String, Object>> data) {
		this.context = context;
		this.data = data;
	}

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	public void setData(List<HashMap<String, Object>> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		final HashMap<String, Object> info = data.get(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.library_list_item, null);
			viewHolder = new ViewHolder();
			getViewHolder(convertView, viewHolder);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (info.get("id").equals(ApiConfigs.selectId)) {
			viewHolder.imageview_selected.setVisibility(View.VISIBLE);
		}
		else
		{
			viewHolder.imageview_selected.setVisibility(View.GONE);
		}

		if (position == currItemIndex) {
			viewHolder.imageview_play
					.setBackgroundResource(R.drawable.list_stop);
		} else {

			viewHolder.imageview_play
					.setBackgroundResource(R.drawable.list_icon);
		}
		
		
		viewHolder.textview_ring.setText(info.get("musicName").toString());
//
		viewHolder.textview_ring.setText(info.get("musicName").toString());
		//
		viewHolder.imageview_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currItemIndex = position;
				System.out.println("----musicUrl----"
						+ info.get("musicUrl").toString());
				if (current == position) {
					if (isPlaying) {
						viewHolder.imageview_play
								.setBackgroundResource(R.drawable.list_icon);
						play(info.get("musicUrl").toString(),
								AppConstant.PlayerMsg.STOP_MSG);
					} else {
						viewHolder.imageview_play
								.setBackgroundResource(R.drawable.list_icon);
						play(info.get("musicUrl").toString(),
								AppConstant.PlayerMsg.PLAY_MSG);
						notifyDataSetChanged();
					}
				} else {
					if (isPlaying) {
						viewHolder.imageview_play
								.setBackgroundResource(R.drawable.list_icon);
					} else {
						viewHolder.imageview_play
								.setBackgroundResource(R.drawable.list_stop);
					}
					play(info.get("musicUrl").toString(),
							AppConstant.PlayerMsg.PLAY_MSG);
					notifyDataSetChanged();
				}
				current = position;

			}
		});
		
		convertView.setFocusable(false);
		return convertView;
	}

	void getViewHolder(View view, ViewHolder viewHolder) {
		viewHolder.imageview_play = (ImageView) view
				.findViewById(R.id.imageview_play);
		viewHolder.textview_ring = (TextView) view
				.findViewById(R.id.textview_ring);
		viewHolder.imageview_selected = (ImageView) view
				.findViewById(R.id.imageview_selected);
		viewHolder.layout_select = (RelativeLayout) view
				.findViewById(R.id.layout_select);

	}

	class ViewHolder {
		TextView textview_ring, textview_selected;
		ImageView imageview_play, imageview_selected;
		RelativeLayout layout_select;
	}
	
	/**
	 * ≤•∑≈“Ù¿÷
	 * 
	 * @param playUrl
	 * @param action
	 */
	protected void play(String playUrl, int action) {
		if (action == AppConstant.PlayerMsg.PLAY_MSG) {
			isPlaying = true;
		} else {
			isPlaying = false;
		}

		Intent playerIntent = new Intent();
		playerIntent.putExtra("PLAY_URL", playUrl);
		playerIntent.putExtra("PLAY_MSG", action);
		playerIntent.setClass(context, PlayerService.class);
		context.startService(playerIntent);
	}

}
