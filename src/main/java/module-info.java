module tech.jaboc.jcom {
	requires javafx.controls;
	requires javafx.fxml;
	requires org.jetbrains.annotations;
	
	
	opens tech.jaboc.jcom to javafx.fxml;
	exports tech.jaboc.jcom;
}