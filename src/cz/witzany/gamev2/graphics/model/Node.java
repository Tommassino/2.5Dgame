package cz.witzany.gamev2.graphics.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import cz.witzany.gamev2.net.Message;

/**
 * Class representing a node in the graphics tree. Each node represents a
 * possibly drawable/updatable part of the screen.
 * 
 * @author tommassino
 * 
 */
public class Node {

	private int level;
	private Node parent;
	private HashMap<Integer, Node> children;

	public Node() {
		this.level = -1;
		this.parent = null;
		children = new HashMap<Integer, Node>();
	}

	public final void tick() {
		update();
		for (Node n : children.values())
			n.tick();
		postUpdate();
	}

	public void postUpdate() {
	}

	public void update() {
	}

	public void addChild(Node n) {
		if (children.keySet().size() > 0)
			addChild(Collections.max(children.keySet()) + 1, n);
		else
			addChild(1, n);
	}

	public void addChild(int id, Node n) {
		children.put(id, n);
		n.setParent(this);
	}

	private void setParent(Node n) {
		this.parent = n;
		this.level = n.getLevel() + 1;
	}

	public int getLevel() {
		return level;
	}

	public Node getParent() {
		return parent;
	}

	public final void message(Message msg, int level) {
		if (msg.getAddressLenght() > level) {
			int addr = msg.getAddress(level);
			Node next = children.get(addr);
			if (next != null)
				next.message(msg, level + 1);
			else
				Logger.getLogger(this.getClass().getName()).severe(
						msg + " lost.");
		} else
			messageRecieved(msg);
	}

	public void messageRecieved(Message msg) {
	}
}
