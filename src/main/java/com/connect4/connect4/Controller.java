package com.connect4.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
	private static final int COLOUMS = 7;
	private static final int ROWS = 6;

	private static boolean isAllowed = true;
	private Disc[][] insertedDiscArray = new Disc[ROWS][COLOUMS]; // used to track structural changes in the grid.
	private static final double CIRCLE_DIAMETER = 80.0;
	private static final String DISC_1_COLOR = "#FF0E55";
	private static final String DISC_2_COLOR = "#272727";

	private static String player_one = "Red";
	private static String player_two = "Black";

	private boolean isPlayerOneTurn = true;

	@FXML
	public TextField playerOneName;

	@FXML
	public TextField playerTwoName;

	@FXML
	public Button setButton;

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Label playerNameLabel;

	// this function creates a rectangle with holes and adds it to out 'rootGridPane'.=================================
	public void createPlayGround()
	{
		playerNameLabel.setText(" "); // player name has to be empty when the game begins.
		Shape rectangleWithHoles = gameStructuralGrid();

		// this line adds 'rectangleWithHoles' to the rootGridPane on which the play ground is being made.
		rootGridPane.add(rectangleWithHoles,0,1);

		List<Rectangle> rectangleList = createClickAbleColumns();
		for(Rectangle rec : rectangleList) {
			rootGridPane.add(rec, 0, 1);
		}
	}

	// this function creates a rectangle with holes in it.=============================================================
	private Shape gameStructuralGrid()
	{
		/*
			'Rectangle' is constructor that takes 'Breadth' and 'Length' as parameter. It is completely filled rectangle.
		    the size of our rectangle must be a little bigger in order to accommodate padding and margin. */
		Shape rectangleWithHoles = new Rectangle((COLOUMS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);

		//we run the below 'for loops' to put holes in our rectangle.
		for(int i=0;i<ROWS;i++)
		{
			for(int j=0;j<COLOUMS;j++)
			{
				/* here we create 'circle' holes on the 'rectangle'.
				+20 -> adds margin to the top and left side of the playground.*/
				Circle circle = new Circle((CIRCLE_DIAMETER/2)+20,(CIRCLE_DIAMETER/2)+20,CIRCLE_DIAMETER/2);//creating circle (center-x,center-y,radius).

				/* this line creates holes in the same position.
				 hence we move the circle based on the value of 'i' and 'j'.*/
				circle.setTranslateX(j*(CIRCLE_DIAMETER+5));
				circle.setTranslateY(i*(CIRCLE_DIAMETER+5));
				circle.setSmooth(true);

				rectangleWithHoles = Shape.subtract(rectangleWithHoles,circle);
			}
		}

		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}

	// This method creates columns which can be clicked and inserts disc when clicked. ===============================
	private List<Rectangle> createClickAbleColumns()
	{
		//This list stores all the 'clickable-rectangles'. These are like 7 rectangles objects that responds to mouse events.
		List<Rectangle> list = new ArrayList<>();

		for(int i=0;i<COLOUMS;i++) {
			//we create a new rectangle for every column in out 'rectangleWithHoles'.
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(i*(CIRCLE_DIAMETER+5)+20);

			rectangle.setOnMouseEntered(event->rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event->rectangle.setFill(Color.TRANSPARENT));

			final int col = i;
			rectangle.setOnMouseClicked(event ->
			{
				if(isAllowed)// while the animation is running players are not allowed to insert disc.
				{
					isAllowed = false;
					insertDisc(new Disc(isPlayerOneTurn), col);
				}
			});

			list.add(rectangle);
		}

		return list;
	}

	// this function inserts discs into both 'structural'->SG and visual 'play-board'->PG.============================
	private void insertDisc(Disc disc, int col)
	{
		// we use this 'row' to update both the grids (SG,PG).
		int row = ROWS-1;
		while(row>=0 && getDiscIfPresent(row,col)!=null)
		{
			row--;
		}
		// if the whole row is filled then just return.
		if(row<0)
		{
			return;
		}

		// insert the disc object into 'insertedDiscArray'.
		insertedDiscArray[row][col] = disc;
		disc.setTranslateX(col*(CIRCLE_DIAMETER+5)+20);// inserting the disc at the specified row.

		// translate animation. disc moving to the topmost
		TranslateTransition ts = new TranslateTransition(Duration.seconds(0.5),disc);
		ts.setToY(row*(CIRCLE_DIAMETER+5)+20);

		// check if the game will end on adding this disc.
		final int cur_row = row;
		ts.setOnFinished(actionEvent ->
		{
			isAllowed = true; // when the animation is finished player is allowed to insert disc.
			if(gameEnded(cur_row,col)) {
				gameOver();
				return;
			}
			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn ? player_one+"'s" : player_two+"'s");
		});
		ts.play();
		insertedDiscsPane.getChildren().add(disc);
	}

	// this method declares the winner and ends the game.=============================================================
	private void gameOver()
	{
		String winner = isPlayerOneTurn? player_one : player_two;

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect4");
		alert.setHeaderText("Winner is "+winner);
		alert.setContentText("Play Again?");

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No");

		alert.getButtonTypes().setAll(yesBtn,noBtn);
		Platform.runLater(()->
		{
			Optional<ButtonType> optional = alert.showAndWait();

			if(optional.isPresent() && optional.get()==yesBtn)
			{
				resetGame();
			}
			else
			{
				Platform.exit();
				System.exit(0);
			}
		});

	}

	public void resetGame()
	{
		insertedDiscsPane.getChildren().clear();

		for(int i=0;i<ROWS;i++)
		{
			for(int j=0;j<COLOUMS;j++)
			{
				insertedDiscArray[i][j]=null;
			}
		}

		isPlayerOneTurn = true;
		playerNameLabel.setText(player_one);
		createPlayGround();
	}

	// this method checks if the game has reached winning criteria(i.e. if the combination was made).=================
	private boolean gameEnded(int cur_row, int col)
	{
		// we get the set of positions where the 'disc' can be placed in the mentioned 'col'.
		List<Point2D> verticalPoints = new ArrayList<>();// set of diagonal winning coordinates ->vertical.
		for(int i=cur_row-3;i<=cur_row+3;i++)
		{
			verticalPoints.add(new Point2D(i,col));
		}

		List<Point2D> horizontalPoints = new ArrayList<>();// set of diagonal winning coordinates ->horizontal.
		for(int i=col-3;i<=col+3;i++)
		{
			horizontalPoints.add(new Point2D(cur_row,i));
		}

		List<Point2D> diagonal_1_points = new ArrayList<>();// set of diagonal winning coordinates ->right side diagonal.
		for(int i=0;i<=3;i++)
		{
			diagonal_1_points.add(new Point2D(cur_row-i,col+i));
		}
		for(int i=0;i<=3;i++)
		{
			diagonal_1_points.add(new Point2D(cur_row+i,col-i));
		}

		List<Point2D> diagonal_2_points = new ArrayList<>();// set of diagonal winning coordinates ->left side diagonal.
		for(int i=0;i<=3;i++)
		{
			diagonal_2_points.add(new Point2D(cur_row-i,col-i));
		}
		for(int i=0;i<=3;i++)
		{
			diagonal_2_points.add(new Point2D(cur_row+i,col+i));
		}

		return checkCombinations(verticalPoints)
							|| checkCombinations(horizontalPoints)
							|| checkCombinations(diagonal_2_points)
							|| checkCombinations(diagonal_1_points);
	}

	/* this method takes a set which contains the winning coordinates for a specific disc position and checks if ======
		they are filled, and they are discs of the same player.
	*/
	private boolean checkCombinations(List<Point2D> points)
	{
		/* we take the set of possible combination set and check if we have a
		  valid combination there.*/
		int chain=0;
		for(Point2D point : points)
		{
			int r_index = (int)point.getX();
			int c_index = (int)point.getY();

			Disc disc = getDiscIfPresent(r_index,c_index);

			if(disc != null && disc.isPlayerOne == isPlayerOneTurn)
			{
				chain++;
				if(chain == 4)
				{
					return true;
				}
			}
			else
			{
				chain=0;
			}

		}
		return false;
	}

	// this method returns a disc if the 'row' and 'col' values are within the board.==================================
	private Disc getDiscIfPresent(int row, int col)
	{
		if(row>=ROWS || row<0 || col>=COLOUMS || col<0)
		{
			return null;
		}
		return insertedDiscArray[row][col];
	}

	private static class Disc extends Circle
	{
		private final boolean isPlayerOne;

		public Disc(boolean isPlayerOne)
		{
			this.isPlayerOne = isPlayerOne;
			setRadius(CIRCLE_DIAMETER/2);

			String val;
			if(isPlayerOne)
			{
				val=DISC_1_COLOR;
			}
			else
			{
				val=DISC_2_COLOR;
			}
			setFill(Color.valueOf(val));

			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle)
	{
		setButton.setOnAction(actionEvent -> setNames());
	}

	private void setNames()
	{
		player_one = playerOneName.getText();
		player_two = playerTwoName.getText();
		playerNameLabel.setText(player_one+"'s");
	}
}
