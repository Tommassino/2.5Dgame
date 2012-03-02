package cz.witzany.gamev2.graphics;

public interface Mutator<T extends Node> {
	public void update(T obj, int diff);
}
