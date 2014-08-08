package com.aozhi.util.uikit;

import java.io.File;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.aozhi.myplayer.R;

public class TestAlertActivity extends Activity {


	private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

	private static final int MMAlertSelect1 = 0;
	private static final int MMAlertSelect2 = 1;
	private static final int MMAlertSelect3 = 2;

	private CheckBox isTimelineCb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.send_to_wx);
		initView();
	}

	private void initView() {

		isTimelineCb = (CheckBox) findViewById(R.id.is_timeline_cb);
		isTimelineCb.setChecked(false);

		// send to weixin
		findViewById(R.id.send_text).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				final EditText editor = new EditText(TestAlertActivity.this);
				editor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
				editor.setText(R.string.send_text_default);

				MMAlert.showAlert(TestAlertActivity.this, "send text", editor, getString(R.string.app_share),
						getString(R.string.app_cancel), new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								String text = editor.getText().toString();
								if (text == null || text.length() == 0) {
									return;
								}

								finish();
							}
						}, null);
			}
		});

		findViewById(R.id.send_img).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MMAlert.showAlert(TestAlertActivity.this, getString(R.string.send_img), TestAlertActivity.this.getResources()
						.getStringArray(R.array.send_img_item), null, new MMAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case MMAlertSelect1: {
							Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.send_img);

							finish();
							break;
						}
						case MMAlertSelect2: {
							String path = SDCARD_ROOT + "/test.png";
							File file = new File(path);
							if (!file.exists()) {
								String tip = TestAlertActivity.this.getString(R.string.send_img_file_not_exist);
								Toast.makeText(TestAlertActivity.this, tip + " path = " + path, Toast.LENGTH_LONG).show();
								break;
							}


							finish();
							break;
						}
						case MMAlertSelect3: {
							String url = "http://weixin.qq.com/zh_CN/htmledition/images/weixin/weixin_logo0d1938.png";

							try {

								finish();
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

				MMAlert.showAlert(TestAlertActivity.this, getString(R.string.send_music), TestAlertActivity.this.getResources()
						.getStringArray(R.array.send_music_item), null, new MMAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case MMAlertSelect1: {

							finish();
							break;
						}
						case MMAlertSelect2: {

							finish();
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
				MMAlert.showAlert(TestAlertActivity.this, getString(R.string.send_video), TestAlertActivity.this.getResources()
						.getStringArray(R.array.send_video_item), null, new MMAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case MMAlertSelect1: {

							finish();
							break;
						}
						case MMAlertSelect2: {

							finish();
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
				MMAlert.showAlert(TestAlertActivity.this, getString(R.string.send_webpage), TestAlertActivity.this.getResources()
						.getStringArray(R.array.send_webpage_item), null, new MMAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case MMAlertSelect1:

							finish();
							break;
						default:
							break;
						}
					}
				});
			}
		});

		findViewById(R.id.send_appdata).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MMAlert.showAlert(TestAlertActivity.this, getString(R.string.send_appdata), TestAlertActivity.this.getResources()
						.getStringArray(R.array.send_appdata_item), null, new MMAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case MMAlertSelect1:
							final String dir = SDCARD_ROOT + "/tencent/";
							File file = new File(dir);
							if (!file.exists()) {
								file.mkdirs();
							}
							CameraUtil.takePhoto(TestAlertActivity.this, dir, "send_appdata", 0x101);
							break;
						case MMAlertSelect2: {

							finish();
							break;
						}
						case MMAlertSelect3: {

							finish();
							break;
						}
						default:
							break;
						}
					}

				});
			}
		});

		findViewById(R.id.send_emoji).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MMAlert.showAlert(TestAlertActivity.this, getString(R.string.send_emoji), TestAlertActivity.this.getResources()
						.getStringArray(R.array.send_emoji_item), null, new MMAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						final String EMOJI_FILE_PATH = SDCARD_ROOT + "/emoji.gif";
						final String EMOJI_FILE_THUMB_PATH = SDCARD_ROOT + "/emojithumb.jpg";
						switch (whichButton) {
						case MMAlertSelect1: {

							finish();
							break;
						}

						case MMAlertSelect2: {

							finish();
							break;
						}
						default:
							break;
						}
					}
				});
			}
		});

	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
