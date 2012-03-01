package cz.witzany.gamev2.graphics.impl;

import java.io.IOException;


import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.graphics.model.Node;
import cz.witzany.gamev2.graphics.model.PosNode;
import cz.witzany.gamev2.graphics.utils.AnimTemplate;
import cz.witzany.gamev2.graphics.utils.FBO;
import cz.witzany.gamev2.graphics.utils.Shader;
import cz.witzany.gamev2.graphics.utils.ShaderLoader;
import cz.witzany.gamev2.gui.EventHandler;
import cz.witzany.gamev2.gui.GUI;
import cz.witzany.gamev2.net.Message;
import cz.witzany.gamev2.net.utils.ByteUtils;
import cz.witzany.gamev2.net.utils.Type;

import static org.lwjgl.opengl.GL11.*;

public class Game implements Runnable {

	private static Game instance;
	private long lastFrame;
	private int delta;
	private PosNode follow;
	private int width, height;
	public float night = 1f;
	private FBO mapFBO;
	private FBO lightingFBO;
	private Shader postProcess;
	private Node map;
	private Node lights;

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

	public void tick() {
		//setup viewport
		if (follow != null) {
			Vector3f pos = follow.getPosition();
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(- width/2, width/2, -height
					/ 2, height / 2, 10, -10);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
		}
		
		glBindTexture(GL_TEXTURE_2D, 0);
		mapFBO.bind();

		glEnable(GL_TEXTURE_2D);
		glClearStencil(0);
		glClearDepth(0x0);
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
				| GL_STENCIL_BUFFER_BIT);

		glTranslatef (-width/2, -height/2, 0.0f);
		glScaled(width, height, 1);
		glPushMatrix();
		map.tick();
		
		glBindTexture(GL_TEXTURE_2D, 0);
		lightingFBO.bind();

		glEnable(GL_TEXTURE_2D);
		glClearStencil(0);
		glClearDepth(0x0);
		glClearColor(1, 1, 1, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
				| GL_STENCIL_BUFFER_BIT);
		
		lights.tick();

		glBindTexture(GL_TEXTURE_2D, 0);
		lightingFBO.unbind();
		glDisable(GL_TEXTURE_2D);
		
		glClearStencil(0);
		glClearDepth(0x0);
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
				| GL_STENCIL_BUFFER_BIT);

		glLoadIdentity ();
		glTranslatef (-width/2, height/2, 0.0f);
		glScaled(width, -height, 1);

		postProcess.apply();
		postProcess.setTexture("colorMap", 0,  mapFBO.getTexture());
		postProcess.setTexture("lightMap", 1,  lightingFBO.getTexture());

		glColor3d(1, 0, 1);
		glBegin(GL11.GL_QUADS);
		glTexCoord2f(0, 0); glVertex2d(0, 0);
		glTexCoord2f(1, 0); glVertex2d(1, 0);
		glTexCoord2f(1, 1); glVertex2d(1, 1);
		glTexCoord2f(0, 1); glVertex2d(0, 1);
		glEnd();

		glFlush ();		
	}
	
	private void initGL(){
		try {
			setDisplayMode(1680, 1050, true);
			Display.create();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mapFBO = new FBO(width, height);
		lightingFBO = new FBO(width, height);
		postProcess = ShaderLoader.loadShader("Data/Shaders/Postprocess"); 

		glClearColor (0.0f, 0.0f, 0.0f, 0.5f);						// Black Background
		glClearDepth (0.0f);										// Depth Buffer Setup
		glShadeModel (GL_SMOOTH);									// Select Smooth Shading
		glHint (GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);			// Set Perspective Calculations To Most Accurate
		glShadeModel(GL_SMOOTH);
		
		glPushAttrib(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT
				| GL_STENCIL_BUFFER_BIT);
		glDepthRange(1.0f, 0.0f);

		glEnable(GL_STENCIL_TEST);
		glEnable(GL_DEPTH_TEST);

		glAlphaFunc(GL_GREATER, 0x00);
		glDepthFunc(GL_GREATER);
		glStencilFunc(GL_NEVER, 0x0, 0xFFFFFFFF);
	}
	
	private void initMap(){
		
		lights = new Node(1);
		map = new Node(1);
		
		map.addChild(new Image(2, 0, 0, 9.999f, 1,
				"Data/Textures/Sprites/TerrainSample"));
		map.addChild(new DepthSprite(5, 0, 0, 0.7,
				"Data/Textures/Depthsprites/House", 1f));
		map.addChild(new FunAnim(6, 300, 25, 0.3,
				"Data/Textures/Depthsprites/Kostka", 0.4f));
		
		try {
			AnimTemplate tml = new AnimTemplate("Data/Templates/Panak.tml");
			follow = tml.construct(7);
			map.addChild(follow);
			Image playerLight = new Image(0, 0, 0, 0, 3f, "Data/Textures/Sprites/RadialLight");
			playerLight.bindPosition(follow);
			lights.addChild(playerLight);
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
