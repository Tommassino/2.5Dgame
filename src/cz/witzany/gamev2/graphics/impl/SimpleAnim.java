package cz.witzany.gamev2.graphics.impl;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.graphics.model.TimerNode;

public class SimpleAnim extends TimerNode {

	private ArrayList<DepthSprite> frames;
	
	public SimpleAnim(int guid) {
		super(guid);
		frames = new ArrayList<DepthSprite>();
	}
	
	public void addFrame(DepthSprite frame){
		frames.add(frame);
	}

	private final int next = 200;
	private int n = next;
	private int curr = 0;
	private int moved = 0;
	@Override
	public void update(long diff) {
		if(frames.size()==0)
			return;
		
		if(moved>0){
			if(n < diff){
				curr++;
				if(curr >= frames.size())
					curr = 0;
				n=next;
			}else n-= diff;
		}
		moved -= diff;
		
		DepthSprite sprite = frames.get(curr);
		Vector3f position = getPosition();
		sprite.setPosition(position.x, position.y, position.z);
		sprite.update();
	}

	public void setPosition(float x, float y, float z){
		super.setPosition(x, y, z);
		moved = 200;
	}
}
