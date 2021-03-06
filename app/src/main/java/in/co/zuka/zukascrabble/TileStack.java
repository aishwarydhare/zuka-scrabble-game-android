package in.co.zuka.zukascrabble;

import java.util.Iterator;
import java.util.Stack;

public class TileStack {

    static final int MAX_STACK_HEIGHT = 1;

    private Stack<Tile> tileStack;

    public TileStack() {
        tileStack = new Stack<Tile>();
    }

    public TileStack(TileStack ts) {
        tileStack = new Stack<Tile>();
        tileStack.addAll(ts.getStack());
    }

    public boolean removeTop() {
        Tile t;
        t = this.deleteTop();
        return (t == null) ? false : true;
    }

    public boolean removeTile(Tile tile) {
        Tile t = tileStack.peek();
        if (tile.equals(t)) {
            t = this.deleteTop();
        }
        return (t == null) ? false : true;
    }

    public Tile deleteTop() {
        if (tileStack.isEmpty())
            return null;
        return tileStack.pop();
    }

    public Tile getTop() {
        return (tileStack.isEmpty()) ? null : tileStack.peek();
    }

    public Stack<Tile> getStack() {
        return tileStack;
    }

    public boolean addTile(Tile tile) {
        if ((tile == null) || (!canAddTile()))
            return false;
        tileStack.push(tile);
        return true;
    }

    public Iterator<Tile> getStackIterator() {
        return tileStack.listIterator();
    }

    public boolean hasSingleTile() {
        return (tileStack.size() == 1) ? true : false;
    }

    public boolean canAddTile() {
        if (tileStack.size() < MAX_STACK_HEIGHT)
            return true;
        return false;
    }

    public boolean isEmpty() {
        return tileStack.isEmpty();
    }

    public int getSize() {
        return tileStack.size();
    }

    public int getNumTilesInStack() {
        return tileStack.size();
    }


}
