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
    void initialize() {
        totalTimeOfWorkLabel.setText("Загальний час роботи ");
        timeOfCurrentSessionLabel.setText("Час у поточному сеансі ");
        mainWindowToggleButton.setSelected(true); // Щоб кнопка була нажата при ініціалізації


        ObservableList<String> items = FXCollections.observableArrayList("Chrome", "Discord", "Steam", "CsGo", "Zoom");

        //метод зчитає дані і помістить в статичний масив
        readTotalTimeOfWork(infoFile, items);
        for (int i = 0; i < totalTimeArr.size(); i++) {
            System.out.println(totalTimeArr.get(i));
        }

        listOfTrackedPrograms.setItems(items);

        MultipleSelectionModel<String> trackedProgramsSelModel = listOfTrackedPrograms.getSelectionModel();
        trackedProgramsSelModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                TrackedProgramNameLabel.setText("Програма: " + newValue);
                int i = trackedProgramsSelModel.getSelectedIndex();
                int a = timeOfAllPrograms.get(i) / 60;
                int b = totalTimeArr.get(i) / 60;
                updateTimeLabel(a, timeOfCurrentSessionLabel, "Час у поточному сеансі: ");
                updateTimeLabel(b, totalTimeOfWorkLabel, "Загальний час роботи: ");
                nameOfCurrProgram = newValue;
                alarmComboBox.setVisible(true);
            }
        });
        updateTimeOfAllPrograms(items.size());
        trackAll(items);
        //Кнопка обнвити, обновляє інфу з часом
        updateButton.setOnAction(e -> {
            int index = trackedProgramsSelModel.getSelectedIndex();
            updateTimeLabel(timeOfAllPrograms.get(index) / 60, timeOfCurrentSessionLabel, "Час у поточному сеансі: ");
            updateTimeLabel(totalTimeArr.get(index) / 60, totalTimeOfWorkLabel, "Загальний час роботи: ");
        });
        //Загатовка для вибору періоду
        ObservableList<String> elem = FXCollections.observableArrayList("Вимкнути сповіщення", "Кожні 15 хв" ,"Кожні 30 хв", "Щогодини", "Раз на три години", "Раз на добу");
        SingleSelectionModel<String> alarmComboBoxSelMod = alarmComboBox.getSelectionModel();
        alarmComboBox.setItems(elem);
        if (nameOfCurrProgram == ""){
            alarmComboBox.setVisible(false);
        }
        alarmComboBoxSelMod.select(1);
        alarmComboBox.setOnAction(e -> {
            int index = alarmComboBoxSelMod.getSelectedIndex();
            String str = alarmComboBoxSelMod.getSelectedItem();
            System.out.println(index + " " + str);
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

    void updateTimeOfAllPrograms(int size) {
        for (int i = timeOfAllPrograms.size(); i < size; i++) {
            timeOfAllPrograms.add(i, 0);
        }
    }

    /*void trackOne(String item) {
        updateTimeOfAllPrograms(timeOfAllPrograms.size());
        Timeline timeline;
        int[] time = new int[1];
        time[0] = 0;
        timeline = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> {
                    if (logic.isProcessAlive(item)) {
                        time[0] = timeOfAllPrograms.get(timeOfAllPrograms.size() - 1);
                        time[0]++;
                        timeOfAllPrograms.add(timeOfAllPrograms.size(), time[0]);
                    } else time[0] = 0;
                    System.out.println("Time of program " + item + "  " + timeOfAllPrograms.get(timeOfAllPrograms.size() - 1));
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }*/

    void trackAll(ObservableList<String> items) {
        Timeline timeline;
        int constantSize = items.size();
        int[] arr = new int[items.size()];
        int[] arr2 = new int[items.size()];
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
                                try {
                                    showNotification("Ви працюєте вже 15 хвилин в " + items.get(i));
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
    void showNotification(String text) throws AWTException, MalformedURLException {
        //Obtain only one instance of the SystemTray object
            SystemTray tray = SystemTray.getSystemTray();
            //If the icon is a file
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            //Alternative (if the icon is on the classpath):
            //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("src/sample/images/GearIconButton.png"));

            TrayIcon trayIcon = new TrayIcon(image, "Java AWT Tray Demo");
            //Let the system resize the image if needed
            trayIcon.setImageAutoSize(true);
            //Set tooltip text for the tray icon
            trayIcon.setToolTip("App Tracker");
            tray.add(trayIcon);
            trayIcon.displayMessage("App Tracker", text, TrayIcon.MessageType.INFO);
        }

    void updateTimeLabel(int a, Label label, String text) {
        if ((a == 0) || (a >= 5)){
            String str = text + a + " хвилин";
            label.setText(str);
        }
        else if (a == 1){
            String str = text + a + " хвилина";
            label.setText(str);
        }
        else if (a == 2 || a == 3 || a == 4){
            String str = text + a + " хвилини";
            label.setText(str);
        }
    }

    //метод справді працює непогано час виконання від 9 до 60 мілісекунд
    public void writeItemsInFileAndTime(File file, ObservableList<String> items, ArrayList<Integer> execTimeInSec) {
        try (FileWriter writer = new FileWriter(file, true)) {
            int[] totalTime = new int[items.size()];
            Scanner sc = new Scanner(file, StandardCharsets.UTF_8);
            int wordNumber = 0; //дорівнює кількості елементів, допомагає писати в масив
            Date date = new Date();

            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                if (str.contains("total time") && str.contains(items.get(wordNumber))) {
                    char[] arr = str.toCharArray();
                    for (int i = 0; i < arr.length - 1; i++) {
                        if (arr[i] == '_') {
                            int j = i + 1;
                            StringBuilder number = new StringBuilder();
                            while (arr[j] != '_') {
                                number.append(arr[j]);
                                j++;
                            }
                            totalTime[wordNumber] = Integer.parseInt(String.valueOf(number));
                        }
                    }
                    //метод не ідеальний він перепроходить весь файл
                    //але це іф дозволяє зчитувати лише останні дані
                    //плюс метод можна вважати історією роботи програми)
                    if (wordNumber == totalTime.length - 1)
                        wordNumber = 0;
                    else wordNumber++;
                }
            }
            //writer.append("\n" + "Останнє вибране значення alarmComboBox " + alarmComboBoxSelMod.getSelectedIndex() + alarmComboBoxSelMod.getSelectedItem(); + "\n");
            sc.close();

            writer.append("Востаннє програма відкривалась " + date + "\n");
            for (int i = 0; i < items.size(); i++) {
                int f = execTimeInSec.get(i) + totalTime[i];
                writer.append(items.get(i) + " execution time: " + execTimeInSec.get(i) + "\n");
                writer.append(items.get(i) + " total time_" + f + "_\n");
            }
            writer.append('\n');
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void readTotalTimeOfWork(File file, ObservableList<String> items) {
        int[] totalTime = new int[items.size()];
        Scanner sc = null;
        try {
            sc = new Scanner(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int wordNumber = 0; //дорівнює кількості елементів, допомагає писати в масив

        while (sc.hasNextLine()) {
            String str = sc.nextLine();
            if (str.contains("total time")) {
                char[] arr = str.toCharArray();
                for (int i = 0; i < arr.length - 1; i++) {
                    if (arr[i] == '_') {
                        int j = i + 1;
                        StringBuilder number = new StringBuilder();
                        while (arr[j] != '_') {
                            number.append(arr[j]);
                            j++;
                        }
                        totalTime[wordNumber] = Integer.parseInt(String.valueOf(number));
                    }
                }
                if (wordNumber == totalTime.length - 1)
                    wordNumber = 0;
                else wordNumber++;
            }
        }
        sc.close();
        for (int i = 0; i < totalTime.length; i++) {
            totalTimeArr.add(i, totalTime[i]);
            System.out.println("Index is " + i + "Value is " + totalTime[i]);
        }
    }
}


