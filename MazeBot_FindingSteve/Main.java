public class Main {
    public static void main (String[] args) {
        Space start = new Space(1,0,"S");
        Space end = new Space (5,5, "E");

        Maze m1 = new Maze(6,start, end);

        m1.setMazeWall(2,0);
        m1.setMazeWall(3,0);
        m1.setMazeWall(1,2);
        m1.setMazeWall(2,2);
        m1.setMazeWall(3,2);
        m1.setMazeWall(4,2);
        m1.setMazeWall(4,3);
        m1.setMazeWall(1,4);
        m1.setMazeWall(2,2);

        //AStar a1= new AStar(m1, true);

        //System.out.println(a1.solve());
    }
}
