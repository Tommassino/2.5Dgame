package cz.witzany.gamev2.graphics.impl;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.graphics.model.Node;
import cz.witzany.gamev2.graphics.model.PosNode;
import cz.witzany.gamev2.graphics.utils.AnimTemplate;
import cz.witzany.gamev2.gui.EventHandler;
import cz.witzany.gamev2.gui.GUI;
import cz.witzany.gamev2.net.Message;
import cz.witzany.gamev2.net.utils.ByteUtils;
import cz.witzany.gamev2.net.utils.Type;

public class Game extends Node implements Runnable {

	private static Game instance;
	private long lastFrame;
	private int delta;
	private PosNode follow;
	private int width, height;
	public float night = 1f;

	private Game() {
		super(1);
		follow = null;
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
		if (follow != null) {
			Vector3f pos = follow.getPosition();
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(pos.x - width / 2, pos.x + width / 2, pos.y + height
					/ 2, pos.y - height / 2, 10, -10);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
		}

		GL11.glClearStencil(0);
		GL11.glClearDepth(0x0);
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT
				| GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT
				| GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glDepthRange(1.0f, 0.0f);

		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		GL11.glAlphaFunc(GL11.GL_GREATER, 0x00);
		GL11.glDepthFunc(GL11.GL_GREATER);
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
			setDisplayMode(1680, 1050, true);
			Display.create();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addChild(new Image(2, 0, 0, 9.999f, 1,
				"Data/Textures/Sprites/TerrainSample"));
		addChild(new DepthSprite(5, 1000, 325, 0.7,
				"Data/Textures/Depthsprites/House", 1f));
		addChild(new FunAnim(6, 840, 525, 0.3,
				"Data/Textures/Depthsprites/Kostka", 0.4f));
		try {
			AnimTemplate tml = new AnimTemplate("Data/Templates/Panak.tml");
			follow = tml.construct(7);
			addChild(follow);
			GUI.getInstance().setControlled(follow);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		addChild(GUI.getInstance());

		GL11.glShadeModel(GL11.GL_SMOOTH);

		int frames = 0;
		long total = 0;
		lastFrame = (Sys.getTime() * 1000) / Sys.getTimerResolution();
		while (!Display.isCloseRequested()) {
			long time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
			if (time - lastFrame < 16) {
				try {
					Thread.sleep(16 - time + lastFrame);
					time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			delta = (int) (time - lastFrame);
			frames++;
			total += delta;
			if (total > 1000) {
				System.out.println(frames);
				frames = 0;
				total -= 1000;
			}
			// System.out.println(delta);
			EventHandler.poll();
			tick();
			lastFrame = time;
			Display.update();
		}
		Display.destroy();
		System.exit(0);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Set the display mode to be used
	 * 
	 * @param width
	 *            The width of the display required
	 * @param height
	 *            The height of the display required
	 * @param fullscreen
	 *            True if we want fullscreen mode
	 */
	public void setDisplayMode(int width, int height, boolean fullscreen) {

		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width)
				&& (Display.getDisplayMode().getHeight() == height)
				&& (Display.isFullscreen() == fullscreen)) {
			return;
		}

		try {
			DisplayMode targetDisplayMode = null;

			if (fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				for (int i = 0; i < modes.length; i++) {
					DisplayMode current = modes[i];

					if ((current.getWidth() == width)
							&& (current.getHeight() == height)) {
						if ((targetDisplayMode == null)
								|| (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null)
									|| (current.getBitsPerPixel() > targetDisplayMode
											.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against
						// the
						// original display mode then it's probably best to go
						// for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display
								.getDesktopDisplayMode().getBitsPerPixel())
								&& (current.getFrequency() == Display
										.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: " + width + "x"
						+ height + " fs=" + fullscreen);
				return;
			}

			this.width = targetDisplayMode.getWidth();
			this.height = targetDisplayMode.getHeight();
			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);
			Display.setVSyncEnabled(fullscreen);

		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode " + width + "x" + height
					+ " fullscreen=" + fullscreen + e);
		}
	}
}
