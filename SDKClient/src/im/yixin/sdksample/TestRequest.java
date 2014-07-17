/**
 * 
 */
package im.yixin.sdksample;

import im.yixin.sdk.api.BaseReq;
import im.yixin.sdk.api.ExceptionInfo;

/**
 * @author yixinopen@188.com
 * 
 */
public class TestRequest extends BaseReq {
	/*
	 * (non-Javadoc)
	 * 
	 * @see im.yixin.sdk.api.BaseReq#getType()
	 */
	@Override
	public int getType() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see im.yixin.sdk.api.BaseReq#checkArgs()
	 */
	@Override
	public boolean checkArgs(ExceptionInfo info) {
		return true;
	}
}
