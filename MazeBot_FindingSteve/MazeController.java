import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import java.util.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;

public class MazeController implements EventHandler<ActionEvent>, Explorer
{
  private MazeGui gui;
  private ArrayList<Node> path;
  private int dimension;
  private Maze maze;
  private char[][] map;
  private Space start;
  private Space end;
  Point[] explored;

  public MazeController(MazeGui gui)
  {
    this.gui = gui;
    gui.addActionListener(this);
    path = new ArrayList<>();
  }
  @Override
  public void handle(ActionEvent event)
  {
    EventTarget target = event.getTarget();
    if (target instanceof javafx.scene.Node)
    {
      javafx.scene.Node node = (javafx.scene.Node)target;
    //  Alert alert;
      Optional<ButtonType> result;
      switch(node.getId())
      {
        case "get-started":
          gui.setScene(MazeGui.MAZE);
          break;
         /* Start A* Algorithm */
        case "start-maze":
          gui.setScene(MazeGui.MAIN_MENU);
          break;
      }
    }
  }

  public void beginSolving()
  {
    start = gui.getStartSpace();
    end = gui.getEndSpace();
    System.out.printf ("%d %d, %d %d\n", start.getX(), start.getY(), end.getX(), end.getY());
    Maze m1 = new Maze(gui.size + 2, start, end);

    Space[][] grid = gui.getGrid();

    for (int y = 0; y < gui.size + 2; y++) {
      for (int x = 0; x < gui.size + 2; x++)
      {
        if (grid[y][x].isWall()) {
          m1.setMazeWall(y, x);
          //System.out.printf ("%d %d %b\n", y, x, grid[y][x].isWall());}
          System.out.print("#");
        } else {
          System.out.print(".");
        }
      }
      System.out.println();
    }

    new Thread(() -> { AStar a1= new AStar(this, m1); System.out.println(a1.solve());}).start();


  }

  // called when the A* algorithm explores a new tile
  public void onExplore(Solver state) {
    // this is where you update the tiles and gui
    explored = state.getExplored().toArray(new Point[0]);
    for (int i = 0; i < explored.length - 1; i++)
    {
      gui.updateTile(explored[i].y, explored[i].x, 0);
      {
        final int i1 = i;
        Platform.runLater(() -> gui.exploredLabel.setText(Integer.toString(i1)));
        gui.walkSound.play();
      }
    }
    gui.updateTile(explored[explored.length - 1].y, explored[explored.length - 1].x, 2);
    Platform.runLater(() -> gui.exploredLabel.setText(Integer.toString(explored.length)));
  }

  @Override
  public void onPathFound(Node solution) {
    //remove the zombie from the last path point
    gui.updateTile(explored[explored.length - 1].y, explored[explored.length - 1].x, 0);
    // display the path
    int stepscount = 0;
    Node current = solution.getParent();
    ArrayList<Space> reverse = new ArrayList<Space>();

    while (current.hasParent()) {
      Maze content = current.getContent();
      Space space = content.getCurrentSpace();
      reverse.add(space);
      current = current.getParent();
    }
    /* print the optimal path in reverse */
    Space s;
    for (int i = reverse.size()-1; i >= 0; i--)
    {
      s = reverse.get(i);
      try {
        Thread.sleep(20);
      } catch (Exception ex) {

      }

      {
        final int stepscount1 = stepscount;
        Platform.runLater(() -> gui.stepsLabel.setText(Integer.toString(stepscount1)));
      }
      gui.updateTile(s.getY(), s.getX(), 1);
      stepscount++;
      gui.goingToGoalSound.play();
    }

    {
      final int stepscount1 = stepscount;
      Platform.runLater(() -> gui.stepsLabel.setText(Integer.toString(stepscount1)));
      gui.goalSound.play();
    }
  }
}
