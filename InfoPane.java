package gameOfLife;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * This class extends HBox, containing 8 StackPanes,
 * which display 4 groups of variable:
 * 	 1. Generation:
 *   2. Initial Live Cells:
 *   3. Current Live Cells:
 *   4. Max Live Cells:
 */
class InfoPane extends HBox {
	
	private int size;
	private Label[] labels;
	private Text[] texts;
	private StackPane[] stats;
	
	/** constructor */
	public InfoPane(int num) {
		size = num;
		labels = new Label[size];
		texts = new Text[size];
		stats = new StackPane[size * 2];
		
		loadLabels();
		loadTexts();
		loadStats();
		this.getChildren().addAll(stats);
	}
	
	/** create and load labels */
	private void loadLabels() {
		for (int i = 0; i < labels.length; ++i) {
			labels[i] = new Label();
			labels[i].setFont(Font.font("", FontWeight.BOLD, 12));
		}
	}
	
	/** create and load texts */
	private void loadTexts() {
		for (int i = 0; i < texts.length; ++i) {
			texts[i] = new Text("0");
			texts[i].setFont(Font.font("", FontWeight.BOLD, 12));
			texts[i].setFill(Color.RED);
		}
		texts[0].setFill(Color.BLUE);
	}
	
	/** create and load stats */
	private void loadStats() {
		for (int i = 0; i < stats.length; i += 2) {
			stats[i] = new StackPane();
			stats[i].getChildren().add(labels[i/2]);
			stats[i].setAlignment(Pos.CENTER_RIGHT);
		}
		for (int i = 1; i < stats.length; i += 2) {
			stats[i] = new StackPane();
			stats[i].getChildren().add(texts[(i - 1)/2]);
			stats[i].setAlignment(Pos.CENTER_LEFT);
		}
	}
	
	/** set variable names */
	public void setLabels(String[] names) {
		for (int i = 0; i < labels.length; ++i) {
			labels[i].setText(names[i]);
		}
	}
	
	/** set the width of each stack panes */
	public void setWidth(double[] width) {
		for (int i = 0; i < stats.length; ++i) {
			stats[i].setPrefWidth(width[i]);
		}
	}
	
	/** display values */
	public void updateText(int[] vals) {
		for (int i = 0; i < texts.length; ++i) {
			texts[i].setText("" + vals[i]);
		}
	}
	
}

