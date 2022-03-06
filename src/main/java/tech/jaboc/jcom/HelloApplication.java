package tech.jaboc.jcom;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class HelloApplication extends Application {
	ResizableCanvas gameCanvas;
	
	
	@Override
	public void start(Stage stage) throws IOException {
		StackPane basePane = new StackPane();
		
		Scene scene = new Scene(basePane);
		
		// Canvas
		
		ResizableCanvas canvas = new ResizableCanvas();
		gameCanvas = canvas;
		basePane.getChildren().add(canvas);
		
		canvas.widthProperty().bind(basePane.widthProperty());
		canvas.heightProperty().bind(basePane.heightProperty());
		
		// BorderLayout
		
		BorderPane borderPane = new BorderPane();
		basePane.getChildren().add(borderPane);
		
		borderPane.prefWidthProperty().bind(basePane.widthProperty());
		borderPane.prefHeightProperty().bind(basePane.heightProperty());
		
		// Bottom Panel
		
		Rectangle rect = new Rectangle(100, 100, Color.HOTPINK);
		HBox hbox = new HBox();
		hbox.prefHeightProperty().bind(borderPane.prefHeightProperty().multiply(0.15));
		hbox.minHeightProperty().bind(borderPane.prefHeightProperty().multiply(0.15));
		hbox.maxHeightProperty().bind(borderPane.prefHeightProperty().multiply(0.15));
		hbox.getChildren().add(rect);
		hbox.setAlignment(Pos.BOTTOM_CENTER);
		rect.setFocusTraversable(true);
		rect.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
			if (rect.isHover()) {
				rect.requestFocus();
				System.out.println("Selected");
			}
		});
		rect.widthProperty().bind(hbox.widthProperty());
		rect.heightProperty().bind(hbox.heightProperty());
		
		borderPane.setBottom(hbox);
		
		stage.setTitle("Hello!");
		stage.setScene(scene);
		stage.show();
		
		startGameLoop();
	}
	
	private void startGameLoop() {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(1.0 / 60.0), actionEvent -> {
			internalDraw();
		});
		timeline.getKeyFrames().add(keyFrame);
		
		lastNanos = System.nanoTime();
		
		timeline.playFromStart();
	}
	
	long lastNanos = 0;
	
	private void internalDraw() {
		GraphicsContext g = gameCanvas.getGraphicsContext2D();
		double width = gameCanvas.getWidth();
		double height = gameCanvas.getHeight();
		
		double deltaTime = (System.nanoTime() - lastNanos) / 1_000_000_000.0;
		lastNanos = System.nanoTime();
		g.clearRect(0, 0, width, height);
		
		g.setStroke(Color.RED);
		g.strokeLine(0, 0, width, height);
		g.strokeLine(0, height, width, 0);
		
		g.setTextBaseline(VPos.TOP);
		g.fillText(String.valueOf(deltaTime), 0, 0);
	}
	
	public static void main(String[] args) {
		launch();
	}
	
	// https://dlemmermann.wordpress.com/2014/04/10/javafx-tip-1-resizable-canvas/
	static class ResizableCanvas extends Canvas {
		public ResizableCanvas() {
			// Redraw canvas when size changes.
//			widthProperty().addListener(evt -> draw());
//			heightProperty().addListener(evt -> draw());
		}
		
		private void draw() {
//			double width = getWidth();
//			double height = getHeight();
			
		
		}
		
		@Override
		public boolean isResizable() {
			return true;
		}
		
		@Override
		public double prefWidth(double height) {
			return getWidth();
		}
		
		@Override
		public double prefHeight(double width) {
			return getHeight();
		}
	}
}