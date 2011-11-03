package cz.witzany.gamev2.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

	/** The server socket. */
	private ServerSocket serverSocket;

	public void listen(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("Now listening on port " + port);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				ClientHandler handler = new ClientHandler(clientSocket);
				Thread t = new Thread(handler);
				t.start();
			} catch (IOException e) {
				System.out.println("Client connection failed");
			}
		}
	}

	public class ClientHandler implements Runnable {

		private Socket clientSocket;
		private DataOutputStream out;
		private DataInputStream in;

		public ClientHandler(Socket clientSocket) throws IOException {
			this.clientSocket = clientSocket;
			out = new DataOutputStream(clientSocket.getOutputStream());
			in = new DataInputStream(clientSocket.getInputStream());
			System.out.println("Server accepted client "
					+ clientSocket.getInetAddress().getHostAddress());
		}

		@Override
		public void run() {
			int phase = 0;
			int triangleGuid = 15;
			while (clientSocket.isConnected()) {
				/*
				 * try { switch (phase) { case 0: { byte[] data = new byte[9];
				 * data[0] = 1; ByteUtils.writeInt(Type.TRIANGLE.id, data, 1);
				 * ByteUtils.writeInt(triangleGuid, data, 5); Message
				 * addTriangle = Message.buildMessage("", data);
				 * addTriangle.writeMessage(out); break; } case 1: { byte[] data
				 * = new byte[1 + 4 * 6]; data[0]=Triangle.MSG_SET_POS;
				 * ByteUtils.writeInt(100, data, 1); ByteUtils.writeInt(100,
				 * data, 5); ByteUtils.writeInt(100, data, 9);
				 * ByteUtils.writeInt(200, data, 13); ByteUtils.writeInt(200,
				 * data, 17); ByteUtils.writeInt(200, data, 21); Message
				 * setPosition = Message.buildMessage(triangleGuid+".", data);
				 * setPosition.writeMessage(out); } } phase++; } catch
				 * (IOException e) { e.printStackTrace(); }
				 */
			}
		}
	}

}
