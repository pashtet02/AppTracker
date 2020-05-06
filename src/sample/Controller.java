package sample;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller {
    private Logic logic = new Logic();
    private File infoFile = new File("src/sample/info.txt");
    static ArrayList<Integer> totalTimeArr = new ArrayList<>();
    static ArrayList<Integer> timeOfAllPrograms = new ArrayList<>();
    static ArrayList<Integer> stepOfNotifications = new ArrayList<>();

    public static ArrayList<Integer> getTimeOfAllPrograms() {
        return timeOfAllPrograms;
    }

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
    public ListView<String> listOfTrackedPrograms;

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
    ComboBox<String> alarmComboBox;
    String nameOfCurrProgram = "";

    @FXML
    PopupMenu popupMenu;

    @FXML
    void initialize() {
        totalTimeOfWorkLabel.setText("Загальний час роботи ");
        timeOfCurrentSessionLabel.setText("Час у поточному сеансі ");
        mainWindowToggleButton.setSelected(true); // Щоб кнопка була нажата при ініціалізації

        ObservableList<String> items = FXCollections.observableArrayList(
                "Chrome", "Discord", "Steam", "CsGo", "Zoom");

        //метод зчитає дані і помістить в статичний масив
        logic.readTotalTimeOfWork(infoFile, items);

        listOfTrackedPrograms.setItems(items);
        MultipleSelectionModel<String> trackedProgramsSelModel = listOfTrackedPrograms.getSelectionModel();
        trackedProgramsSelModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                TrackedProgramNameLabel.setText("Програма: " + newValue);
                int i = trackedProgramsSelModel.getSelectedIndex();
                int a = timeOfAllPrograms.get(i) / 60;
                int b = totalTimeArr.get(i) / 60;
                //stepOfNotifications.add(i, 1);
                updateTimeLabel(a, timeOfCurrentSessionLabel, "Час у поточному сеансі: ");
                updateTimeLabel(b, totalTimeOfWorkLabel, "Загальний час роботи: ");
                nameOfCurrProgram = newValue;
                alarmComboBox.setVisible(true);
            }
        });

        //Загатовка для вибору періоду
        ObservableList<String> elem = FXCollections.observableArrayList(
                "Вимкнути сповіщення", "15 хв" ,"30 хв", "Щогодини", "Раз на три години", "Раз на добу");
        SingleSelectionModel<String> alarmComboBoxSelMod = alarmComboBox.getSelectionModel();
        alarmComboBox.setItems(elem);
        if (nameOfCurrProgram.equals("")){
            alarmComboBox.setVisible(false);
        }
        alarmComboBoxSelMod.select(1);
        alarmComboBox.setOnAction(e -> {
            int index = alarmComboBoxSelMod.getSelectedIndex();
            String str = alarmComboBoxSelMod.getSelectedItem();
            System.out.println(index + " " + str);
        });

        updateTimeOfAllPrograms(items.size());
        trackAll(items);
        //Кнопка обнвити, обновляє інфу з часом
        updateButton.setOnAction(e -> {
            int index = trackedProgramsSelModel.getSelectedIndex();
            updateTimeLabel(timeOfAllPrograms.get(index) / 60, timeOfCurrentSessionLabel, "Час у поточному сеансі: ");
            updateTimeLabel(totalTimeArr.get(index) / 60, totalTimeOfWorkLabel, "Загальний час роботи: ");
        });

        System.out.println(alarmComboBoxSelMod.getSelectedIndex());
        ArrayList<String> installedPrograms = null;
        try {
            installedPrograms = logic.getAllImportantPrograms();
            installedPrograms.add("Teleram");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObservableList<String> ins = FXCollections.observableArrayList(installedPrograms);
        installedProgramsListView.setItems(ins);
        //MultipleSelectionModel<String> instProgramSelMod = installedProgramsListView.getSelectionModel();
        /*instProgramSelMod.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                int index = instProgramSelMod.getSelectedIndex();
                installedProgramsListView.getItems().remove(index);
                listOfTrackedPrograms.getItems().add(newValue);
            }
        });*/
    }

///////////////LOGIC
    void updateTimeOfAllPrograms(int size) {
        for (int i = timeOfAllPrograms.size(); i < size; i++) {
            timeOfAllPrograms.add(i, 0);
        }
    }

    void trackAll(ObservableList<String> items) {
        Timeline timeline;
        Logic logic = new Logic();
        int constantSize = items.size();
        int[] arr = new int[items.size()];
        int[] arr2 = new int[items.size()];
        int[] lol = new int[items.size()];
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = totalTimeArr.get(i);
        }
        timeline = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> {
                    for (int i = 0; i < constantSize; i++) {
                        if (logic.isProcessAlive(items.get(i))) {
                            arr[i]++;
                            arr2[i]++;
                            if (arr[i] % 900 == 0){
                                lol[i] += 15;
                                try {
                                    logic.showNotification("Ви працюєте вже " + lol[i] +" хвилин в " + items.get(i));
                                } catch (AWTException ex) {
                                    ex.printStackTrace();
                                } catch (MalformedURLException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        timeOfAllPrograms.add(i, arr[i]);
                        totalTimeArr.add(i, arr2[i]);
                        System.out.println("Index is " + i + "Value " + arr2[i]);
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    void updateTimeLabel(int a, Label label, String text) {
        if (a >= 60){
            int num = a/60;
            if ((a % 10 == 0) || (a % 10 >= 5)){
                String str = text + num + " год. " + a % 60 + " хвилин";
                label.setText(str);
            }
            else if (a % 10 == 1){
                String str = text + num + " год. " + a % 60 + " хвилина";
                label.setText(str);
            }
            else if ((a % 10 == 2) || (a % 10 == 3) || (a % 10 == 4)) {
                String str = text + num + " год. " + a % 60 + " хвилини";
                label.setText(str);
            }
        }
        else {
            if ((a % 10 == 0) || (a % 10 >= 5)){
                String str = text + a + " хвилин";
                label.setText(str);
            }
            else if (a % 10 == 1){
                String str = text + a + " хвилина";
                label.setText(str);
            }
            else if (a % 10 == 2 || a % 10 == 3 || a % 10 == 4){
                String str = text + a + " хвилини";
                label.setText(str);
            }
        }
    }
}


