module tech.jaboc.jcom {
	requires javafx.controls;
	requires javafx.fxml;
	
	
	opens tech.jaboc.jcom to javafx.fxml;
	exports tech.jaboc.jcom;
}