import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import java.io.File;
import java.lang.Integer;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import java.util.Optional;
import javafx.scene.media.AudioClip;

public class MazeGui extends Application
{

  Stage stage; //TODO: remove size editor in GUI, add 'Reset', change Zombie to Steve, change name from finding steve
  // to finding diamonds, click drag to change walls or click a side button to change the type that click drag sets
  // NOTE: TO CHANGE SIZE OF GRID, CHANGE THE SIZE HERE IN MAZE GUI

  Solver solver;
  private Scene startScene, mazeScene;
  public static final String MAIN_MENU = "MENU";
  public static final String MAZE = "MAZE";
  private final Font MINECRAFTIA = Font.loadFont(getClass().getResourceAsStream("assets/Minecraftia.ttf"),40);
  /*
  * To set a 15 by 15 tile map:
  * x_tiles = 600/40 = 15
  * y_tiles = 600/40 = 15
  */
  public static int size = 20;
  private static int TILE_SIZE = 30;
  private static int MAP_WIDTH = TILE_SIZE * (size + 2);
  private static int MAP_HEIGHT = TILE_SIZE * (size + 2);
  private static int X_TILES = size + 2;
  private static int Y_TILES = size + 2;
  private Tile[][] grid = new Tile[X_TILES][Y_TILES];
  private static Space[][] spaceGrid = new Space[X_TILES][Y_TILES];
  private static Space start;
  private static Space end;
  ImageView getStartedButton;
  ImageView beginFindingButton;
  ImageView changeSizeButton;
  ImageView resetButton;
  TextField sizeTextField;
  Text stepsLabel;
  Text exploredLabel;
  static GridPane mazePane;
  static Pane map  = new Pane();
  private static final Image GRASS_PATH = new Image("assets/grass_path_top.png", TILE_SIZE, TILE_SIZE, true, true);
  private static final Image DIAMOND_ORE = new Image("assets/diamond_ore.png", TILE_SIZE, TILE_SIZE, true, true);
  private static final Image POWDER = new Image("assets/concretepowder_black.png", TILE_SIZE, TILE_SIZE, true, true);
  private static final Image BIRCH_PLANKS = new Image("assets/birch_planks.png", TILE_SIZE, TILE_SIZE, true, true);
  private static final Image EXPLORED = new Image("assets/concretepowder_red.png", TILE_SIZE, TILE_SIZE, true, true);
  private static final Image PATH = new Image("assets/concretepowder_blue.png", TILE_SIZE, TILE_SIZE, true, true);
  private static final Image ZOMBIE = new Image("assets/zombie.gif", TILE_SIZE, TILE_SIZE, true, true);
  private static final AudioClip buttonSound = new AudioClip(new File("assets/sounds/btn_click.wav").toURI().toString());
  public static final AudioClip walkSound = new AudioClip(new File("assets/sounds/walk.mp3").toURI().toString());
  public static final AudioClip goalSound = new AudioClip(new File ("assets/sounds/goal.mp3").toURI().toString());
  public static final AudioClip goingToGoalSound = new AudioClip(new File ("assets/sounds/orb.mp3").toURI().toString());
  private static final AudioClip blockSound = new AudioClip(new File ("assets/sounds/block.mp3").toURI().toString());
  private static final AudioClip c418 = new AudioClip(new File ("assets/sounds/c418.mp3").toURI().toString());
  public MazeGui()
  {
  }

  @Override
  public void start(Stage currentStage)
  {
      stage = currentStage;
      createScenes();
      MazeController controller = new MazeController(this);
      currentStage.setScene(startScene);
      currentStage.setMaximized(true);
      currentStage.setTitle("Minecraft Maze Bot");
      currentStage.setResizable(false);
      currentStage.show();

  }
  private void createScenes()
  {
      /*  Start Screen */
      Label welcomeLabel = new Label();
      ImageView title = new ImageView(new Image("assets/titlescreen.png"));
      getStartedButton = new ImageView(new Image("assets/mcbutton.png",150,80, true, false));
      getStartedButton.setId("get-started"); /* CHECK Controller to see what it does! :) */
      getStartedButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
        buttonSound.play();
        c418.play();
        this.setScene(MAZE);
      });
      BorderPane mainPane = new BorderPane();
      VBox mainBox = new VBox();
      mainBox.setAlignment(Pos.CENTER);
      mainBox.getChildren().addAll(title, getStartedButton);
      BackgroundImage titlebg = new BackgroundImage(new Image("assets/titlebg.jpg", true),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          new BackgroundSize(1.0, 1.0, true, true, false, false));

      mainPane.setBackground(new Background(titlebg));
      mainPane.setStyle("-fx-font-size: 2em;");
      mainBox.setMargin(getStartedButton, new Insets(15, 0, 0, 0));
      mainPane.setCenter(mainBox);
      startScene = new Scene(mainPane, 1000, 700);

      /* Maze Grid */
      mazePane = new GridPane();
      VBox mapInfo = new VBox(10);
      ImageView stepsImg = new ImageView(new Image("assets/steps.png", 200, 200, true, true));
      ImageView sizeImg  = new ImageView(new Image("assets/size.png", 140, 140, true, true));
      ImageView exploredImg = new ImageView(new Image("assets/explored.png", 250, 250, true, true));
      beginFindingButton = new ImageView(new Image("assets/mcbutton.png",150,80, true, false));
      beginFindingButton.setId("start-maze");
      beginFindingButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
        buttonSound.play();
        new MazeController(this).beginSolving();
      });
      VBox.setMargin(beginFindingButton, new Insets(3, 0, 0, 0));
      stepsLabel = new Text("0");
      stepsLabel.setFont(MINECRAFTIA);
      stepsLabel.setFill(Color.PINK);
      VBox.setMargin(stepsLabel, new Insets(-15, 0, 0, 30));
      exploredLabel = new Text("0");
      exploredLabel.setFont(MINECRAFTIA);
      exploredLabel.setFill(Color.LIGHTGREEN);
      VBox.setMargin(exploredLabel, new Insets(-15, 0, 0, 30));

      sizeTextField = new TextField();
      VBox.setMargin(sizeTextField, new Insets(5, 0, 0, 10));

      changeSizeButton = new ImageView(new Image("assets/sizebutton.png",150,80, true, false));
      changeSizeButton.setId("change-size");
      changeSizeButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
          buttonSound.play();
          String value = sizeTextField.getText();
          int mazesize  = Integer.parseInt(value);
          Pane mazemap = new Pane();
          if (mazesize <= 8)
            mazemap = setMazePane(8);
          else if (mazesize > 8 && mazesize < 16)
            mazemap = setMazePane(mazesize);
          else if (mazesize >= 16 && mazesize < 24)
            mazemap = setMazePane(mazesize);
          else if (mazesize >= 24 && mazesize < 32)
            mazemap = setMazePane(mazesize);
          else if (mazesize >= 32 && mazesize < 40)
            mazemap = setMazePane(mazesize);
          else if (mazesize >= 40 && mazesize < 56)
            mazemap = setMazePane(mazesize);
          else if (mazesize >= 56 && mazesize < 64)
            mazemap = setMazePane(mazesize);
          else
            mazemap = setMazePane(64);
          MazeGui.mazePane.getChildren().remove(MazeGui.map);
          MazeGui.map = mazemap;
          MazeGui.mazePane.add(mazemap,0,0);
      });

      resetButton = new ImageView(new Image("assets/reset.png",150,80, true, false));
      resetButton.setId("change-size");
      resetButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
        buttonSound.play();
        resetMaze();
      });

      Pane initialMap = new Pane();
      initialMap.setPrefSize(MAP_WIDTH, MAP_HEIGHT);
      for (int y = 0; y < Y_TILES; y++) {
           for (int x = 0; x < X_TILES; x++) {
               Tile tile; Space space;
                 if (x == 0 || (y == 0 || y == Y_TILES-1) || x == X_TILES-1)
                 {
                   tile = new Tile(y,x, true, 1.0);
                   space = new Space(y,x,true);
                 }
                 else
                 {
                   tile = new Tile(y,x, false, 1.0);
                   space = new Space(y,x, false);
                 }
               grid[y][x] = tile;
               spaceGrid[y][x] = space;
               initialMap.getChildren().add(tile);
          }
      }

      MazeGui.map =  initialMap;

      mapInfo.getChildren().addAll(stepsImg, stepsLabel, exploredImg,
      exploredLabel, sizeImg, sizeTextField, changeSizeButton, resetButton, beginFindingButton);

      BackgroundImage mazebg = new BackgroundImage(new Image("assets/dirtbg.png"),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT);
      mazePane.setMargin(MazeGui.map, new Insets(30, 0, 0, 35));
      mazePane.setMargin(mapInfo, new Insets(60, 0, 0, 25));
      mazePane.add(MazeGui.map, 0,0);
      mazePane.add(mapInfo, 1,0);
      mazePane.setBackground(new Background(mazebg));
      mazeScene = new Scene(mazePane, 1000, 700);
  }

  private Pane setMazePane(int DIMENSION)
  {
    size = DIMENSION;
    X_TILES = size + 2;
    Y_TILES = size + 2;
    grid = new Tile[Y_TILES][X_TILES];
    spaceGrid = new Space[Y_TILES][X_TILES];
    System.out.println("Succesfully changed dimension!");

    Pane map = new Pane();
    map.setPrefSize(MAP_WIDTH, MAP_HEIGHT);
    for (int y = 0; y < Y_TILES; y++) {
          for (int x = 0; x < X_TILES; x++) {
              Tile tile; Space space;
                if (x == 0 || (y == 0 || y == Y_TILES-1) || x == X_TILES-1)
                {
                  tile = new Tile(y,x, true, 22.0 / X_TILES);
                  space = new Space(y,x,true);
                }
                else
                {
                  tile = new Tile(y,x, false, 22.0 / X_TILES);
                  space = new Space(y,x, false);
                }
              grid[y][x] = tile;
              spaceGrid[y][x] = space;
              map.getChildren().add(tile);
          }
    }
    return map;
  }

  private class Tile extends StackPane
  {

      private int x,y;
      private boolean isWall;
      private Rectangle border;
      private ImageView block;
      private int type;
      /*
        0 - space
        1 - wall
        2 - start
        3 - goal
      */
      public Tile(int y, int x, boolean isWall, double scale)
      {
        this.x = x;
        this.y = y;
        this.isWall = isWall;
        this.border = new Rectangle((TILE_SIZE-2) * scale, (TILE_SIZE-2) * scale);
        this.type = 0;
        if (isWall)
          block = new ImageView(BIRCH_PLANKS);
        else
          block = new ImageView(GRASS_PATH);
        border.setFill(Color.TRANSPARENT);
        getChildren().addAll(block, border);
        setTranslateX(x * TILE_SIZE * scale);
        setTranslateY(y * TILE_SIZE * scale);
        setScaleX(scale);
        setScaleY(scale);
        setOnMouseClicked(e -> updateTile());
      }

      public ImageView getBlock()
      {
          return this.block;
      }

      public void setWall(boolean isWall)
      {
        this.isWall = isWall;
      }

      public void updateTile()
      {
        System.out.println("Changing tile image of (" + x + ", " + y + ")....");
        this.type++;
        this.type %= 4;
        switch(this.type)
        {
          case 0:
            blockSound.play();
            block.setImage(GRASS_PATH);
            this.isWall = false;
            spaceGrid[y][x].setAttribute(" ");
            break;
          case 1:
            blockSound.play();
            block.setImage(BIRCH_PLANKS);
            this.isWall = true;
            spaceGrid[y][x].setWall();
            spaceGrid[y][x].setAttribute("B");
            break;
          case 2:
            blockSound.play();
            block.setImage(POWDER);
            start = MazeGui.spaceGrid[y][x];
            spaceGrid[y][x].setAttribute("S");
            this.isWall = false;
            break;
          case 3:
            blockSound.play();
            block.setImage(DIAMOND_ORE);
            end = MazeGui.spaceGrid[y][x];
            spaceGrid[y][x].setAttribute("E");
            this.isWall = false;
            /* TODO: Create function to save the coordinate of the goal */
            break;
      }
    }
  }

  public void updateTile(int y, int x, int index)
  {
      Image image = null;
      switch (index)
      {
          case 0:
              image = EXPLORED;
              break;
          case 1:
              image = PATH;
              break;
          case 2:
              image = ZOMBIE;
              break;
      }
      //TODO: find why this is x y not y x
      grid[y][x].getBlock().setImage(image);
  }

  private void updateZombie()
  {
    /* TODO: Update Start Tile with Steve*/

  }

  private void resetMaze()
  {
      for (int y = 0; y < Y_TILES; y++) {
          for (int x = 0; x < X_TILES; x++) {
              if (x != 0 && (y != 0 && y != Y_TILES-1) && x != X_TILES-1)
              {
                grid[y][x].getBlock().setImage(GRASS_PATH);
                grid[y][x].setWall(false);
                grid[y][x].type = 0;
                spaceGrid[y][x].setAttribute(" ");
                spaceGrid[y][x].unsetWall();
              }
          }
      }
      stepsLabel.setText("0");
      exploredLabel.setText("0");
  }

  public Space getStartSpace()
  {
    return start;
  }
  public Space getEndSpace()
  {
    return end;
  }

  public Space[][] getGrid()
  {
    return spaceGrid;
  }
  public void setScene(String scene)
  {
    if (scene == MAIN_MENU)
      stage.setScene(startScene);
    else if (scene == MAZE)
      stage.setScene(mazeScene);
  }

  private void attachHandlerToPane(Pane pane, EventHandler<ActionEvent> handler)
  {
    for (Node node : pane.getChildren())
    {
        if (node instanceof Button)
        {
            Button button = (Button)node;
            button.setOnAction(handler);
        }
        else if (node instanceof Pane)
            attachHandlerToPane((Pane)node, handler);
      }
  }

  private void attachHandlerToScene(Scene scene, EventHandler<ActionEvent> handler)
  {
    Parent root = scene.getRoot();
    if (root instanceof Pane)
    {
        attachHandlerToPane((Pane)root, handler);
    }
  }

  public void addActionListener(EventHandler<ActionEvent> handler)
  {
    attachHandlerToScene(startScene, handler);
    attachHandlerToScene(mazeScene, handler);
  }

  public void addMouseListener(EventHandler<MouseEvent> handler)
  {
      getStartedButton.setOnMouseClicked(handler);
      beginFindingButton.setOnMouseClicked(handler);
  }

  public static void main(String[] args)
  {
      launch(args);
  }
}
