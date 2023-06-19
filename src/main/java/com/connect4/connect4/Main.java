package com.connect4.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application
{
	private Controller controller;

	@Override
	public void start(@SuppressWarnings("exports") Stage stage) throws Exception
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("activity.fxml"));
		GridPane rootGridPane = loader.load();

		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(stage.widthProperty());
		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);

		controller = loader.getController();
		controller.createPlayGround();

		Scene scene = new Scene(rootGridPane);
		stage.setScene(scene);
		stage.setTitle("Connect4");
		stage.setResizable(false);
		stage.show();

	}

	private MenuBar createMenu()
	{
		//file option.
		Menu fileMenu = new Menu("File");

		MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction(event -> controller.resetGame());

		SeparatorMenuItem sp = new SeparatorMenuItem();

		MenuItem exitGame = new MenuItem("Exit Game");
		exitGame.setOnAction(event->exitGame());

		fileMenu.getItems().addAll(newGame,sp,exitGame);

		//help option.
		Menu helpMenu = new Menu("Help");

		MenuItem about = new MenuItem("About Game");
		about.setOnAction(event->showAbout());

		MenuItem credit = new MenuItem("Credits");
		credit.setOnAction(event->showCredit());

		SeparatorMenuItem sp1 = new SeparatorMenuItem();
		MenuItem howToPlay = new MenuItem("How to play");
		howToPlay.setOnAction(event->helpMenu());

		helpMenu.getItems().addAll(about,credit,sp1,howToPlay);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu,helpMenu);

		return menuBar;
	}

	private void helpMenu()
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect4");
		alert.setHeaderText("How to play");
		alert.setContentText("""
				The pieces fall straight down, occupying the next available space
				within the column. The objective of the game is to be the first to form a horizontal,\s
				vertical, or diagonal line of four of one's own discs. Connect Four is a solved game.\s
				The first player can always win by playing the right moves.""");
		alert.setHeight(500);
		alert.show();
	}

	private void showCredit()
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Credits");
		alert.setHeaderText("Developed by");
		alert.setContentText("Kailas Nath S, as a part of learning javafx.");
		alert.show();
	}

	private void showAbout()
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect4");
		alert.setHeaderText("About");
		alert.setContentText("""
				Connect Four is a two-player connection rack game, in which the players\s
				choose a color and then take turns dropping colored tokens into a six-row,\s
				seven-column vertically suspended grid. The pieces fall straight down, occupying\s
				the lowest available space within the column. The objective of the game is to be the\s
				first to form a horizontal, vertical, or diagonal line of four of one's own tokens. Connect\s
				Four is a solved game.""");
		alert.setHeight(500);
		alert.show();
	}

	private void exitGame()
	{
		Platform.exit();
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
