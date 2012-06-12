package de.lauer.andremote;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;


public class AndRemoteActivity extends Activity {

	private final String tag = this.getClass().getSimpleName();

	MyConnection con = null;
	Button leftClick; 
	Button rightClick;
	Button arrowLeft;
	Button arrowRight;

	TextView tv;

	Context ctx;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Log.i(tag, "onCreate");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		String addr = getIntent().getStringExtra("addr");
		String pass = getIntent().getStringExtra("pass");

		ctx = this;

		leftClick = (Button) findViewById(R.id.button1);
		rightClick = (Button) findViewById(R.id.button2);
		arrowLeft = (Button) findViewById(R.id.button3);
		arrowRight = (Button) findViewById(R.id.button4);

		tv = (TextView) findViewById(R.id.textview);

		Single.getInstance(); //create a new Object if there is no old
		con = Single.getInstance().getCon();
		try {
			if(con == null){
				//Toast.makeText(this, "new Connection", Toast.LENGTH_SHORT).show();
				con = new MyConnection(this,addr, 3005);
				con.sendCommand("ohi");
				Single.getInstance().setCon(con);
				if(pass.length()>0)con.sendCommand("pass:"+pass);
			}else{
				//Toast.makeText(this, "old Connection", Toast.LENGTH_SHORT).show();
				//stop the task
				CloseCon.getCloseCon().cancel();
				CloseCon.getCloseCon().nullIt();
				//connection is there
				//con.sendCommand("imback");
			}
		} catch (Exception e) {
			e.printStackTrace();
			setResult(10);
			finish();
		}
		//Single.getInstance().setCon(null);
		if(Single.getInstance().getCon() == null){
			Toast.makeText(this, "singe con != null", Toast.LENGTH_SHORT).show();
		}


		arrowLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv.setText("arrowLeft");
				try {
					con.sendCommand("arrowleft");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		arrowRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv.setText("arrowRight");
				try {
					con.sendCommand("arrowright");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		leftClick.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//tv.setText("eventLeft");
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					tv.setText("left");
					try {
						con.sendCommand("left");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					tv.setText("leftup");
					try {
						con.sendCommand("leftup");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
				return false;
			}
		});


		rightClick.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//tv.setText("eventRight");
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					tv.setText("right");
					try {
						con.sendCommand("right");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					tv.setText("rightup");
					try {
						con.sendCommand("rightup");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		Timer t = new Timer();
		t.schedule(CloseCon.getCloseCon(), 500);
		Log.i(tag, "task sceduled");
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
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	private boolean flick = false;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.send_alt_tabk:
			try {
				con.sendCommand("alt_tab");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.showkeyboard:
			i = new Intent(this, EnterText.class);
			startActivity(i);
			break;
		case R.id.goto4chan:
			i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.4chan.org/b/"));
			startActivity(i);
			break;
		case R.id.leftdown:
			try{
				if(flick){
					con.sendCommand("left");
					flick = !flick;
				}else{
					con.sendCommand("leftup");
					flick = !flick;
				}
			}catch (Exception e) {
			}
		}
		return super.onOptionsItemSelected(item);
	}


}

/*
 *	never ever touch this again, it works, stay cool.
 *	increase this if you failed to correct: 1
 */

class CloseCon extends TimerTask{

	private static CloseCon task = null;

	private CloseCon(){

	}
	public static CloseCon getCloseCon(){
		if(task != null)return task;
		task = new CloseCon();
		return task;
	}
	public static void nullIt(){
		task = null;
	}

	@Override
	public void run() {
		try {
			Log.i("Task", "run");
			//Single.getInstance().getCon().sendCommand("kthxbye");
			MyConnection c = Single.getInstance().getCon();
			if(c != null){

				try {
					c.sendCommand("kthxbye");
					c.close();
				} catch (Exception e) {
					//this exception WILL occur since im sending the close to the computer first
				}
				Single.getInstance().setCon(null);
			}
			CloseCon.getCloseCon().nullIt();
		} catch (Exception e) {
			Log.e("Task", "", e);
		}
	}

}
