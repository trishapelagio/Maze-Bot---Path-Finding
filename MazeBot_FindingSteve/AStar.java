import java.util.*;

/*

Further considerations
- if f(n) is equal
- manhattan or euclidean distance
* */

public class AStar extends Solver {
    private HashSet<Point> fringePoints = new HashSet<>();

    public AStar(Explorer explorer, Maze maze) {
        this.explorer = explorer;
        this.maze = maze;
        this.result = "";
        this.maze.getStart().assignMaze(this.maze);
        this.fringe = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node s1, Node s2) {
                Double sf1 = s1.getContent().getCurrentSpace().getF();
                Double sf2 = s2.getContent().getCurrentSpace().getF();
                Double sh1 = s1.getContent().getCurrentSpace().getH();
                Double sh2 = s2.getContent().getCurrentSpace().getH();

                if(sf1 > sf2) // Comapres function values
                    return 1;
                else if(sf1 == sf2) { // If f(n) is equal, comapre heuristic values
                    if(sh1 > sh2)
                        return 1;
                    else if(sh1 == sh2)
                        return 0;
                    else
                        return -1;
                }
                else
                    return -1;
            }
        });
        this.explored = new HashSet<>();

    }

    public String solve() {
        this.maze.initMaze(); //Re-init maze
        fringePoints.clear();

        Boolean endfound = false; // Initially false until end state is reached
        this.nodesCounter = 0;
        this.pathLength = 0;

        //Compute F value of Starting square //
        this.maze.getStart().calcManhattanH();

        this.maze.getStart().calcF();

        //Init data structures
        this.fringe.clear(); //Clear fringe Queue
        ((PriorityQueue<Node>)this.fringe).offer(new Node(this.maze)); //Adding the first node (Start node) (G is at 0, Start to Start = 0)
        this.explored.clear(); //Clear explored

        while(!endfound)
        {
            if(this.fringe.isEmpty())
                break;
            else
            {
                Node current = ((PriorityQueue<Node>) this.fringe).remove(); //Remove current most optimal path
                this.maze = (Maze) current.getContent();
                Space currState = this.maze.getCurrentSpace();

                if(currState.getX() == this.maze.getEnd().getX() && currState.getY() == this.maze.getEnd().getY()) // Goal state is reached
                {
                    Node temp = new Node(this.maze);
                    temp.setParent(current);
                    ((PriorityQueue<Node>) this.fringe).offer(temp);
                    endfound = true;
                }

                else
                {
                    ArrayList<Node> nexts = this.getNextSpaces(); // Do not get spaces with walls
//                    this.explored.add(currState);
                    if(!this.explored.contains(currState.getPoint())) {
                        this.explored.add(currState.getPoint());
                        currState.setAttribute("*");
                    }

                    ArrayList<Node> x = new ArrayList<Node>();
                    for(int i = 0; i < nexts.size(); i++)
                        x.add(nexts.get(i));

                    for(int i= 0 ; i < x.size() ; i++) {
                        Node neighbor = x.get(i);

                        Point pt = neighbor.getContent().getCurrentSpace().getPoint();
                        if(!this.explored.contains(pt)) { //Do not re-explore paths already explored
                            if(!this.fringe.contains(neighbor) && !fringePoints.contains(pt)) {  // Do not add paths already in fringe (possibility of being explored)
                                neighbor.setParent(current);
                                ((PriorityQueue<Node>) this.fringe).offer(neighbor);
                                fringePoints.add(pt);
                                this.nodesCounter++;
                            }
                        }
                    }

                    // notify the gui and show explored tiles
                    try {
                        Thread.sleep(20);
                    } catch (Exception ex) {

                    }

                    explorer.onExplore(this);
                }
            }
        }

        this.result = "A-Star Search Algorithm\n\n";

        if(endfound)
        {
            // this.maze.resetGrid();
            Node revertedTree = ((PriorityQueue<Node>) this.fringe).remove();
            explorer.onPathFound(revertedTree);

            revertedTree = revertedTree.getParent();
            this.result += "Path: " + this.maze.getEnd().toString() + "(End) <- ";
            this.pathLength++;

            while(revertedTree.hasParent()) {
                Maze temp = revertedTree.getContent();
                Space state = temp.getCurrentSpace();

                if(!state.equals(this.maze.getEnd()))
                {
                    this.result += state.toString() + " <- ";
                    this.maze.getGrid()[state.getY()][state.getX()].setAttribute("*");
                    pathLength++;
                }
                revertedTree = revertedTree.getParent();
            }

            pathLength--; // Since first node has cost 0

            this.result += this.maze.getStart().toString() + "(Start) \n";
            this.result += this.maze.printMaze();
        }
        else
        {
            this.result += "Cannot go beyond/No Solution.";
        }

        return this.result;
    }

    public ArrayList<Node> getNextSpaces() {
      ArrayList<Node> res = new ArrayList<Node>();

      ArrayList<Maze> nexts = this.maze.getCurrentSpace().getNexts();

      int gCurrent = this.maze.getCurrentSpace().getG();

      for(int i = 0; i < nexts.size(); i++) {
        Space temp = nexts.get(i).getCurrentSpace();
        if(!this.explored.contains(temp.getPoint())) {
            nexts.get(i).getCurrentSpace().calcManhattanH();

          nexts.get(i).getCurrentSpace().incG(gCurrent);
          nexts.get(i).getCurrentSpace().calcF();

          Node tempNode = new Node(nexts.get(i));
          res.add(tempNode);
        }
      }
      return res;
    }

    public String getResult() {
        if(this.result == "")
            return "No solution found";
        else
            return this.result;
    }

    public AbstractCollection<Node> getFringe() {
        return this.fringe;
    }
    public AbstractCollection<Point> getExplored() { return this.explored; }
}
