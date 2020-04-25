package sample;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller {

    Logic logic = new Logic();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane logicPane;

    @FXML
    private ToggleButton mainWindowToggleButton;

    @FXML
    private ListView<String> installedProgramsListView;

    @FXML
    private ToggleButton statsToggleButton;

    @FXML
    private ToggleButton settingsToggleButton;

    @FXML
    private ListView<String> listOfTrackedPrograms;

    @FXML
    private Text headerText;

    @FXML
    private Label TrackedProgramNameLabel;

    @FXML
    private Label timeOfCurrentSessionLabel;

    @FXML
    private Label totalTimeOfWorkLabel;

    @FXML
    void initialize() throws IOException {

        ObservableList<String> items = FXCollections.observableArrayList ("Chrome", "Discord", "Steam", "CsGo", "Zoom");
        listOfTrackedPrograms.setItems(items);
        MultipleSelectionModel<String> trackedProgramsSelModel = listOfTrackedPrograms.getSelectionModel();
        trackedProgramsSelModel.selectedItemProperty().addListener(new ChangeListener<String>(){
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                TrackedProgramNameLabel.setText("Програма: " + newValue);
                System.out.println("Old value: " + oldValue);
                System.out.println("New value: " + newValue);
                Timeline timeline;
                String str = "Час у поточночному сеансі: ";
                AtomicInteger time = new AtomicInteger(0);
                timeline = new Timeline(
                        new KeyFrame(Duration.millis(1000), e -> {
                            if (logic.isProcessAlive(newValue)) {
                                time.getAndIncrement();
                                System.out.println("Time = " + time);
                                timeOfCurrentSessionLabel.setText(str + new SimpleDateFormat("mm:ss")
                                        .format(new Date(TimeUnit.SECONDS.toMillis(time.get()))));
                                totalTimeOfWorkLabel.setText(str + new SimpleDateFormat("mm:ss")
                                        .format(new Date(TimeUnit.SECONDS.toMillis(time.get()))));
                            } else {
                                System.out.println("Час виконання програми: " + newValue + new SimpleDateFormat(" mm:ss ")
                                        .format(new Date(TimeUnit.SECONDS.toMillis(time.get()))));
                            }
                        })
                );
                timeline.setCycleCount(Timeline.INDEFINITE);
                if (logic.isProcessAlive(newValue)){
                    timeline.play();
                }
                else{
                    timeline.stop();
                }
            }
        });
        ArrayList<String > installedPrograms = logic.getAllImportantPrograms();
        ObservableList<String> ins = FXCollections.observableArrayList (installedPrograms);
        installedProgramsListView.setItems(ins);
    }
}


