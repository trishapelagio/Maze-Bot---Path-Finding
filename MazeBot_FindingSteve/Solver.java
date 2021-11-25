import java.util.*;

public abstract class Solver { // Parent class for implementing algorithms
    protected Explorer explorer;
    protected Maze maze;
    protected String result;
    protected AbstractCollection<Node> fringe; // Abstract in order to support further search algorithms
    protected AbstractCollection<Point> explored;
    protected int nodesCounter;
    protected int pathLength;

    public abstract String solve();

    public abstract ArrayList<Node> getNextSpaces(); // Function obtains the adjacent spaces that can be traversed

    public abstract String getResult(); // This returns the result of an algorithm

    public abstract AbstractCollection<Node> getFringe();
    public abstract AbstractCollection<Point> getExplored();
}
