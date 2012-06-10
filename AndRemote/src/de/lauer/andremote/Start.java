package de.lauer.andremote;

import android.app.Activity;
import android.content.Intent;
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
		String addr = ip.getText().toString(); //i wont check anything here.
		String pass = whatever.getText().toString();
		Intent intent = new Intent(this,AndRemoteActivity.class);
		intent.putExtra("addr", addr);
		intent.putExtra("pass", pass);
		startActivityForResult(intent, 42);
//		Toast.makeText(this, "works", Toast.LENGTH_SHORT).show();
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 42){
			switch (resultCode) {
			case Activity.RESULT_OK:
					//everything is fine
				break;
			case 10:
				Toast.makeText(this, "That didn't work so well, make sure u used the correct " +
						"IP and target device runs the Reciever", Toast.LENGTH_SHORT).show();
			default:
				break;
			}
		}
	}


}
