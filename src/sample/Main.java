package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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

    Logic logic = new Logic();
    Stage primaryStage = new Stage();
    SystemTray tray = java.awt.SystemTray.getSystemTray();
    static ImageIcon icon = new ImageIcon("src/sample/bulb2.gif");
    static Image image = icon.getImage();
    private static boolean notificationsOn = true;
    static TrayIcon trayIcon = new TrayIcon(image);


    @Override
    public void stop() throws Exception {
        super.stop();
        File file = new File("src/sample/info.txt");
        ObservableList<String> items = FXCollections.observableArrayList("Chrome", "Discord", "Steam", "CsGo", "Zoom");
        logic.writeItemsInFileAndTime(file, items, Controller.getTimeOfAllPrograms());
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
        primaryStage.setTitle("Tracker");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }


    void addAppToTray(TrayIcon trayIcon) {
        try {
            java.awt.Toolkit.getDefaultToolkit();
            //app requires system tray support, just exit if there is no support.
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
        } catch (AWTException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    static void showNotification(String text, TrayIcon trayIcon){
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