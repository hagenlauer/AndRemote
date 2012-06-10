package de.lauer.andremote;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AndRemoteActivity extends Activity {
	
	private final String tag = this.getClass().getSimpleName();
	
	MyConnection con = null;
	Button leftClick; 
	Button rightClick;

	TextView tv;

	Context ctx;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ctx = this;
		leftClick = (Button) findViewById(R.id.button1);
		rightClick = (Button) findViewById(R.id.button2);
		tv = (TextView) findViewById(R.id.textview);
		
		
		try {
			if(con == null){
				con = new MyConnection(this,"192.168.2.41", 3005);
				//con.sendCommand("ohi");
			}else{
				con.sendCommand("imback");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		leftClick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv.setText("leftClick");
				try {
					con.sendCommand("left");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		rightClick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv.setText("rightClick");
				try {
					con.sendCommand("right");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		

	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		try {
			con.sendCommand("kthxbye");
			con.close();
			con = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	float oldx = 0, oldy = 0;
	

	@Override
	public boolean onTouchEvent(MotionEvent event){
		float newx = 0, newy = 0;
		double resultx = 0, resulty = 0;
		String out = "";

		switch(event.getAction()){
		case(MotionEvent.ACTION_MOVE):
			newx = event.getX();
			newy = event.getY();

		if(Math.sqrt(Math.pow((oldx-newx), 2) + Math.pow((oldy-newy),2)) > 2){
			
			resultx = newx - oldx;
			resulty = newy - oldy;
			
			oldx = newx;// - oldx;
			
			oldy = newy;// - oldy;
			
			//Log.i(tag, (int)resultx + " : "+(int)resulty);
			try {
				con.sendCommand((int)resultx+":"+(int)resulty);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e(tag, "", e);
			}
			
		}else{
			tv.setText("movement to small");
		}

		

		//tv.setText(oldx+" : "+oldx);
		break;
		
		case(MotionEvent.ACTION_DOWN):
			oldx = event.getX();
			oldy = event.getY();
			break;
		case(MotionEvent.ACTION_UP):
			break;
		default:
			break;
		}

		return true;
	}


}