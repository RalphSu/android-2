package com.mixmusic.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mixmusic.R;
import com.mixmusic.biz.AppConstant;
import com.mixmusic.biz.BizManager;
import com.mixmusic.service.PlayerService;
import com.mixmusic.utils.DialogUtil;
import com.mixmusic.utils.Util;
import com.mixmusic.utils.ViewUtil;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.openapi.IWXAPI;

public class MineListAdapter extends BaseAdapter {

	private Context context;
	private IWXAPI api;
	private boolean isPlaying = false;
	private int current = -1;
	private int currMoreItemIndex = -1;
	private List<HashMap<String, Object>> data;
	// 重新获取数据
	private Handler loadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 1) {
				{
					data = (List<HashMap<String, Object>>) msg.obj;
					setData(data);
				}
			}
			super.handleMessage(msg);
		}
	};
	// 得到点赞
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 1) {
				BizManager.getInstance().getMySongList(context, 1, 15, loadHandler);
				DialogUtil.getInstance().ShowToast(context, "删除成功");
			} else {
				DialogUtil.getInstance().ShowToast(context, msg.obj.toString());
			}
			currMoreItemIndex = -1;
			notifyDataSetChanged();

			super.handleMessage(msg);
		}
	};

	public MineListAdapter(Context context, List<HashMap<String, Object>> data) {
		this.context = context;
		this.data = data;
	}
	public MineListAdapter(Context context, IWXAPI api,List<HashMap<String, Object>> data) {
		this.context = context;
		this.data = data;
	}

	public void setData(List<HashMap<String, Object>> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {

		return data.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder viewHolder;
		final HashMap<String, Object> info = data.get(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.find_list_item, null);
			viewHolder = new ViewHolder();
			getViewHolder(convertView, viewHolder);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.textview_number.setText(String.valueOf(position + 1));
		viewHolder.textview_song.setText(info.get("songName").toString());
		viewHolder.textview_praise.setText(info.get("clickNum").toString());

		initMoreGrid(viewHolder.moreGrid);
		// 弹出折叠隐藏
		if (position == currMoreItemIndex) {
			viewHolder.moreGrid.setVisibility(View.VISIBLE);// 显示弹出框
			viewHolder.textview_number.setVisibility(View.GONE);
			viewHolder.imageview_play.setVisibility(View.VISIBLE);
			viewHolder.textview_song.setTextColor(context.getResources().getColor(R.color.main_bg_color));
		} else {
			viewHolder.moreGrid.setVisibility(View.GONE);// 隐藏弹出框
			viewHolder.textview_number.setVisibility(View.VISIBLE);
			viewHolder.imageview_play.setVisibility(View.GONE);
			viewHolder.textview_song.setTextColor(context.getResources().getColor(R.color.tab_text_select));
		}
		viewHolder.textview_song.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (viewHolder.moreGrid.getVisibility() != View.VISIBLE) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							Looper.prepare();
							DialogUtil.getInstance().ShowToast(context, "正在播放：" + info.get("songName").toString());
						}
					}).start();
					viewHolder.textview_number.setVisibility(View.GONE);
					viewHolder.imageview_play.setVisibility(View.VISIBLE);
					viewHolder.textview_song.setTextColor(context.getResources().getColor(R.color.main_bg_color));
					play(info.get("songUrl").toString(), AppConstant.PlayerMsg.PLAY_MSG);
					currMoreItemIndex = position;
				} else {
					viewHolder.textview_number.setVisibility(View.VISIBLE);
					viewHolder.imageview_play.setVisibility(View.GONE);
					viewHolder.textview_song.setTextColor(context.getResources().getColor(R.color.tab_text_select));
					currMoreItemIndex = -1;
					play(info.get("songUrl").toString(), AppConstant.PlayerMsg.STOP_MSG);
				}
				notifyDataSetChanged();
			}

		});
		viewHolder.layout_praise.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				current = position;
				BizManager.getInstance().setPraise(context, info.get("id").toString(), handler);
			}
		});
		convertView.setFocusable(false);
		return convertView;
	}

	void getViewHolder(View view, ViewHolder viewHolder) {
		viewHolder.layout_item = (LinearLayout) view.findViewById(R.id.layout_item);
		viewHolder.imageview_play = (ImageView) view.findViewById(R.id.imageview_play);
		viewHolder.textview_number = (TextView) view.findViewById(R.id.textview_number);
		viewHolder.textview_song = (TextView) view.findViewById(R.id.textview_song);
		viewHolder.textview_praise = (TextView) view.findViewById(R.id.textview_praise);
		viewHolder.layout_praise = (LinearLayout) view.findViewById(R.id.layout_praise);
		viewHolder.moreGrid = (GridView) view.findViewById(R.id.more_grid);

	}

	class ViewHolder {
		TextView textview_number, textview_song, textview_praise;
		ImageView imageview_play;
		LinearLayout layout_item, layout_praise;
		GridView moreGrid;
	}

	void initMoreGrid(GridView moreGrid) {
		final List<MoreItem> MoreItem = new ArrayList<MoreItem>();
		MoreItem.add(new MoreItem(context.getResources().getDrawable(R.drawable.delete), "删 除", 0));
		MoreItem.add(new MoreItem(context.getResources().getDrawable(R.drawable.zring), "设为振铃", 1));
		MoreItem.add(new MoreItem(context.getResources().getDrawable(R.drawable.share_wx), "分享", 2));
		if (MoreItem.size() <= 5) {
			moreGrid.getLayoutParams().height = ViewUtil.dip2px(context, 70);
		} else if (MoreItem.size() <= 10) {
			moreGrid.getLayoutParams().height = ViewUtil.dip2px(context, 70 * 2);
		}
		moreGrid.setAdapter(new MoreGridAdapter(context, MoreItem));
		moreGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onMoreGridItemClick(MoreItem.get(position));
			}
		});
	}

	void onMoreGridItemClick(MoreItem moreItem) {
		final HashMap<String, Object> info = data.get(currMoreItemIndex);
		if (currMoreItemIndex != -1) {
			switch (moreItem.id) {
			case 0:
				BizManager.getInstance().delete(context, info.get("id").toString(), handler);
				break;
			case 1:
				break;
			case 2:
				//share(info);
				Toast.makeText(context, "APPID申请中，敬请期待.....^_^", Toast.LENGTH_LONG).show();
				break;
			}
		}
	}

	private void share(HashMap<String, Object> info) {
		
		WXMusicObject music = new WXMusicObject();
		music.musicUrl=info.get("songUrl").toString();

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = music;
		msg.title = info.get("songName").toString();
		msg.description = "我自己合成的音乐,呵呵";
		Log.i("myapp", "==========>"+msg.title);

		Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.send_music_thumb);
		msg.thumbData = Util.bmpToByteArray(thumb, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("music");
		req.message = msg;
		req.scene =    SendMessageToWX.Req.WXSceneSession;//SendMessageToWX.Req.WXSceneTimeline
		api.sendReq(req);
		
	}

	/**
	 * 播放音乐
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
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}