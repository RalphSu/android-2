package com.example.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;

import com.example.aidl.IRemoteService;
import com.example.localservice.R;


public class RemoteActivity extends Activity {
	IRemoteService mIRemoteService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote);
        
        Intent service=new Intent("com.example.service.query.pid");
		bindService(service, mConnection,Context.BIND_AUTO_CREATE);
    }

    public void getPID(View view)  {
		try {
			Toast.makeText(getApplicationContext(), "PID: "+mIRemoteService.getPid(), Toast.LENGTH_SHORT).show();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        mIRemoteService = IRemoteService.Stub.asInterface(service);
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        mIRemoteService = null;
	    }
	};
    
}
