package cz.witzany.gamev2.graphics.impl;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.graphics.Mutator;
import cz.witzany.gamev2.graphics.Node;
import cz.witzany.gamev2.graphics.shaders.DepthImage;
import cz.witzany.gamev2.graphics.shaders.Shader;
import cz.witzany.gamev2.graphics.shaders.ShaderLoader;
import cz.witzany.gamev2.graphics.shaders.SimpleImage;
import cz.witzany.gamev2.graphics.utils.AnimTemplate;
import cz.witzany.gamev2.graphics.utils.FBO;
import cz.witzany.gamev2.gui.EventHandler;
import cz.witzany.gamev2.gui.GUI;

public class Game implements Runnable {

	private static Game instance;
	private long lastFrame;
	private int delta;
	private Node follow;
	private int width, height;
	public float night = 0.7f;
	private FBO mapFBO;
	private FBO lightingFBO;
	private Shader postProcess;
	private Node map;
	private Node lights;
	private boolean screenshot = false;

	private Game() {
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

	public void screenshot() {
		screenshot = true;
	}

	private void takeScreen() {
		GL11.glReadBuffer(GL11.GL_FRONT);
		int bpp = 4; // Assuming a 32-bit display with a byte each for red,
						// green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA,
				GL11.GL_UNSIGNED_BYTE, buffer);
		File file = new File("screenshot.png"); // The file to save to.
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				int i = (x + (width * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16)
						| (g << 8) | b);
			}

		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void tick() {
		float x = 0, y = 0;
		// setup viewport
		if (follow != null) {
			Vector3f pos = follow.getPosition();
			x = pos.x;
			y = pos.y;
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(-width / 2, width / 2, -height / 2, height / 2, 10, -10);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
		}

		glEnable(GL_DEPTH_TEST);
		glBindTexture(GL_TEXTURE_2D, 0);
		mapFBO.bind();

		glEnable(GL_TEXTURE_2D);
		glClearStencil(0);
		glClearDepth(0x0);
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
				| GL_STENCIL_BUFFER_BIT);

		glTranslatef(-x, -y, 0.0f);
		map.tick();
		glDisable(GL_DEPTH_TEST);

		glEnable(GL_BLEND);
		glBindTexture(GL_TEXTURE_2D, 0);
		lightingFBO.bind();

		glEnable(GL_TEXTURE_2D);
		glClearStencil(0);
		glClearDepth(0x0);
		glClearColor(1, 1, 1, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
				| GL_STENCIL_BUFFER_BIT);

		lights.tick();
		glDisable(GL_BLEND);

		glBindTexture(GL_TEXTURE_2D, 0);
		lightingFBO.unbind();
		glDisable(GL_TEXTURE_2D);

		glClearStencil(0);
		glClearDepth(0x0);
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
				| GL_STENCIL_BUFFER_BIT);

		glLoadIdentity();
		glTranslatef(-width / 2, height / 2, 0.0f);
		glScaled(width, -height, 1);

		postProcess.apply();
		postProcess.setTexture("colorMap", 0, mapFBO.getTexture());
		postProcess.setTexture("lightMap", 1, lightingFBO.getTexture());
		postProcess.setUniform("night", night);

		glColor3d(1, 0, 1);
		glBegin(GL11.GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2d(0, 0);
		glTexCoord2f(1, 0);
		glVertex2d(1, 0);
		glTexCoord2f(1, 1);
		glVertex2d(1, 1);
		glTexCoord2f(0, 1);
		glVertex2d(0, 1);
		glEnd();

		if (screenshot) {
			takeScreen();
			screenshot = false;
		}

		glFlush();
	}

	private void initGL() {
		try {
			setDisplayMode(1680, 1050, true);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		mapFBO = new FBO(width, height);
		lightingFBO = new FBO(width, height);
		postProcess = ShaderLoader.loadShader("Data/Shaders/Postprocess");

		glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
		glClearDepth(1.0f); // Depth Buffer Setup
		glShadeModel(GL_SMOOTH);

		glPushAttrib(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT
				| GL_STENCIL_BUFFER_BIT);
		glDepthRange(1.0f, 0.0f);

		glEnable(GL_STENCIL_TEST);
		glEnable(GL_DEPTH_TEST);

		glAlphaFunc(GL_GREATER, 0x00);
		glDepthFunc(GL_GREATER);
		glStencilFunc(GL_NEVER, 0x0, 0xFFFFFFFF);
		glBlendFunc(GL_ZERO, GL_SRC_COLOR);
	}

	private void addTorch(int x, int y) {
		try {
			SimpleAnim a = new AnimTemplate("Data/Templates/Torch.tml")
					.construct();
			a.setPosition(x, y, 0);
			map.addChild(a);
			SimpleAnim anim = new SimpleAnim();
			anim.bindPosition(a);
			anim.addFrame(new ShaderedImage<SimpleImage>(0, 0, 0, 2f,
					new SimpleImage("Data/Textures/Sprites/RadialLight")));
			anim.addFrame(new ShaderedImage<SimpleImage>(0, 0, 0, 1.95f,
					new SimpleImage("Data/Textures/Sprites/RadialLight")));
			anim.setSpeed(50);
			lights.addChild(anim);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initMap() {

		lights = new Node();
		map = new Node();

		ShaderedImage<SimpleImage> terrain = new ShaderedImage<SimpleImage>(0, 0, 9.99f, 1, new SimpleImage("Data/Textures/Sprites/TerrainSample"));
		ShaderedImage<DepthImage> house = new ShaderedImage<DepthImage>(0, 0, 0, 0.7f, new DepthImage("Data/Textures/Depthsprites/House",1));
		ShaderedImage<DepthImage> fireball = new ShaderedImage<DepthImage>(300, 200, 0, 0.3f, new DepthImage("Data/Textures/Depthsprites/Fireball",0.4f));
		ShaderedImage<SimpleImage> fireballLight = new ShaderedImage<SimpleImage>(300, 200, 0, 0.3f, new SimpleImage("Data/Textures/Sprites/FireballLight"));
		fireballLight.bindPosition(fireball);
		
		map.addChild(terrain);
		map.addChild(house);
		map.addChild(fireball);
		lights.addChild(fireballLight);
		
		fireball.addMutator(new Mutator<Node>() {
			@Override
			public void update(Node node, int diff) {
				Vector3f pos = node.getPosition();
				node.setPosition(pos.x-2, pos.y+2, pos.z);
			}
		});
		
		ShaderedImage<DepthImage> cube = new ShaderedImage<DepthImage>(300, 25, 0, 0.3f, new DepthImage("Data/Textures/Depthsprites/Kostka",0.4f));
		cube.addMutator(new Mutator<ShaderedImage<DepthImage>>() {
			private final float speed = 0.001f;
			private boolean spin = true;
			private float scale = 0.75f;
			private final float max = 1.25f;
			private final float min = 0.5f;
			
			@Override
			public void update(ShaderedImage<DepthImage> obj, int diff) {
				float add = speed * diff;
				if (!spin)
					add *= -1;
				scale += add;
				if (scale > max) {
					scale = max;
					spin = !spin;
				} else if (scale < min) {
					scale = min;
					spin = !spin;
				}
				obj.setScale(scale);
			}
		});
		map.addChild(cube);
		
		addTorch(300, 100);
		addTorch(-300, 100);
		addTorch(0, 200);

		try {
			AnimTemplate tml = new AnimTemplate("Data/Templates/Panak.tml");
			follow = tml.construct();
			map.addChild(follow);
			GUI.getInstance().setControlled(follow);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		map.addChild(GUI.getInstance());
	}

	@Override
	public void run() {
		initGL();
		initMap();

		int frames = 0;
		long total = 0;
		lastFrame = (Sys.getTime() * 1000) / Sys.getTimerResolution();
		while (!Display.isCloseRequested()) {
			long time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
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
			Display.sync(60);
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
