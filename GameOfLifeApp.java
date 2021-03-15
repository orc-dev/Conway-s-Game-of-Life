package gameOfLife;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Simulation: Conway's Game of Life
 * 	   This class assembles a main board, an info pane and a control
 * panel onto the scene. The main board displays the pattern of
 * each generation. The info pane displays some statistics. The
 * control panel provides three buttons and two checkboxes for the
 * user to control the behavior of the main board.
 *     This class defines the function of each button and checkbox,
 * so that the user can perform simulation of Conway's game of life.
 *  
 * @author Xin Cai
 * Date: 2020.August.21 - 27
 */
public class GameOfLifeApp extends Application {

  private MainBoard main;
  private InfoPane info;
  private Button restart;
  private Button breaK;
  private CheckBox unbnd;

  /** main: launch the app */
  public static void main(String[] args) {
	Application.launch(args);
  }

  /** start: set up scene and stage */
  @SuppressWarnings("exports")
  public void start(Stage stage) {
		
	/* create and load the main board */
	main = new MainBoard(20, 32);
	main.setPadding(new Insets(0, 30, 2, 30));
	main.setAlignment(Pos.CENTER);
	for (int i = 0; i < main.getSize(); ++i)
	  main.loadCellHandler(new CellHandler(i));
		
	/* create and load info pane */
	double[] width = {120, 75, 120, 60, 120, 40, 120, 60};
	String[] names = {"Generation: ", "Initial Live Cells: ",
	    "Current Live Cells: ", "Max Live Cells: "};
		
	info = new InfoPane(4); // 4 sets of values
	info.setPadding(new Insets(15, 0, 0, 0));
	info.setAlignment(Pos.CENTER);
	info.setWidth(width);
	info.setLabels(names);
	
	/* create and load control panel */
	unbnd = loadCBoxUnbd();
	CheckBox trace = loadCBoxTraj();
	Button newSimu = loadButtonNext();
	restart = loadButtonRestart();
	breaK = loadButtonBreak();
	
	HBox ctrlPanel = new HBox(20);
	ctrlPanel.setPadding(new Insets(4, 0, 17, 0));
	ctrlPanel.setAlignment(Pos.CENTER);
	ctrlPanel.getChildren().addAll(newSimu, restart, breaK, unbnd, trace);

	/* create root pane and add all nodes */
	FlowPane root = new FlowPane(10, 10);
	root.setAlignment(Pos.CENTER);
	root.getChildren().addAll(info, main, ctrlPanel);
		
	/* create scene and add event handler on 'Right Key' */
	Scene scene = new Scene(root);
	loadEventHandlerOnRightKey(scene);
	
	/* set up and show stage */
	stage.setTitle("Brief Simulation: Game of Life");
	stage.setScene(scene);
	stage.setResizable(false);
	stage.show();
  }
	
  /** Inner Class: create handler for clicking cells */
  class CellHandler implements EventHandler<MouseEvent> {
	private int index;
		
    /** constructor */
    public CellHandler(int index) {
	  this.index = index;
	}
	
	/** return index */
	public int getIndex() {
	  return index;
	}
	
	/** 
	 * Create mouse event handler
	 * In the 0th generation, users are allowed to set an initial
	 * pattern as a starting point of simulation by clicking cells.
	 * 	- Clicking a white square (dead cell) sets it live;
	 * 	- Clicking a black square (live cell) sets it dead;
	 * 	- Each clicking will also update the info pane to show 
	 *    updated 'Initial Live Cells', 'Current Live Cells', 
	 *    and 'Max Live Cells';
	 * Once the initial pattern is set, as the right key is pressed,
	 * the program will update and display the pattern of next 
	 * generation. Thus, current generation will be greater than 0
	 * and the function of setting initial pattern will not work.
	 */
	@Override
	public void handle(MouseEvent e) {
	  if (main.isZeroGen() && e.getButton() == MouseButton.PRIMARY) {
		if (main.isLive(index)) {
		main.setDead(index);
		main.removeFromZeroGen(index);
		} else {
		  main.setLive(index);
		  main.addToZeroGen(index);
		}
		  main.resetMax();
		  updateText();
		}
	  }
  }
	
  /** 
   * Add event handler on KeyCode.RIGHT: evolution
   * 
   * Once the 'Right Key' is pressed, the program updates
   * and displays the pattern of a new generation.
   *   - This function will inactivated when the current pattern 
   *     falls into a still pattern (all the next generation has 
   *     the same pattern as the current pattern);
   *   - Invoking the computeNextGen() method, the states of each
   *     cell in next generation are computed according the rules
   *     of Conway's game of life
   *   - By checking whether the current generation is the 0th turn,
   *     if true, 
   *         update the trajectory array;
   *         set the checkbox 'Unbounded' disable;
   *         set the buttons 'Restart' and 'Break' available;
   *   - Then, do the followings:
   *         display the updated pattern by refreshCurrGen();
   *         increment the number of iteration by increamentIterNum();
   *         update the trajectory array by updateTrajGen();
   *         update the statistics by updateText();
   */
  private void loadEventHandlerOnRightKey(Scene scene) {
	scene.setOnKeyPressed(e -> {
	  boolean activated = true;
		if (!main.isZeroGen() && main.isStill())
		  activated = false;
			
		if (e.getCode() == KeyCode.RIGHT && activated) {
		  main.computeNextGen();
		  if (main.isZeroGen()) {
		    main.updateTrajGen();
			unbnd.setDisable(true);
			restart.setDisable(false);
			breaK.setDisable(false);
		  }
          main.refreshCurrGen();
		  main.incrementIterNum();
		  main.updateTrajGen();
		  updateText();
		}
    });
  }
		
  /** 
   * Set up and return Button: 'New Simulation'
   * Button Function:
   * 	  return to a new 0th generation with none live
   * cells on the board by clearing all current data
   */
  private Button loadButtonNext() {
	Button btn = new Button("New Simulation");
	btn.setPrefWidth(110);
	btn.setFocusTraversable(false);
	btn.setOnAction(e -> {
	  main.reset();  // reset
	  updateText();
	  unbnd.setDisable(false);
	  restart.setDisable(true);
	  breaK.setDisable(true);
	});
	return btn;
  }
	
  /** 
   * Set up and return Button: 'Restart'
   * Button Function:
   *    return to the 0th generation of this simulation;
   *    the board shows exactly the same as the initial pattern
   * of the 0th turn in this simulation;
   *    user can edit this initial pattern;
   */
  private Button loadButtonRestart() {
	Button btn = new Button("Restart");
	btn.setPrefWidth(110);
	btn.setFocusTraversable(false);
	btn.setDisable(true);
	btn.setOnAction(e -> {
	  main.restart();  // restart
	  updateText();
	  unbnd.setDisable(false);
	  restart.setDisable(true);
	  breaK.setDisable(true);
	});
	return btn;
  }
	
  /** 
   * Set up and return Button: 'Break'
   * Button Function:
   *    take the current generation as a new 0th generation;
   *    user can edit this initial pattern
   */
  private Button loadButtonBreak() {
	Button btn = new Button("Break");
	btn.setPrefWidth(110);
	btn.setFocusTraversable(false);
	btn.setDisable(true);
	btn.setOnAction(e -> {
	  main.setBreak();  // setBreak
	  updateText();
	  unbnd.setDisable(false);
	  restart.setDisable(true);
	  breaK.setDisable(true);
	});
    return btn;
  }
	
  /** 
   * Set up and return CheckBox: 'Unbounded'
   * 
   * This chekbox can only be set in each 0th generation; thus, it
   * is disable during the process of simulation.
   * 
   * This checkbox determines whether the bound blocks live cells.
   * if the checkbox is selected, all cells has 8 neighbors; for cells
   * on the edge, they counts the cells on the other edge as their neighbors.
   */
  private CheckBox loadCBoxUnbd() {
	CheckBox box = new CheckBox("Unbounded");
	box.setFocusTraversable(false);
	box.setOnAction(e -> {
	  main.setUnbounded(box.isSelected());
	});
	return box;
  }

  /** 
   * Set up and return CheckBox: 'Trajectory'
   * 
   * The program will trace the trajectory of live cells, that is,
   * for each square, once a live cell has appeared there, this square
   * will be added into the array of TrajGen.
   * 
   * Once the checkbox is selected, trajectorys will be displayed in 
   * each turn of updating. The color of trajectory is gray.
   */
  private CheckBox loadCBoxTraj() {
	CheckBox box = new CheckBox("Trajectory");
	box.setFocusTraversable(false);
	box.setOnAction(e -> {
	  main.setShowTraj(box.isSelected());
	  if (!main.isZeroGen())
		main.refreshCurrGen();
	});
	return box;
 }
	
  /** 
   * Update statistics by displaying the following 4 values:
   *   - Generation: the nth iteration
   *   - Initial Live Cells: the number of live cells in the 0th turn
   *   - Current Live Cells: the number of live cells in current turn
   *   - Max Live Cells: the max number of live cells since the 0th turn
   */
  private void updateText() {
	int[] vals = { main.getIterNum(),     
	               main.countInitCells(),
	               main.countCurrCells(), 
	               main.countMaxCells() 
	             };
	info.updateText(vals);
  }

}

