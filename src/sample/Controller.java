package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static sample.Main.showNotification;

public class Controller {
    private static Logic logic = new Logic();
    private static File infoFile = new File("src/sample/info.txt");
    private final int FifteenMinutes = 900;
    private final int OneHour = 3600;
    private final int ThreeHours = 10800;
    private String nameOfCurrProgram = "";

    public static ArrayList<Integer> totalTimeArr = new ArrayList<>(); //масив з загальним часом роботи всіх програм
    public static ArrayList<Integer> timeOfAllPrograms = new ArrayList<>(); //масив з поточним часом роботи програм
    public static ArrayList<Integer> stepOfNotifications = new ArrayList<>(); //масив з періодом сповіщень для кожної програми
    private static String nameOfChoicedProgram = "";

    public static ArrayList<Integer> getStepOfNotifications() {
        return stepOfNotifications;
    }

    public static ArrayList<Integer> getTimeOfAllPrograms() {
        return timeOfAllPrograms;
    }

    public static ArrayList<Integer> getTotalTimeArr() {
        return totalTimeArr;
    }
    public static ObservableList<String> items = FXCollections.observableArrayList(logic.readItems(infoFile));
    public static ObservableList<String> getItems() {
        return items;
    }


    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane logicPane;

    @FXML
    private ToggleButton homeButton;

    @FXML
    private ToggleButton settingsButton;

    @FXML
    private ToggleButton statisticsButton;

    @FXML
    private AnchorPane settingsPane;

    @FXML
    private AnchorPane statisticsPane;

    @FXML
    private AnchorPane homePane;

    @FXML
    private SplitPane homeSplitPane;

    @FXML
    private AnchorPane listAnchorPane;

    @FXML
    private ToggleButton installedProgramsButton;

    @FXML
    private ToggleButton trackedProcessesButton;

    @FXML
    private ListView<String> listOfTrackedPrograms;

    @FXML
    private ListView<String> listOfInstalledPrograms;

    @FXML
    private Button addToListButton;

    @FXML
    private AnchorPane infoMainPane;

    @FXML
    private AnchorPane infoTrackPane;

    @FXML
    private Label infoName;

    @FXML
    private Label infoCurrentTime;

    @FXML
    private Label infoAllTime;

    @FXML
    private Label infoPeriodLabel;

    @FXML
    private ComboBox<String> infoPeriodComboBox;

    @FXML
    private Text headerText;

    ObservableList<String> elem = FXCollections.observableArrayList(
            "Вимкнено", "15 хв.", "1 год.", "3 год.");
    MultipleSelectionModel<String> trackedProgramsSelModel;

    @FXML
    void initialize() {
        listOfInstalledPrograms.setStyle("-fx-control-inner-background: #313335;");
        listOfTrackedPrograms.setStyle("-fx-control-inner-background: #313335;");
        infoPeriodComboBox.setStyle("-fx-control-inner-background: #313335;" + infoPeriodComboBox.getStyle());
        homeSplitPane.setDividerPositions(1);
        homeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                homePane.toFront();
                statisticsButton.setSelected(false);
                settingsButton.setSelected(false);
                homeButton.setDisable(true);
                statisticsButton.setDisable(false);
                settingsButton.setDisable(false);
            }
        });

        homeSplitPane.getDividers().get(0).positionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (homeSplitPane.getDividerPositions()[0] < 0.4971)
                    homeSplitPane.setDividerPositions(0.4971);
            }
        });
        trackedProcessesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                listOfTrackedPrograms.toFront();
                trackedProcessesButton.setSelected(true);
                trackedProcessesButton.setBackground(installedProgramsButton.getBackground());
                trackedProcessesButton.setDisable(true);
                installedProgramsButton.setSelected(false);
                installedProgramsButton.setDisable(false);
                homeSplitPane.setDividerPositions(0.4971);
            }
        });
        installedProgramsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                listOfInstalledPrograms.toFront();
                installedProgramsButton.setSelected(true);
                installedProgramsButton.setBackground(trackedProcessesButton.getBackground());
                installedProgramsButton.setDisable(true);
                trackedProcessesButton.setSelected(false);
                trackedProcessesButton.setDisable(false);
                homeSplitPane.setDividerPositions(1);
                addToListButton.toFront();
            }
        });
        statisticsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                statisticsPane.toFront();
                homeButton.setSelected(false);
                settingsButton.setSelected(false);
                statisticsButton.setDisable(true);
                homeButton.setDisable(false);
                settingsButton.setDisable(false);
                logic.chartCreator(new PieChart(), statisticsPane, items, totalTimeArr);
            }
        });
        settingsButton.setOnAction( e ->{
            showAlertWithoutHeaderText("Меню налаштувань");
            settingsButton.setSelected(false);
        });
        ////////////////////////////////////////////////////////////////////////
        logic.readTotalTimeOfWork(infoFile, items);
        SingleSelectionModel<String> infoPeriodComboBoxSelMod = infoPeriodComboBox.getSelectionModel();

        infoAllTime.setText("Загальний час роботи ");
        infoCurrentTime.setText("Час у поточному сеансі ");

        listOfTrackedPrograms.setItems(items);
        trackedProgramsSelModel = listOfTrackedPrograms.getSelectionModel();
        trackedProgramsSelModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                infoName.setText("Програма: " + newValue);
                int i = trackedProgramsSelModel.getSelectedIndex();
                int a = timeOfAllPrograms.get(i) / 60;
                int b = totalTimeArr.get(i) / 60;
                updateTimeLabel(a, infoCurrentTime, "Час у поточному сеансі: ");
                updateTimeLabel(b, infoAllTime, "Загальний час роботи: ");
                updateCheckBox(infoPeriodComboBoxSelMod);
                nameOfCurrProgram = newValue;
                infoPeriodComboBox.setVisible(true);
                System.out.println(timeOfAllPrograms);
                System.out.println(totalTimeArr);
            }
        });


        infoPeriodComboBox.setItems(elem);
        if (nameOfCurrProgram.equals("")){
            infoPeriodComboBox.setVisible(false);
        }

        infoPeriodComboBox.setOnAction(e -> {
            int index = infoPeriodComboBoxSelMod.getSelectedIndex();
            switch (index){
                case 0:
                    Main.notificationsOn = false;
                    break;
                case 1:
                    stepOfNotifications.set(trackedProgramsSelModel.getSelectedIndex(), FifteenMinutes);
                    System.out.println("Index is " + trackedProgramsSelModel.getSelectedIndex() + " 900 " + index);
                    break;
                case 2:
                    stepOfNotifications.set(trackedProgramsSelModel.getSelectedIndex(), OneHour);
                    System.out.println("Index is " + trackedProgramsSelModel.getSelectedIndex() + " 3600");
                    break;
                case 3:
                    stepOfNotifications.set(trackedProgramsSelModel.getSelectedIndex(), ThreeHours);
                    System.out.println("Index is " + trackedProgramsSelModel.getSelectedIndex() + " 10800");
                    break;
                default:
                    System.out.println("Було натиснено вимкнути сповіщення");
                    break;
            }
        });
        //Запуск таймерів
        updateTimeOfAllPrograms(items.size());
        trackAll(items);
        updatorOfLabels();

        //TODO
        //Забрати повторення програм
        ArrayList<String> installedProgramsArr = null;
        try {
            installedProgramsArr = logic.getAllImportantPrograms(items);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("arr size " + installedProgramsArr.size());
        ObservableList<String> installedPrograms = FXCollections.observableArrayList(installedProgramsArr);
        listOfInstalledPrograms.setItems(installedPrograms);
        MultipleSelectionModel<String> instProgramSelMod = listOfInstalledPrograms.getSelectionModel();

        instProgramSelMod.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                if (!(newValue.equals(""))){
                    addToListButton.setVisible(true);
                }
                nameOfChoicedProgram = instProgramSelMod.getSelectedItem();
            }
        });

        addToListButton.setVisible(false);
        addToListButton.setOnAction(e -> {
            System.out.println("Add button");
            int index = instProgramSelMod.getSelectedIndex();
            int sizeOne = timeOfAllPrograms.size();
            int sizeTwo = totalTimeArr.size();
            items.add(nameOfChoicedProgram);
            trackOne(nameOfChoicedProgram, sizeOne, sizeTwo);
            System.out.println("Name of program_" + nameOfChoicedProgram + "_");
            installedPrograms.remove(index);
            listOfInstalledPrograms.refresh();
            System.out.println(items);
        });
    }

    //викликається при натисненні нового елемента з таба tracked programs
    private void updateCheckBox(SingleSelectionModel<String> alarmComboBoxSelMod){
        int indexOfSelectedItem = trackedProgramsSelModel.getSelectedIndex();
        int period = stepOfNotifications.get(indexOfSelectedItem);
        switch (period){
            case 900:
                alarmComboBoxSelMod.select(1);
                break;
            case 3600:
                alarmComboBoxSelMod.select(2);
                break;
            case 10800:
                alarmComboBoxSelMod.select(3);
                break;
            default:
                System.out.println("Шось не то");
        }
    }

    void trackOne(String nameOfProgram, int indexOfCurr, int indexOfTotal){
        timeOfAllPrograms.add(indexOfCurr, 0);
        totalTimeArr.add(indexOfTotal, 0);
        stepOfNotifications.add(indexOfCurr, OneHour);
        Timeline timeline;
        int[] lol = {0};
        Logic logic = new Logic();
        int[] currentTime = new int[1];
        int[] totalTime = new int[1];
        timeline = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> {
                    if(logic.isProcessAlive(nameOfProgram.substring(0, nameOfProgram.length()-1))) {
                        currentTime[0]++;
                        totalTime[0]++;
                        System.out.println("In track one process works "  + nameOfProgram + " curr time = " + currentTime[0]);
                        System.out.println("In track one process works " + nameOfProgram + " total time = " + totalTime[0]);
                        if (currentTime[0] % stepOfNotifications.get(indexOfCurr) == 0){
                            lol[0] += stepOfNotifications.get(indexOfCurr) / 60;
                            showNotification("Ви працюєте вже " + lol[0] +" хвилин в " + items.get(indexOfCurr), Main.trayIcon);
                        }
                    }
                    timeOfAllPrograms.set(indexOfCurr, currentTime[0]);
                    totalTimeArr.set(indexOfTotal, totalTime[0]);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    //таймер, який кожні 20с обновляє лейбли з часом
    private void updatorOfLabels(){
        Timeline timeline;
        timeline = new Timeline(new KeyFrame(Duration.seconds(20), e -> {
            int index = trackedProgramsSelModel.getSelectedIndex();
            if (index >= 0){
                updateTimeLabel(timeOfAllPrograms.get(index) / 60, infoCurrentTime, "Час у поточному сеансі: ");
                updateTimeLabel(totalTimeArr.get(index) / 60, infoAllTime, "Загальний час роботи: ");
                System.out.println("Updator worked");
            }
            else {
                System.out.println("ще нічого не вибрано");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimeOfAllPrograms(int size) {
        for (int i = timeOfAllPrograms.size(); i < size; i++) {
            timeOfAllPrograms.add(i, 0);
        }
    }

    private void trackAll(ObservableList<String> items) {
        Timeline timeline;
        Logic logic = new Logic();
        int constantSize = items.size();
        int[] arr = new int[items.size()];
        int[] arr2 = new int[items.size()];
        int[] lol = new int[items.size()];
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = totalTimeArr.get(i);
        }
        timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
            for (int i = 0; i < constantSize; i++) {
                if (logic.isProcessAlive(items.get(i))) {
                    arr[i]++;
                    arr2[i]++;
                    if (arr[i] % stepOfNotifications.get(i) == 0){
                        lol[i] += stepOfNotifications.get(i) / 60;
                        showNotification("Ви працюєте вже " + lol[i] +" хвилин в " + items.get(i), Main.trayIcon);
                    }
                }
                timeOfAllPrograms.set(i, arr[i]);
                totalTimeArr.set(i, arr2[i]);
            }
        })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimeLabel(int a, Label label, String text) {
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
            else if(a == 11 || a == 12 || a== 13 || a==14){
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
    private void showAlertWithoutHeaderText(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(text);
        alert.setHeaderText(null);
        alert.setContentText("Вибачте, ця функція ще не реалізована! :(");
        alert.showAndWait();
    }
}
