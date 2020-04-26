package sample;

import java.io.IOException;
import java.net.URL;
import java.util.*;



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
    static ArrayList<Integer> timeOfAllPrograms = new ArrayList<>();

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
    private Button updateButton;

    @FXML
    void initialize(){

        mainWindowToggleButton.setSelected(true); // Щоб кнопка була нажата при ініціалізації

        ObservableList<String> items = FXCollections.observableArrayList("Chrome", "Discord", "Steam", "CsGo", "Zoom");
        listOfTrackedPrograms.setItems(items);
        MultipleSelectionModel<String> trackedProgramsSelModel = listOfTrackedPrograms.getSelectionModel();
        trackedProgramsSelModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                TrackedProgramNameLabel.setText("Програма: " + newValue);
                int i = trackedProgramsSelModel.getSelectedIndex();
                int a = timeOfAllPrograms.get(i) / 60;
                updateTimeOfCurrentSessionLabel(a);

            }
        });
        resetTimeOfAllPrograms(items.size());
        trackAll(items);
        //Кнопка обнвити, обновляє інфу з часом
        updateButton.setOnAction(e -> {
            int index = trackedProgramsSelModel.getSelectedIndex();
            updateTimeOfCurrentSessionLabel(timeOfAllPrograms.get(index) / 60);
        });


        ArrayList<String> installedPrograms = null;
        try {
            installedPrograms = logic.getAllImportantPrograms();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObservableList<String> ins = FXCollections.observableArrayList(installedPrograms);
        installedProgramsListView.setItems(ins);
        MultipleSelectionModel<String> instProgramSelMod = installedProgramsListView.getSelectionModel();
        instProgramSelMod.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                System.out.println("Chosen value is: " + newValue);
                installedProgramsListView.getItems().remove(newValue);
                listOfTrackedPrograms.getItems().add(newValue);
                System.out.println(listOfTrackedPrograms.getItems());


            }
        });
    }
    //При початку роботи всі елементи будуть = 0, щоб не було ексепшинів
    void resetTimeOfAllPrograms(int size){
        for (int i = 0; i < size; i++) {
            timeOfAllPrograms.add(i, 0);
        }
    }

    void trackAll(ObservableList<String> items) {
        Timeline timeline;
        int[] arr = new int[items.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = 0;
        }
        timeline = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> {
                    for (int i = 0; i < items.size(); i++) {
                        if (logic.isProcessAlive(items.get(i))) {
                            arr[i]++;
                        }
                        timeOfAllPrograms.add(i, arr[i]);
                        System.out.println("Time of program " + items.get(i)+ "  " + timeOfAllPrograms.get(i));
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    //Метод лише для того, щоб обновляти лейбл timeOfCurrentSessionLabel
    void updateTimeOfCurrentSessionLabel(int a){
        if ((a == 0) || (a >= 5))
            timeOfCurrentSessionLabel.setText("Час у поточному сеансі " + a + " хвилин");
        else if (a == 1)
            timeOfCurrentSessionLabel.setText("Час у поточному сеансі " + a + " хвилина");
        else if (a==2 || a==3 || a==4)
            timeOfCurrentSessionLabel.setText("Час у поточному сеансі " + a + " хвилини");
    }
}
