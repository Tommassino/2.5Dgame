package cz.witzany.gamev2.graphics.model;

import java.util.HashMap;
import java.util.logging.Logger;

import cz.witzany.gamev2.net.Message;

/**
 * Class representing a node in the graphics tree. Each node represents a possibly drawable/updatable part of the screen.
 * @author tommassino
 *
 */
public class Node {

	private int guid;
	private int level;
	private Node parent;
	private HashMap<Integer,Node> children;
	
	public Node(int guid){
		this.guid = guid;
		this.level = -1;
		this.parent = null;
		children = new HashMap<Integer, Node>();
	}
	
	public final void tick(){
		update();
		for(Node n : children.values())
			n.tick();
		postUpdate();
	}
	
	public void postUpdate(){}
	
	public void update(){}
	
	public int getId(){
		return guid;
	}
	
	public void addChild(Node n){
		children.put(n.getId(), n);
		n.setParent(this);
	}
	
	private void setParent(Node n){
		this.parent = n;
		this.level = n.getLevel()+1;
	}
	
	public int getLevel(){
		return level;
	}
	
	public Node getParent(){
		return parent;
	}
	
	public final void message(Message msg, int level){
		if(msg.getAddressLenght() > level){
			int addr = msg.getAddress(level);
			Node next = children.get(addr);
			if(next != null)
				next.message(msg, level+1);
			else
				Logger.getLogger(this.getClass().getName()).severe(msg+" lost.");
		}else
			messageRecieved(msg);
	}
	
	public void messageRecieved(Message msg){}
}
