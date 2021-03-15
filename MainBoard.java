package gameOfLife;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gameOfLife.GameOfLifeApp.CellHandler;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This class create a matrix of cells and display their states in each 
 * generation. The function of data structures:
 * 
 *   - currGen: a 1D Rectangle array to collect cells;
 *              cells are 20 * 20 squares. A white square represents
 *              a dead cell; a black square represents a live cell
 *   - nextGen: a 1D boolean array to collect the states of all cells
 *   			in the next generation. 'True' represents the cell is
 *   			live in next turn; 'false' represents dead
 *   - trajGen: a 1D boolean array to collect whether a cell has ever
 *   			been live. If 'true' and the checkbox of 'Trajectory'
 *   			is selected, the ever-live, yet current-dead cells
 *              are represented by gray square
 *   - zeorGen: a HashSet<Integer> to collect the indexes of live 
 *              cells in the 0th generation (initial generation)
 *   - indexes: a HashSet<Integer> of all the indexes of cells. It helps
 *   			a programmer to use some aggregate-operation syntaxes to
 *   			write the code rather than to use for-loops.
 */
public class MainBoard extends GridPane {
	
	private int nrow;
	private int ncol;
	private Rectangle[] currGen;
	private   boolean[] nextGen;
	private   boolean[] trajGen;
	private HashSet<Integer> zeroGen;
	private HashSet<Integer> indexes;
	
	private boolean unbounded;
	private boolean showTraj;
	private int iterNum;
	private int cellMax;
	
	/** constructor */
	public MainBoard(int row, int col) {
		nrow = row;
		ncol = col;
		currGen = new Rectangle[nrow * ncol];
		nextGen = new boolean[nrow * ncol];
		trajGen = new boolean[nrow * ncol];
		zeroGen = new HashSet<>();
		indexes = new HashSet<>(nrow * ncol);
		
		/** create cells and place them on the main board */
		for (int i = 0; i < currGen.length; ++i) {
			indexes.add(i);
			currGen[i] = createCell();
			this.add(currGen[i], i % ncol, i / ncol);
		}
	}
	
	/** create default cell: white square */
	private Rectangle createCell() {
		Rectangle cell = new Rectangle(20, 20);
		cell.setFill(Color.WHITE);
		cell.setStroke(Color.rgb(228, 228, 228));
		cell.setStrokeWidth(2);
		return cell;
	}
	
	/** reset game parameters for 'New Simulation' */
	public void reset() {
		trajGen = new boolean[currGen.length];
		iterNum = 0;
		zeroGen = new HashSet<>();  // zeroGen <- empty Set
		indexes.stream().forEach(e -> setDead(e));
	}
	
	/** reset game parameters for 'Restart' */
	public void restart() {
		trajGen = new boolean[currGen.length];
		iterNum = 0;
		indexes.stream().forEach(e -> setDead(e));
		zeroGen.stream().forEach(e -> setLive(e));
	}
	
	/** reset game parameters for 'Break' */
	public void setBreak() {
		trajGen = new boolean[currGen.length];
		iterNum = 0;
		zeroGen = getIndexOfCurrGen();  // zeroGen <- currGen
		indexes.stream().forEach(e -> setDead(e));
		zeroGen.stream().forEach(e -> setLive(e));
	}

	/** return indexes of live cells in current generation */
	private HashSet<Integer> getIndexOfCurrGen() {
		return indexes.stream().filter(e -> isLive(e))
			   .collect(Collectors.toCollection(HashSet::new));
	}
	
	/** import event handler from outside class */
	@SuppressWarnings("exports")
	public void loadCellHandler(CellHandler hdlr) {
		currGen[hdlr.getIndex()].setOnMouseClicked(hdlr);
	}
	
	/** return a list of indexes of all neighbours */
	private HashSet<Integer> getNeighbors(int index) {
		int row = index / ncol;
		int col = index % ncol;
		
		int[][] neighborPoints = new int[][]{
			{row - 1, col - 1},
			{row - 1, col    },
			{row - 1, col + 1},
			{row    , col - 1},
			{row    , col + 1},
			{row + 1, col - 1},
			{row + 1, col    },
			{row + 1, col + 1},
		};
		return Stream.of(neighborPoints)
			   .filter(point -> isOnBoard(point))
			   .map(point -> point[0] * ncol + point[1])
			   .collect(Collectors.toCollection(HashSet::new));
	}
	
	/** check whether a given coordinate is on board */
	private boolean isOnBoard(int[] point) {
		return (point[0] >= 0 && point[0] < nrow &&
				point[1] >= 0 && point[1] < ncol);
	} 

	/** return a list of indexes of all neighbours (unbounded) */
	private HashSet<Integer> getEightNeighbors(int index) {
		int row = index / ncol;
		int col = index % ncol;
                                         // out of bound : within bound
		int top    = (row - 1) < 0          ? (nrow - 1) : (row - 1);
		int left   = (col - 1) < 0          ? (ncol - 1) : (col - 1);
		int bottom = (row + 1) > (nrow - 1) ?          0 : (row + 1);
		int right  = (col + 1) > (ncol - 1) ?          0 : (col + 1);
		
		int[][] neighborPoints = new int[][]{
		    {top,    left },
			{top,    col  },
			{top,    right},
			{row,    left },
			{row,    right},
			{bottom, left },
			{bottom, col  },
			{bottom, right}
		};
		return Stream.of(neighborPoints)
				     .map(point -> point[0] * ncol + point[1])
				     .collect(Collectors.toCollection(HashSet::new));
	}
	
	/** count live neighbours */
	private int countLiveNeighbors(int index) {
		HashSet<Integer> neighbors = unbounded ?
			getEightNeighbors(index) : getNeighbors(index);
		return (int) neighbors.stream().filter(e -> isLive(e)).count();
	}
	
	/** The Game of Life Rules */
	private boolean isLiveInNextGen(int index) {
		switch (countLiveNeighbors(index)) {
			case  2: return isLive(index);
			case  3: return true;
			default: return false;
		}
	}
	
	/** compute the life state of each cell in next generation */
	public void computeNextGen() {
		indexes.stream().forEach(e -> nextGen[e] = isLiveInNextGen(e));
	}
	
	/** update trajectory */
	public void updateTrajGen() {
		indexes.stream().filter(e -> !trajGen[e] && isLive(e))
		                .forEach(e -> trajGen[e] = true);

	}
	
	/** update current generation */
	public void refreshCurrGen() {
		indexes.stream().forEach(e -> {
			if (nextGen[e]) {
				setLive(e);
			} else {
				if (showTraj && trajGen[e])
					setTraj(e);
				else
					setDead(e);
			}
		});
	}
	
	public void setLive(int index) {
		currGen[index].setFill(Color.BLACK);
	}
	
	public void setDead(int index) {
		currGen[index].setFill(Color.WHITE);
	}
	
	public void setTraj(int index) {
		currGen[index].setFill(Color.rgb(210, 210, 210));
	}
	
	public void setUnbounded(boolean unbounded) {
		this.unbounded = unbounded;
	}
	
	public void setShowTraj(boolean showTraj) {
		this.showTraj = showTraj;
	}
	
	public void incrementIterNum() {
		iterNum++;
	}
	
	public void resetMax() {
		cellMax = 0;
	}
	
	public void addToZeroGen(int element) {
		zeroGen.add((Integer) element);
	}
	
	public void removeFromZeroGen(int element) {
		zeroGen.remove((Integer) element);
	}
	
	/** ---------- 8 getters ---------- */
	
	public int getSize() {
		return nrow * ncol;
	}
	
	public boolean isLive(int index) {
		return currGen[index].getFill().equals(Color.BLACK);
	}
	
	public boolean isZeroGen() {
		return (iterNum == 0);
	}
	
	public int getIterNum() {
		return iterNum;
	}
	
	public int countInitCells() {
		return zeroGen.size();
	}
		
	public int countCurrCells() {
		return (int) indexes.stream().filter(e -> isLive(e)).count();
	}
	
	public int countMaxCells() {
		cellMax = (iterNum == 0) ? countInitCells() :
			Math.max(countCurrCells(), cellMax);
		return cellMax;
	}
	
	/** If the current generation generate exactly the same pattern for the 
	  *  following generations, return true. */
	public boolean isStill() {
		computeNextGen();
		return indexes.stream().allMatch(e -> isLive(e) == nextGen[e]);
	}

}