package cz.witzany.gamev2;

import java.io.File;
import java.io.IOException;

import cz.witzany.gamev2.graphics.utils.ImageLoader;
import cz.witzany.gamev2.net.Client;
import cz.witzany.gamev2.net.Server;

public class Main {

	public static void main(String args[]) {
		//ImageLoader.gen(new File("Data/Textures/Depthsprites"));
		Server server = new Server();
		Client client = Client.getInstance();
		try {
			server.listen(6666);
			Thread st = new Thread(server);
			st.start();
			client.connect("localhost", 6666);
			Thread ct = new Thread(client);
			ct.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
