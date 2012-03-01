package cz.witzany.gamev2.graphics.utils;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_RENDERBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindRenderbufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferRenderbufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenRenderbuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glRenderbufferStorageEXT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;

import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;

public class FBO {

	private int FBOId;
	private int textureId;
	private int depthId;
	private int width,height;
	
	public FBO(int width, int height){
		this.width = width;
		this.height = height;
		if(!GLContext.getCapabilities().GL_EXT_framebuffer_object)
			throw new RuntimeException("GPU doesnt support framebuffer objects");
		
		FBOId = glGenFramebuffersEXT();
		textureId = glGenTextures();
		depthId = glGenRenderbuffersEXT();
		
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBOId);
		
		// initialize color texture
		glBindTexture(GL_TEXTURE_2D, textureId);                                   // Bind the colorbuffer texture
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);               // make it linear filterd
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0,GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);  // Create the texture data
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, textureId, 0); // attach it to the framebuffer
		 
		 
		// initialize depth renderbuffer
		glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthId);                // bind the depth renderbuffer
		glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, width, height); // get the data space for it
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthId); // bind it to the renderbuffer
		 
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);                                    // Swithch back to normal framebuffer rendering
	}
	
	public int getTexture(){
		return textureId;
	}
	
	public void bind(){
		glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, FBOId);
	}
	
	public void unbind(){
		glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, 0);
	}
}
