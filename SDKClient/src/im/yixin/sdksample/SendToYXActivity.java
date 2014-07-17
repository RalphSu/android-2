package im.yixin.sdksample;

import java.io.File;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.api.YXImageMessageData;
import im.yixin.sdk.api.YXMessage;
import im.yixin.sdk.api.YXMusicMessageData;
import im.yixin.sdk.api.YXTextMessageData;
import im.yixin.sdk.api.YXVideoMessageData;
import im.yixin.sdk.api.YXWebPageMessageData;
import im.yixin.sdk.util.BitmapUtil;
import im.yixin.sdk.util.YixinConstants;

public class SendToYXActivity extends Activity {

	private static final int THUMB_SIZE = 80;

	private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

	private IYXAPI api;

	private static final int YXAlertSelect1 = 0;

	private static final int YXAlertSelect2 = 1;

	private static final int YXAlertSelect3 = 2;

	private CheckBox isTimelineCb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		api = YXAPIFactory.createYXAPI(this, YixinConstants.TEST_APP_ID);
		api.registerApp();
		setContentView(R.layout.send_to_yx);
		initView();
	}

	private void initView() {

		isTimelineCb = (CheckBox) findViewById(R.id.is_timeline_cb);
		isTimelineCb.setChecked(false);

		// send to yixin
		findViewById(R.id.send_text).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				final EditText editor = new EditText(SendToYXActivity.this);
				editor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
				editor.setText(R.string.send_text_default);

				YXAlert.showAlert(SendToYXActivity.this, "send text data", editor, getString(R.string.app_share),
						getString(R.string.app_cancel), new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								String text = editor.getText().toString();
								if (text == null || text.length() == 0) {
									return;
								}

								// 初始化一个YXTextObject对象
								YXTextMessageData textObj = new YXTextMessageData();
								textObj.text = text;

								// 用YXTextObject对象初始化一个YXMessage对象
								YXMessage msg = new YXMessage();
								msg.messageData = textObj;
								// 发送文本类型的消息时，title字段不起作用
								// msg.title = "title is ignored";
								msg.description = text;

								// 构造一个Req对象
								SendMessageToYX.Req req = new SendMessageToYX.Req();
								// transaction字段用于唯一标识一个请求
								req.transaction = buildTransaction("text"); 
								req.message = msg;
								req.scene = isTimelineCb.isChecked() ? SendMessageToYX.Req.YXSceneTimeline
										: SendMessageToYX.Req.YXSceneSession;

								// 调用api接口发送数据到易信
								api.sendRequest(req);
								// finish();
							}
						}, null);
			}
		});

		findViewById(R.id.send_img).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				YXAlert.showAlert(SendToYXActivity.this, getString(R.string.send_img), SendToYXActivity.this
						.getResources().getStringArray(R.array.send_img_item), null, new YXAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case YXAlertSelect1: {
							Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test300);
							YXImageMessageData imgObj = new YXImageMessageData(bmp);

							YXMessage msg = new YXMessage();
							msg.messageData = imgObj;
							Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 200, 200, true);
							bmp.recycle();
							msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true); // 设置缩略图
							
							SendMessageToYX.Req req = new SendMessageToYX.Req();
							req.transaction = buildTransaction("img");
							req.message = msg;
							req.scene = isTimelineCb.isChecked() ? SendMessageToYX.Req.YXSceneTimeline
									: SendMessageToYX.Req.YXSceneSession;
							api.sendRequest(req);

							// finish();
							break;
						}
						case YXAlertSelect2: {
							String path = SDCARD_ROOT + "/test.png";
							File file = new File(path);
							if (!file.exists()) {
								String tip = SendToYXActivity.this.getString(R.string.send_img_file_not_exist);
								Toast.makeText(SendToYXActivity.this, tip + " path = " + path, Toast.LENGTH_LONG)
										.show();
								break;
							}

							YXImageMessageData imgObj = new YXImageMessageData();
							imgObj.imagePath = path;

							YXMessage msg = new YXMessage();
							msg.messageData = imgObj;
							Bitmap bmp = BitmapFactory.decodeFile(path);
							Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
							bmp.recycle();
							msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true);

							SendMessageToYX.Req req = new SendMessageToYX.Req();
							req.transaction = buildTransaction("img");
							req.message = msg;
							req.scene = isTimelineCb.isChecked() ? SendMessageToYX.Req.YXSceneTimeline
									: SendMessageToYX.Req.YXSceneSession;
							api.sendRequest(req);

							// finish();
							break;
						}
						case YXAlertSelect3: {
							String url = "http://img1.gamersky.com/image2013/08/20130824u_4/gamersky_10small_20_2013824120334.jpg";

							try {
								YXImageMessageData imgObj = new YXImageMessageData();
								imgObj.imageUrl = url;

								YXMessage msg = new YXMessage();
								msg.messageData = imgObj;
								
								//Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openStream());
								Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test300);
								// 这里修改150参数可以测试缩略图质量情况
								Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 200, 200, true);
								bmp.recycle();
								msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true); // 设置缩略图

								SendMessageToYX.Req req = new SendMessageToYX.Req();
								req.transaction = buildTransaction("img");
								req.message = msg;
								req.scene = isTimelineCb.isChecked() ? SendMessageToYX.Req.YXSceneTimeline
										: SendMessageToYX.Req.YXSceneSession;
								api.sendRequest(req);

								// finish();
							} catch (Exception e) {
								e.printStackTrace();
							}

							break;
						}
						default:
							break;
						}
					}

				});
			}
		});

		findViewById(R.id.send_music).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				YXAlert.showAlert(SendToYXActivity.this, getString(R.string.send_music), SendToYXActivity.this
						.getResources().getStringArray(R.array.send_music_item), null, new YXAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case YXAlertSelect1: {
							YXMusicMessageData music = new YXMusicMessageData();
							music.musicUrl = "http://3g.163.com/ntes/special/0034073A/wechat_article.html?docid=978FP00H00014AED";
							music.musicDataUrl = "http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";
							
							// 低宽带
							//music.musicLowBandUrl = "http://3g.163.com/ntes/special/0034073A/wechat_article.html?docid=978FP00H00014AED";
							//music.musicLowBandDataUrl = "http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";
							
							YXMessage msg = new YXMessage();
							msg.messageData = music;
							// msg.title = "Music Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
							msg.title = "反方向的钟";
							// msg.description = "Music Album Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
							msg.description = "蔡琴";
							Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.test);
							msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);

							SendMessageToYX.Req req = new SendMessageToYX.Req();
							req.transaction = buildTransaction("music");
							req.message = msg;
							req.scene = isTimelineCb.isChecked() ? SendMessageToYX.Req.YXSceneTimeline
									: SendMessageToYX.Req.YXSceneSession;
							api.sendRequest(req);

							// finish();
							break;
						}
						case YXAlertSelect2: {
							YXMusicMessageData music = new YXMusicMessageData();
							music.musicLowBandUrl = "http://3g.163.com/ntes/special/0034073A/wechat_article.html?docid=978FP00H00014AED";
							music.musicDataUrl = "http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";
							YXMessage msg = new YXMessage();
							msg.messageData = music;
							msg.title = "Music Title";
							msg.description = "Music Album";
							Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.test);
							msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);

							SendMessageToYX.Req req = new SendMessageToYX.Req();
							req.transaction = buildTransaction("music");
							req.message = msg;
							req.scene = isTimelineCb.isChecked() ? SendMessageToYX.Req.YXSceneTimeline
									: SendMessageToYX.Req.YXSceneSession;
							api.sendRequest(req);

							// finish();
							break;
						}
						default:
							break;
						}
					}
				});
			}
		});

		findViewById(R.id.send_video).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				YXAlert.showAlert(SendToYXActivity.this, getString(R.string.send_video), SendToYXActivity.this
						.getResources().getStringArray(R.array.send_video_item), null, new YXAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case YXAlertSelect1: {
							YXVideoMessageData video = new YXVideoMessageData();
							video.videoUrl = "http://3g.163.com/ntes/special/0034073A/wechat_article.html?docid=978FP00H00014AED";

							YXMessage msg = new YXMessage(video);
							msg.title = "Vidong Very Loy Long Very Long Very Long";
							msg.description = "Video Long Very LonLong Very Long Very Long";
							Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.test);
							msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);
							SendMessageToYX.Req req = new SendMessageToYX.Req();
							req.transaction = buildTransaction("video");
							req.message = msg;
							req.scene = isTimelineCb.isChecked() ? SendMessageToYX.Req.YXSceneTimeline
									: SendMessageToYX.Req.YXSceneSession;
							api.sendRequest(req);

							// finish();
							break;
						}
						case YXAlertSelect2: {
							YXVideoMessageData video = new YXVideoMessageData();
							video.videoLowBandUrl = "http://3g.163.com/ntes/special/0034073A/wechat_article.html?docid=978FP00H00014AED";

							YXMessage msg = new YXMessage(video);
							msg.title = "Video Title";
							msg.description = "Video Description";
							SendMessageToYX.Req req = new SendMessageToYX.Req();
							req.transaction = buildTransaction("video");
							req.message = msg;
							req.scene = isTimelineCb.isChecked() ? SendMessageToYX.Req.YXSceneTimeline
									: SendMessageToYX.Req.YXSceneSession;
							api.sendRequest(req);

							// finish();
							break;
						}
						default:
							break;
						}
					}
				});
			}
		});

		findViewById(R.id.send_webpage).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				YXAlert.showAlert(SendToYXActivity.this, getString(R.string.send_webpage), SendToYXActivity.this
						.getResources().getStringArray(R.array.send_webpage_item), null, new YXAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case YXAlertSelect1:
							YXWebPageMessageData webpage = new YXWebPageMessageData();
							webpage.webPageUrl = "http://3g.163.com/ntes/special/0034073A/wechat_article.html?docid=978FP00H00014AED";
							YXMessage msg = new YXMessage(webpage);
							// msg.title = "WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
							msg.title = "好的大幅度发斯度好的大幅度发斯蒂芬速度幅度";
							// msg.description = "WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
							msg.description = "幅度发斯蒂芬速度好的大幅度发斯蒂芬速度幅度发斯蒂芬速度好的大幅度发斯蒂";
							Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.test);
							msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);
							SendMessageToYX.Req req = new SendMessageToYX.Req();
							req.transaction = buildTransaction("webpage");
							req.message = msg;
							req.scene = isTimelineCb.isChecked() ? SendMessageToYX.Req.YXSceneTimeline
									: SendMessageToYX.Req.YXSceneSession;
							api.sendRequest(req);

							// finish();
							break;
						default:
							break;
						}
					}
				});
			}
		});

		// unregister from yixin
		findViewById(R.id.unregister).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				api.unRegisterApp();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case 0x101: {
		}
		default:
			break;
		}
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
