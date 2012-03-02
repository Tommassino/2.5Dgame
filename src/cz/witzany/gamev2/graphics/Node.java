package cz.witzany.gamev2.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.net.Message;

/**
 * Class representing a node in the graphics tree. Each node represents a
 * possibly drawable/updatable part of the screen.
 * 
 * @author tommassino
 * 
 */
public class Node {

	private long time;
	private int level;
	private Node parent;
	private HashMap<Integer, Node> children;
	private ArrayList<Mutator<?>> mutators;
	private Vector3f position;

	public Node() {
		this.level = -1;
		this.parent = null;
		children = new HashMap<Integer, Node>();
		mutators = new ArrayList<Mutator<?>>();
		position = new Vector3f();
		time = System.currentTimeMillis();
	}
	
	public void addMutator(Mutator<?> m){
		mutators.add(m);
	}

	public final void tick() {
		long nt = System.currentTimeMillis();
		int diff = (int) (nt - time);
		time = nt;
		for(Mutator m : mutators)
			m.update(this,diff);
		update(diff);
		for (Node n : children.values())
			n.tick();
		postUpdate();
	}

	public void postUpdate() {
	}

	public void update(int diff) {
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

	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
	}

	public Vector3f getPosition() {
		return position;
	}

	public void bindPosition(Node p) {
		position = p.position;
	}
}
