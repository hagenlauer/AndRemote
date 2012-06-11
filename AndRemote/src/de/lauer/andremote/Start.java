package de.lauer.andremote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Start extends Activity{

	EditText ip;
	EditText whatever;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		ip = (EditText)findViewById(R.id.editText1);
		whatever = (EditText)findViewById(R.id.editText2);
	}

	public void connect(View view){
		if(!isConnected(this)){
			Toast.makeText(this, "You might want to turn on and connect the WiFi in order to use the WiFi...", Toast.LENGTH_SHORT).show();
			return;
		}
		String addr = ip.getText().toString(); //i wont check anything here.
		String pass = whatever.getText().toString();
		Intent intent = new Intent(this,AndRemoteActivity.class);
		intent.putExtra("addr", addr);
		intent.putExtra("pass", pass);
		startActivityForResult(intent, 42);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 42){
			switch (resultCode) {
			case Activity.RESULT_OK:
				//everything is fine
				break;
			case 10:
				Toast.makeText(this, "That didn't work so well, make sure you used the correct " +
						"IP and target device runs the Reciever", Toast.LENGTH_SHORT).show();
			default:
				break;
			}
		}
	}
	private static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
			networkInfo =
					connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		}
		return networkInfo == null ? false : networkInfo.isConnected();
	}


}
