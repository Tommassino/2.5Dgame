package cz.witzany.gamev2.graphics.impl;

/********************************************************
*														*
*    Overlords Tutorial - 01							*
*     http://www.flashbang.se							*
*             2006                  					*
*                                   					*
*    ported by kappaOne 								*
*    from http://www.flashbang.se/archives/48			*                           
*********************************************************/

import java.awt.Rectangle;
import java.io.File;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;

import cz.witzany.gamev2.graphics.model.Node;
import cz.witzany.gamev2.graphics.utils.FBO;
import cz.witzany.gamev2.graphics.utils.ImageLoader;
import cz.witzany.gamev2.graphics.utils.Shader;
import cz.witzany.gamev2.graphics.utils.ShaderLoader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
 
public class FBOExample {
 
	float angle;
	
	int colorTextureID;
	int framebufferID;
	int depthRenderBufferID;
 
	/** time at last frame */
	long lastFrame;
 
	/** frames per second */
	int fps;
	/** last fps time */
	long lastFPS;
	
	/** is VSync Enabled */
	boolean vsyncEnabled;
 
	public void start() {
		try {
			Display.setDisplayMode(new DisplayMode(512, 512));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
 
		initGL(); // init OpenGL
		getDelta(); // call once before loop to initialise lastFrame
		lastFPS = getTime(); // call before loop to initialise fps timer
 
		while (!Display.isCloseRequested()) {
			int delta = getDelta();
 
			update(delta);
			renderGL();
 
			Display.update();
			Display.sync(60); // cap fps to 60fps
		}
 
		Display.destroy();
	}
 
	public void update(int delta) {
		// rotate box
		angle += 0.15f * delta;
 
		while (Keyboard.next()) {
		    if (Keyboard.getEventKeyState()) {
		        if (Keyboard.getEventKey() == Keyboard.KEY_F) {
		        	setDisplayMode(800, 600, !Display.isFullscreen());
		        }
		        else if (Keyboard.getEventKey() == Keyboard.KEY_V) {
		        	vsyncEnabled = !vsyncEnabled;
		        	Display.setVSyncEnabled(vsyncEnabled);
		        }
		    }
		}
 
		updateFPS(); // update FPS Counter
	}
	
	Shader secondPass;
	FBO mapFBO;
	FBO lightingFBO;
	Node map;
	Node lighting;
 
	public void initGL() {
		map = new Node(0);
		lighting = new Node(1);
		map.addChild(new Image(0, -1, -1, 0.999f, 0.03f, "Data/Textures/Sprites/Creeper"));
		//map.addChild(new Image(1, 0, -1, -0.1f, 0.015f, "Data/Textures/Sprites/Creeper"));
		lighting.addChild(new Image(0, -1, -1, 0, 0.01f, "Data/Textures/Sprites/RadialLight"));
		map.addChild(new DepthSprite(6, 0, 0, 0.002f,
				"Data/Textures/Depthsprites/Kostka",0.4f));
		map.addChild(new DepthSprite(7, 0, 0, 0.004f,
				"Data/Textures/Depthsprites/Kostka",0.4f));
		
		secondPass = ShaderLoader.loadShader("Data/Shaders/Postprocess"); 
		
		glViewport (0, 0, 512, 512);								// Reset The Current Viewport
		glMatrixMode (GL_PROJECTION);								// Select The Projection Matrix
		glLoadIdentity ();											// Reset The Projection Matrix
		glOrtho(-256, -256, 256, 256, 10, -10);
		glMatrixMode (GL_MODELVIEW);								// Select The Modelview Matrix
		glLoadIdentity ();											// Reset The Modelview Matrix
		
		// Start Of User Initialization
		angle		= 0.0f;											// Set Starting Angle To Zero

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
		
		mapFBO = new FBO(512,512);
		lightingFBO = new FBO(512,512);
	}
 
	public void renderGL() {
		
		// FBO render pass============

		glViewport (0, 0, 512, 512);									// set The Current Viewport to the fbo size

		glBindTexture(GL_TEXTURE_2D, 0);								// unlink textures because if we dont it all is gonna fail
		mapFBO.bind();
		//glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);		// switch to rendering on our FBO

		glClearStencil(0);
		glClearDepth(0x0);
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
				| GL_STENCIL_BUFFER_BIT);

		glEnable(GL_TEXTURE_2D);
		map.tick();
		glDisable(GL_TEXTURE_2D);

		// lighting pass==============

		glViewport (0, 0, 512, 512);									// set The Current Viewport to the fbo size

		glBindTexture(GL_TEXTURE_2D, 0);								// unlink textures because if we dont it all is gonna fail
		lightingFBO.bind();

		glClearColor (1.0f, 1.0f, 1.0f, 1.0f);
		glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);			// Clear Screen And Depth Buffer on the fbo to red

		glEnable(GL_TEXTURE_2D);
		lighting.tick();
		glDisable(GL_TEXTURE_2D);

		// Normal render pass, draw cube with texture

		lightingFBO.unbind();

		glClearColor (0.0f, 1.0f, 0.0f, 0.5f);
		glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);			// Clear Screen And Depth Buffer on the framebuffer to black

		glViewport (0, 0, 512, 512);									// set The Current Viewport
		
		glLoadIdentity ();
		glTranslatef (-1, -1, 0.0f);
		glScaled(2, 2, 1);
		
		secondPass.apply();
		secondPass.setTexture("colorMap", 0,  mapFBO.getTexture());
		secondPass.setTexture("lightMap", 1,  lightingFBO.getTexture());
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0); GL11.glVertex2d(0, 0);
		GL11.glTexCoord2f(1, 0); GL11.glVertex2d(1, 0);
		GL11.glTexCoord2f(1, 1); GL11.glVertex2d(1, 1);
		GL11.glTexCoord2f(0, 1); GL11.glVertex2d(0, 1);
		GL11.glEnd();

		glFlush ();		
	}
	
	public void drawBox() { 
		// this func just draws a perfectly normal box with some texture coordinates
		glBegin(GL_QUADS);
			// Front Face
			glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
			glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
			glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f,  1.0f,  1.0f);	// Top Right Of The Texture and Quad
			glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f,  1.0f,  1.0f);	// Top Left Of The Texture and Quad
			// Back Face
			glTexCoord2f(1.0f, 0.0f); glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Right Of The Texture and Quad
			glTexCoord2f(1.0f, 1.0f); glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Right Of The Texture and Quad
			glTexCoord2f(0.0f, 1.0f); glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Left Of The Texture and Quad
			glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Left Of The Texture and Quad
			// Top Face
			glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left Of The Texture and Quad
			glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f,  1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
			glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f,  1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
			glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right Of The Texture and Quad
			// Bottom Face
			glTexCoord2f(1.0f, 1.0f); glVertex3f(-1.0f, -1.0f, -1.0f);	// Top Right Of The Texture and Quad
			glTexCoord2f(0.0f, 1.0f); glVertex3f( 1.0f, -1.0f, -1.0f);	// Top Left Of The Texture and Quad
			glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
			glTexCoord2f(1.0f, 0.0f); glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
			// Right face
			glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Right Of The Texture and Quad
			glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right Of The Texture and Quad
			glTexCoord2f(0.0f, 1.0f); glVertex3f( 1.0f,  1.0f,  1.0f);	// Top Left Of The Texture and Quad
			glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
			// Left Face
			glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Left Of The Texture and Quad
			glTexCoord2f(1.0f, 0.0f); glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
			glTexCoord2f(1.0f, 1.0f); glVertex3f(-1.0f,  1.0f,  1.0f);	// Top Right Of The Texture and Quad
			glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left Of The Texture and Quad
		glEnd();
	}
	
	/**
	 * Set the display mode to be used 
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public void setDisplayMode(int width, int height, boolean fullscreen) {

		// return if requested DisplayMode is already set
                if ((Display.getDisplayMode().getWidth() == width) && 
			(Display.getDisplayMode().getHeight() == height) && 
			(Display.isFullscreen() == fullscreen)) {
			return;
		}
		
		try {
			DisplayMode targetDisplayMode = null;
			
			if (fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;
				
				for (int i=0;i<modes.length;i++) {
					DisplayMode current = modes[i];
					
					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against the 
						// original display mode then it's probably best to go for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
						    (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width,height);
			}
			
			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);
			
		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
		}
	}
	
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
 
	    return delta;
	}
 
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
 
	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
 
	public static void main(String[] argv) {
		FBOExample fboExample = new FBOExample();
		fboExample.start();
	}
}
