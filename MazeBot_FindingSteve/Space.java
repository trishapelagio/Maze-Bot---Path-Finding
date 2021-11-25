import java.lang.*;
import java.util.*;

public class Space {
    private Maze maze;
    private int x; //column
    private int y; //row
    private String a; //can be removed, to be used for testing

    /*
        a could be set to

        S = start position
        E = end position
        [SPACE] = open
        * = closed
        o = current position
     */

    private boolean wall;

    private int G; //path cost
    private double H; //heuristic cost
    private double F; //total cost

    //Assign this to an open space
    public Space(int y, int x, String a) {
        this.x = x;
        this.y = y;
        this.a = a;
        this.wall = false;
        this.G = 0;
        this.H = 0;
        this.F = 0;
    }

    //Assign this to a wall space
    public Space(int y, int x, boolean w) {
        this.x = x;
        this.y = y;
        this.a = null;
        this.wall = w;
        this.G = 0; // Actual cost g(n)
        this.H = 0; // Heuristic value h(n)
        this.F = 0; // Function value f(n)
    }

    public void assignMaze (Maze m) {
        this.maze = m;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

	public Point getPoint() {
		return new Point(x, y);
	}

    /*
        From the current Space, ge the next space to be traversed
     */

    public ArrayList<Maze> getNexts() {
        ArrayList<Maze> next = new ArrayList<>();

        for(int i=0 ; i<4 ; i++) { // Adds states based on priority specified
            Maze temp = this.maze.clone();

            if(this.maze.priority[i] == 'N') {
                if(this.getN() != null && !this.getN().isWall()) {
                    temp.setNextSpace (this.getN());
                    next.add(temp);
                }
            }
            else if (this.maze.priority[i] == 'E'){
                if(this.getE() != null && !this.getE().isWall()) {
                    temp.setNextSpace (this.getE());
                    next.add(temp);
                }
            }
            else if (this.maze.priority[i] == 'S') {
                if(this.getS() != null && !this.getS().isWall()) {
                    temp.setNextSpace (this.getS());
                    next.add(temp);
                }
            }
            else if (this.maze.priority[i] == 'W') {
                if(this.getW() != null && !this.getW().isWall()) {
                    temp.setNextSpace (this.getW());
                    next.add(temp);
                }
            }
        }
        return next;
    }

    //returns square at north from given space, if there is
    public Space getN() {
        if(this.y-1 < 0)
            return null;
        else
            return this.maze.getGrid()[this.y-1][this.x];
    }

    //returns square at west from given space, if there is
    public Space getW() {
        if(this.x-1 < 0)
            return null;
        else
            return this.maze.getGrid()[this.y][this.x-1];
    }

    //returns square at south from given space, if there is
    public Space getS() {
        if(this.y + 1 == this.maze.dimension)
            return null;
        else
            return this.maze.getGrid()[this.y+1][this.x];
    }

    //returns square at east from given space, if there is
    public Space getE() {
        if(this.x + 1 == this.maze.dimension)
            return null;
        else
            return this.maze.getGrid()[this.y][this.x+1];
    }

    public String getAttribute() {
        return a;
    }

    //Considers error validation if input is not correct, it does not do anything
    public void setAttribute(String a) {
        if(a.equals(" ") || a.equals("S") || a.equals("E") || a.equals("*")) {
            this.a = a;
            this.wall = false;
        }
    }

    //Returns if wall attribute or not
    public boolean isWall() {
        return this.wall;
    }

    //Set space as wall & makes its attribute null
    public void setWall() {
        this.wall = true;
        this.a = "b";
    }

    public void unsetWall() {
        this.wall = false;
        this.a = " ";
    }
    //Return H value
    public double getH() {
        return this.H;
    }

    //Compute heuristic using Manhattan Distance
    public void calcManhattanH () {
        this.H = Math.abs(this.getY() - this.maze.getEnd().getY()) +
                 Math.abs(this.getX() - this.maze.getEnd().getX());
    }

    //Return G value
    public int getG() {
        return G;
    }

    //Increase value of G
    public void incG (int prev) {
        this.G = 1 + prev;
    }

    //Compute F value by addition of H and G
    public double calcF() {
        this.F = this.G + this.H;
        return this.F;
    }

    //Return F value
    public double getF () {
        return this.F;
    }

    @Override
    public String toString() {
        return "[" + this.y + ", " + this.x + "] (" + this.F + ")";
    }

}
