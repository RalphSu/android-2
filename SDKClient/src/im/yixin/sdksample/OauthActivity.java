package im.yixin.sdksample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendAuthToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.util.YixinConstants;

import java.net.URISyntaxException;

/**
 * @author gyq 13-11-20上午11:18
 */
public class OauthActivity extends Activity {
	private IYXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		api = YXAPIFactory.createYXAPI(this, YixinConstants.TEST_APP_ID);
		api.registerApp();
		setContentView(R.layout.oauth);
		initView();
	}

	private void initView() {
		findViewById(R.id.reg_btn).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SendAuthToYX.Req req = new SendAuthToYX.Req();
				req.state = "asdfsdaf";
				req.transaction = String.valueOf(System.currentTimeMillis());
				api.sendRequest(req);
			}
		});

		findViewById(R.id.goto_send_btn).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "https://open.yixin.im/oauth/authorize?response_type=code&client_id="
						+ YixinConstants.TEST_APP_ID;
				Intent intent;
				// perform generic parsing of the URI to turn it into an Intent.
				try {
					intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
				} catch (URISyntaxException ex) {
					return;
				}
				// 安装了app
				if (intent != null && getPackageManager().resolveActivity(intent, 0) != null) {
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.setComponent(null);
					// 在新进程中打开app，否则没有第三方app的独立进程
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						if (startActivityIfNeeded(intent, -1)) {
							return;
						}
					} catch (ActivityNotFoundException ex) {
						// ignore the error. If no application can handle the
						// URL,
						// eg about:blank, assume the browser can handle it.
					}
				}
			}
		});
	}
}
