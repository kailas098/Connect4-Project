module com.connect4.connect4 {
	requires javafx.controls;
	requires javafx.fxml;


	opens com.connect4.connect4 to javafx.fxml;
	exports com.connect4.connect4;
}