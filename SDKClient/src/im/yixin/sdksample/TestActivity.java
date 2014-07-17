/**
 * 
 */
package im.yixin.sdksample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.util.YixinConstants;

/**
 * @author yixinopen@188.com
 * 
 */
public class TestActivity extends Activity {

	private Button gotoBtn, regBtn, launchBtn, checkBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry);
		final IYXAPI api = YXAPIFactory.createYXAPI(this, YixinConstants.TEST_APP_ID);
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
				api.sendRequest(new TestRequest());
			}
		});

		gotoBtn = (Button) findViewById(R.id.goto_send_btn);

		launchBtn = (Button) findViewById(R.id.launch_yx_btn);

		checkBtn = (Button) findViewById(R.id.check_timeline_supported_btn);
	}

}
