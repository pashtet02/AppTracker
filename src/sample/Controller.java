package sample;
        //TODO
        /**1. можливість трекінга інших програм
        2. Добавити іконки на кнопки і тд
        4. Перемалювати картинку програми (сам інтерфейс)
        6. Додати кнопки "дадати" і "видалити" (це потрібно для того щоб включати виключати трекінг тої чи іншої проги)
         9. зробити рефакторинг всього
         10. Зробити jar файл
        Висновок: нам пздц, я сам навіть 4 з 8 не зможу зробити + мене вже заєбала ця робота))))*/

       /** ЗРОБЛЕНО І ПОТРІБНО Тестити 12.05
        3.систем трей
        7. добавив в popup menu можливість відключення сповіщень
        5. добавив функцію updatorOfLAbels яка кожні 20 сєк буде обновляти лейлби і писати туди часове значення
            яке є у вибраної програми (потріно затестить)
        8.Добавлена можливість вибору для окремої програми періоду сповіщень (15хв, 1год, 3 години)*/

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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

import static sample.Main.showNotification;


public class Controller {
    private Logic logic = new Logic();
    private File infoFile = new File("src/sample/info.txt");
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
    ComboBox<String> alarmComboBox;

    @FXML
    private Button AddToTrack;

    @FXML
    private Button dellFromTrack;

    ObservableList<String> items = FXCollections.observableArrayList(
            "Chrome", "Discord", "Steam", "CsGo", "Zoom");

    ObservableList<String> elem = FXCollections.observableArrayList(
            "Вимкнути сповіщення", "15 хв", "Щогодини", "Раз на три години");

    MultipleSelectionModel<String> trackedProgramsSelModel;


    //метод в якому задаються початкові параметри програми
    @FXML
    void initialize() {
        logic.readTotalTimeOfWork(infoFile, items);
        logic.readItemsFromFile(infoFile);

        SingleSelectionModel<String> alarmComboBoxSelMod = alarmComboBox.getSelectionModel();

        totalTimeOfWorkLabel.setText("Загальний час роботи ");
        timeOfCurrentSessionLabel.setText("Час у поточному сеансі ");

        listOfTrackedPrograms.setItems(items);
        trackedProgramsSelModel = listOfTrackedPrograms.getSelectionModel();
        trackedProgramsSelModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                TrackedProgramNameLabel.setText("Програма: " + newValue);
                int i = trackedProgramsSelModel.getSelectedIndex();
                int a = timeOfAllPrograms.get(i) / 60;
                int b = totalTimeArr.get(i) / 60;
                updateTimeLabel(a, timeOfCurrentSessionLabel, "Час у поточному сеансі: ");
                updateTimeLabel(b, totalTimeOfWorkLabel, "Загальний час роботи: ");
                updateCheckBox(alarmComboBoxSelMod);
                nameOfCurrProgram = newValue;
                alarmComboBox.setVisible(true);
            }
        });

        alarmComboBox.setItems(elem);
        if (nameOfCurrProgram.equals("")){
            alarmComboBox.setVisible(false);
        }

        alarmComboBox.setOnAction(e -> {
            int index = alarmComboBoxSelMod.getSelectedIndex();
            switch (index){
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

        //Пишемо всі інстальовані програми
        ArrayList<String> installedProgramsArr = null;
        try {
            installedProgramsArr = logic.getAllImportantPrograms();;
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObservableList<String> installedPrograms = FXCollections.observableArrayList(installedProgramsArr);
        installedProgramsListView.setItems(installedPrograms);
        MultipleSelectionModel<String> instProgramSelMod = installedProgramsListView.getSelectionModel();

        instProgramSelMod.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                if (!(newValue.equals(""))){
                    AddToTrack.setVisible(true);
                    dellFromTrack.setVisible(true);
                }
                nameOfChoicedProgram = instProgramSelMod.getSelectedItem();
            }
        });

       /* //Неробочий код на майбутнє
        AddToTrack.setVisible(false);
        dellFromTrack.setVisible(false);
        AddToTrack.setOnAction(e -> {
            System.out.println("Add button");
            int sizeOne = timeOfAllPrograms.size();
            int sizeTwo = totalTimeArr.size();
            items.add(nameOfChoicedProgram);
            trackOne(nameOfChoicedProgram, sizeOne, sizeTwo);
        });*/
    }

    //Декілька методів для коректної роботи програми


    //викликається при натисненні нового елемента з таба tracked programs
    private void updateCheckBox(SingleSelectionModel<String> alarmComboBoxSelMod){
        int indexOfSelectedItem = trackedProgramsSelModel.getSelectedIndex();
        int period = stepOfNotifications.get(indexOfSelectedItem);
        System.out.println("period is  "+period);
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
        Timeline timeline;
        Logic logic = new Logic();
        int[] currentTime = new int[1];
        int[] totalTime = new int[1];
        timeline = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> {
                    if(logic.isProcessAlive(nameOfProgram)) {
                        currentTime[0]++;
                        totalTime[0]++;
                        System.out.println("In track one process works "  + nameOfProgram + " curr time = " + currentTime[0]);
                        System.out.println("In track one process works " + nameOfProgram + " total time = " + totalTime[0]);
                    }
                    timeOfAllPrograms.add(indexOfCurr, currentTime[0]);
                    System.out.println(timeOfAllPrograms.get(indexOfCurr));
                    totalTimeArr.add(indexOfTotal, totalTime[0]);
                    System.out.println(totalTimeArr.get(indexOfTotal));
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    //таймер, який кожні 20с обновляє лейбли з часом
    private void updatorOfLabels(){
        Timeline timeline;
        timeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> {
            int index = trackedProgramsSelModel.getSelectedIndex();
            if (index >= 0){
                updateTimeLabel(timeOfAllPrograms.get(index) / 60, timeOfCurrentSessionLabel, "Час у поточному сеансі: ");
                updateTimeLabel(totalTimeArr.get(index) / 60, totalTimeOfWorkLabel, "Загальний час роботи: ");
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
        Main main = new Main();
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
                        timeOfAllPrograms.add(i, arr[i]);
                        totalTimeArr.add(i, arr2[i]);
                        //System.out.println("Index is " + i + "Value " + arr2[i]);
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void writeItemsInFile(File file) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.append("Items is: " + trackedProgramsSelModel.getSelectedItems() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void readItemsFromFile(File file, ObservableList<String> items){
        try{
            Scanner sc = new Scanner(file, StandardCharsets.UTF_8);
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                if (str.contains("Items is: ")){
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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


