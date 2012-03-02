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
	private ArrayList<Mutator<?>> mutators;
	private Vector3f position;

	public Node() {
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
	}

	public void update(int diff) {
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
