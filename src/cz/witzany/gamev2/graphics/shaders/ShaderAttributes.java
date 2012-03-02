package cz.witzany.gamev2.graphics.shaders;


public abstract class ShaderAttributes {

	protected Shader shader;
	protected float width, height;
	
	public ShaderAttributes(Shader shader){
		this.shader = shader;
	}
	
	public void setDimensions(float width, float height){
		this.width = width;
		this.height = height;
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public void bind(){
		shader.apply();
	}
	
	public Shader getShader(){
		return shader;
	}
}
