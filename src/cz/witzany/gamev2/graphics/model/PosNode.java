package cz.witzany.gamev2.graphics.model;

import org.lwjgl.util.vector.Vector3f;

public class PosNode extends Node{

	private Vector3f position;
	
	public PosNode(int guid) {
		super(guid);
		position = new Vector3f();
	}
	
	public void setPosition(float x, float y, float z){
		position.set(x, y, z);
	}
	
	public Vector3f getPosition(){
		return position;
	}

}