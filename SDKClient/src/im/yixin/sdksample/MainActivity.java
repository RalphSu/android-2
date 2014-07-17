package im.yixin.sdksample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.util.YixinConstants;

public class MainActivity extends Activity {

	private Button gotoBtn, regBtn, oauth;

	// IYXAPI 是第三方app和易信通信的openapi接口
	private IYXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry);

		// 通过YXAPIFactory工厂，获取IYXAPI的实例
		api = YXAPIFactory.createYXAPI(this, YixinConstants.TEST_APP_ID);

		regBtn = (Button) findViewById(R.id.reg_btn);
		regBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 将该app注册到易信
				api.registerApp();
			}
		});

		gotoBtn = (Button) findViewById(R.id.goto_send_btn);
		gotoBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, SendToYXActivity.class));
				finish();
			}
		});
		oauth = (Button) findViewById(R.id.launch_yx_btn);
		oauth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, OauthActivity.class));
				finish();
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
	}

}
