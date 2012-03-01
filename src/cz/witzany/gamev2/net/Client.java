package cz.witzany.gamev2.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import cz.witzany.gamev2.graphics.impl.Game;

public class Client implements Runnable{

	private Client(){}
	
	private static Client instance;
	private Socket socket = null;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	
	public void connect(String adress, int port) throws UnknownHostException, IOException{
		socket = new Socket(adress,port);
		//#TODO custom buffered streams for packet bursts
		in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		System.out.println("Connected to server");
	}

	@Override
	public void run() {
		if(socket == null)
			return;
		Thread game = new Thread(Game.getInstance());
		game.start();
		while(true){
			try {
				Message m = Message.readMessage(in);
				System.out.println("Message recieved "+m);
				Game.getInstance().message(m, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Client getInstance(){
		if(instance == null)
			instance = new Client();
		return instance;
	}
}
