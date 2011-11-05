package cz.witzany.gamev2.graphics;

import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import cz.witzany.gamev2.net.Message;
import cz.witzany.gamev2.utils.ByteUtils;
import cz.witzany.gamev2.utils.Type;

public class Game extends Node implements Runnable {

	private static Game instance;
	private long lastFrame;
	private int delta;

	private Game() {
		super(1);
	}

	public static int getDelta() {
		return getInstance().delta;
	}

	public static Game getInstance() {
		if (instance == null)
			instance = new Game();
		return instance;
	}

	@Override
	public void update() {
		GL11.glClearStencil(0);
		GL11.glClearDepth(0xFF);
		GL11.glClearColor(0,0,0,0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glDepthRange(0.0f, 1.0f);
		
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		GL11.glAlphaFunc(GL11.GL_GREATER, 0x00);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glStencilFunc(GL11.GL_NEVER, 0x0, 0xFFFFFFFF);
	}

	@Override
	public void messageRecieved(Message msg) {
		byte[] data = msg.getData();
		switch (data[0]) {
		case 1: // cmd add child
			int type = ByteUtils.readInt(data, 1);
			Object node = Type.createInstance(type, ByteUtils.readInt(data, 5));
			addChild((Node) node);
			break;
		}
	}

	@Override
	public void run() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.create();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addChild(new Image(2,100,100,9.9999f,5,"Data/Textures/Sprites/Creeper"));
		Random r = new Random();
		for(int i = 0; i < 2000; i++){
			addChild(new DepthSprite(3+i, r.nextInt(800), r.nextInt(600), 0.1, "Data/Textures/Depthsprites/Kostka", 1.0f));
		}  

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 600, 0, 10, -10);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		int frames = 0;
		long total = 0;
		lastFrame = (Sys.getTime() * 1000) / Sys.getTimerResolution();
		while (!Display.isCloseRequested()) {
			long time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
			if(time - lastFrame < 16){
				try {
					Thread.sleep(16-time+lastFrame);
					time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			delta = (int) (time-lastFrame);
			frames++;
			total+=delta;
			if(total > 1000){
				System.out.println(frames);
				frames = 0;
				total-=1000;
			}
			//System.out.println(delta);
			tick();
			lastFrame = time;
			Display.update();
		}
		Display.destroy();
		System.exit(0);
	}
}
