package com.example.sharing;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;

public class SendingActivity extends Activity {
	private ShareActionProvider mShareActionProvider;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sending);

	}

	public void sendText(View view) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
		sendIntent.setType("text/plain");
		// specify a title for the chooser dialog
		startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_title)));
	}

	public void sendBinary(View view) {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, "http://www.baidu.com/img/bdlogo.gif");
		shareIntent.setType("image/gif");
		startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_title)));
	}

	public void sendMultiple(View view) {
		ArrayList<Uri> imageUris = new ArrayList<Uri>();
		//imageUris.add(imageUri1); // Add your image URIs here
		//imageUris.add(imageUri2);

		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
		shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
		shareIntent.setType("image/*");
		startActivity(Intent.createChooser(shareIntent, "Share images to.."));
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate menu resource file.
	    getMenuInflater().inflate(R.menu.sending, menu);

	    // Locate MenuItem with ShareActionProvider
	    MenuItem item = menu.findItem(R.id.menu_item_share);

	    // Fetch and store ShareActionProvider
	    mShareActionProvider = (ShareActionProvider) item.getActionProvider();

	    setShareIntent(getDefaultShareIntent());
	    
	    // Return true to display menu
	    return true;
	}

	// Call to update the share intent
	private void setShareIntent(Intent shareIntent) {
	    if (mShareActionProvider != null) {
	        mShareActionProvider.setShareIntent(shareIntent);
	    }
	}
	
	private Intent getDefaultShareIntent() {  
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
		sendIntent.setType("text/plain");
		return sendIntent;  
		}  
}
