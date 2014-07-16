package com.recordmix.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class DialogUtil {

	private static DialogUtil instance;
    public 	ProgressDialog progressdialog ;
	public static DialogUtil getInstance() {
		if (instance == null) {
			instance = new DialogUtil();
		}
		return instance;
	}

	/**
	 * 弹出改名并保存记录
	 * @param context
	 * @param songUrl
	 */
	public void ShowAlertDialog(final Context context, final String songUrl) {

		CustomDialog.Builder builder = new CustomDialog.Builder(context);
		builder.setMessage("保存后可在曲库查找");
		builder.setTitle("温馨提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//设置你的操作事项
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
}
	public void ShowMixedDialog(final Context context) {

		CustomDialog.Builder builder = new CustomDialog.Builder(context);
		builder.setMessage("合成完毕，记录已保存到我的曲库");
		builder.setTitle("提示");
		builder.setPositiveButton("试听", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//设置你的操作事项
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}
	/**
	 * 显示普通提示框
	 * @param context
	 * @param msg
	 */
	public void ShowToast(Context context,String msg)
	{
		Toast.makeText(context, msg,Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 显示合成框
	 * @param context
	 * @param isShow
	 */
	public void ProgressDialog(Context context,boolean isShow)
	{
		if (isShow) {
			progressdialog=ProgressDialog.show(context, "请等待...", "正在合成歌曲..."); 
		}
		else
		{
			progressdialog.dismiss();
		}

	}
}
