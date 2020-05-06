package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;



public class Main extends Application {
    Controller controller = new Controller();
    @Override
    public void stop() throws Exception {
        super.stop();
        File file = new File("src/sample/info.txt");
        ObservableList<String> items = FXCollections.observableArrayList("Chrome", "Discord", "Steam", "CsGo", "Zoom");
        long start = System.currentTimeMillis();
        controller.writeItemsInFileAndTime(file, items, Controller.getTimeOfAllPrograms());
        long pass = System.currentTimeMillis();
        long ff = pass - start;
        System.out.println("Execution of method is " + ff);


    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Tracker");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}