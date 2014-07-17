/**
 * 
 */
package im.yixin.sdksample.yxapi;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import im.yixin.sdk.api.BaseReq;
import im.yixin.sdk.api.BaseResp;
import im.yixin.sdk.api.BaseYXEntryActivity;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendAuthToYX;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.util.YixinConstants;
import im.yixin.sdksample.R;

/**
 * @author yixinopen@188.com
 * 
 */
public class YXEntryActivity extends BaseYXEntryActivity {

	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

	private Button gotoBtn, regBtn, launchBtn, checkBtn;

	private IYXAPI api = null;

	private void showUI() {
		setContentView(R.layout.yixin_callback);
		Intent intent = getIntent();
		// TextView tv = (TextView)this.findViewById(R.id.result_tv);
		// tv.setText("参数：" +
		// intent.getStringExtra(YixinConstants.KEY_CONTENT));
	}

	/*******************
	 * 返回第三方app根据app id创建的IYXAPI，
	 * 
	 * @return
	 */
	protected IYXAPI getIYXAPI() {
		return YXAPIFactory.createYXAPI(this, YixinConstants.TEST_APP_ID);
	}

	/**
	 * 易信调用调用时的触发函数
	 */
	@Override
	public void onResp(BaseResp resp) {
		this.showUI();
		Log.i("Yixin.SDK.YXEntryActivity", "onResp called: errCode=" + resp.errCode + ",errStr=" + resp.errStr
				+ ",transaction=" + resp.transaction);
		switch (resp.getType()) {
		case YixinConstants.RESP_SEND_MESSAGE_TYPE:
			SendMessageToYX.Resp resp1 = (SendMessageToYX.Resp) resp;
			switch (resp1.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				Toast.makeText(YXEntryActivity.this, "分享成功", Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_COMM:
				Toast.makeText(YXEntryActivity.this, "分享失败", Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				Toast.makeText(YXEntryActivity.this, "用户取消", Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_SENT_FAILED:
				Toast.makeText(YXEntryActivity.this, "发送失败", Toast.LENGTH_LONG).show();
				break;
			}
			break;
		case YixinConstants.RESP_SEND_AUTH_TYPE:
			SendAuthToYX.Resp resp2 = (SendAuthToYX.Resp) resp;
			switch (resp2.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				Toast.makeText(YXEntryActivity.this, "获取Code成功，code=" + resp2.code, Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_COMM:
				Toast.makeText(YXEntryActivity.this, "失败", Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				Toast.makeText(YXEntryActivity.this, "用户拒绝", Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				Toast.makeText(YXEntryActivity.this, "用户拒绝", Toast.LENGTH_LONG).show();
				break;
			}
		}

	}

	/**
	 * 易信调用调用时的触发函数
	 */
	@Override
	public void onReq(BaseReq req) {
		this.showUI();
		Log.i("YX-SDK-Client", "onReq called: transaction=" + req.transaction);
		switch (req.getType()) {
		case YixinConstants.RESP_SEND_MESSAGE_TYPE:
			SendMessageToYX.Req req1 = (SendMessageToYX.Req) req;
			Toast.makeText(YXEntryActivity.this, req1.message.title, Toast.LENGTH_LONG).show();
		}
	}
}
