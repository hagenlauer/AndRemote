package de.lauer.andremote;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class EnterText extends Activity{
	
	EditText text;
	
	float oldx = 0, oldy = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.text);
		text = (EditText)findViewById(R.id.textEntry);
		
	}
	public void sendText(View view){
		String content = "text:";
		content += text.getText().toString();
		try {
			Single.getInstance().getCon().sendCommand(content);
		} catch (Exception e) {
		}
		finish();
	}
	
	public void sendDel(View view){
		try {
			Single.getInstance().getCon().sendCommand("delkey");
		} catch (Exception e) {
		}
	}
	public void sendBack(View view){
		try {
			Single.getInstance().getCon().sendCommand("backspace");
		} catch (Exception e) {
		}
	}
	public void sendEnter(View view){
		try {
			Single.getInstance().getCon().sendCommand("enter");
		} catch (Exception e) {
		}
	}
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
				Single.getInstance().getCon().sendCommand((int)resultx+":"+(int)resulty);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("EnterText", "", e);
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

	
}
