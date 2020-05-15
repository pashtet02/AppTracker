package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

public class Main extends Application{

    private Logic logic = new Logic();
    private Stage primaryStage = new Stage();
    private File file = new File("src/sample/info.txt");

    private static ImageIcon icon = new ImageIcon("src/sample/bulb2.gif");
    private static Image image = icon.getImage();
    private static boolean notificationsOn = true;
    public static TrayIcon trayIcon = new TrayIcon(image);
    SystemTray tray = java.awt.SystemTray.getSystemTray();
    ObservableList<String> elem = Controller.getItems();

    @Override
    public void stop() throws Exception {
        super.stop();
        //ObservableList<String> items = FXCollections.observableArrayList("Chrome", "Discord", "Steam", "CsGo", "Zoom");
        logic.writeInfo(file, elem, Controller.getTimeOfAllPrograms(), Controller.getStepOfNotifications(), Controller.getTotalTimeArr());
        System.out.println(elem);
        logic.writeItems(file, elem);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.addAppToTray(trayIcon);
        this.primaryStage = primaryStage;

        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(e ->{
            primaryStage.hide();

        });
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("App Tracker");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
        }


    private void addAppToTray(TrayIcon trayIcon) {
        try {
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            java.awt.MenuItem openItem = new java.awt.MenuItem("Open");
            CheckboxMenuItem notifications = new CheckboxMenuItem("Вимкнути сповіщення");
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");

            notifications.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    int cb1Id = e.getStateChange();
                    if (cb1Id == ItemEvent.SELECTED){
                        notificationsOn = false;
                    } else {
                       notificationsOn = true;
                    }
                }
            });
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);
            notifications.setFont(boldFont);
            exitItem.setFont(boldFont);
            exitItem.addActionListener(event -> {
                Platform.exit();
                tray.remove(trayIcon);
            });

            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.add(notifications);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
            tray.add(trayIcon);
        }catch (AWTException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    public static void showNotification(String text, TrayIcon trayIcon){
        if (notificationsOn)
            trayIcon.displayMessage("App Tracker", text, TrayIcon.MessageType.INFO);
        else
            System.out.println("Notifications OFF");
    }

    private void showStage() {
        if (primaryStage != null) {
            primaryStage.show();
            primaryStage.toFront();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}