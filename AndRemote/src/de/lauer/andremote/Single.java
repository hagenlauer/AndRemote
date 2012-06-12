package de.lauer.andremote;

public class Single{

	private static Single single = null;
	private MyConnection connection = null;

	private Single(){
		super();
	}
	public static Single getInstance(){
		if(single!=null)return single;
		single = new Single();
		single.connection = null;
		return single;
	}
	public MyConnection getCon(){
		return connection;
	}
	public void nullIt(){
		single.connection = null;
		connection = null;
	}
	public  void setCon(MyConnection c){
		connection = c;
	}
}

