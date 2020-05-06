package sample;
import javafx.collections.ObservableList;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import static sample.Controller.totalTimeArr;

class Logic{

    ArrayList<String> getCurrentProcesses(){
        ArrayList<String> lst = new ArrayList<>();
        ProcessHandle.allProcesses().forEach(processHandle -> {
            String str = String.valueOf(processHandle.info());
            lst.add( ""+processHandle.pid() + " " + str);
        });
        return lst;
    }

    public ArrayList<String> getAllImportantPrograms() throws IOException {
        ArrayList<String> lst = new ArrayList<>();
        //Строка щоб забрати всі інстальовані програми системи з павершелу
        String command = "powershell.exe " + "Get-ItemProperty" +
                " HKLM:\\Software\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName";
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            //цей іф нада прошо щоб відфільтрувати системні програмки, бо їх забагато
            if ((line.contains("a") || line.contains("i") || line.contains("o") || line.contains("e") ||line.contains("u")) && !(line.contains("Windows") || line.contains("Win") || line.contains("vs")
                    || line.contains("Microsoft") || line.contains("icecap") || line.contains("----") || line.contains("DisplayName"))) {

                lst.add(line.substring(0,31));
            }
        }
        stdout.close();
        return lst;
    }

    public boolean isProcessAlive(String name) {
        String nameWithoutregistr = name.toLowerCase();
        ArrayList<String> currentProcesses = getCurrentProcesses();
        boolean programIsAlive = false;
        for (int i = 0; i < currentProcesses.size(); i++) {
            if (currentProcesses.get(i).toLowerCase().contains(nameWithoutregistr)) {
                programIsAlive = true;
                break;
            }
        }
        return programIsAlive;
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

    public void showNotification(String text) throws AWTException, MalformedURLException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();
        /*Image image = null;
        try {
            image = ImageIO.read(new File("C:\\Users\\Pavlo\\Desktop\\gitHub\\TeamWork\\src\\sample\\GearIcon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //If the icon is a file
        //Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("src/sample/images/GearIconButton.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Java AWT Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("App Tracker");
        tray.add(trayIcon);
        trayIcon.displayMessage("App Tracker", text, TrayIcon.MessageType.NONE);
        tray.remove(trayIcon);

    }

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
                    if (wordNumber == totalTime.length - 1)
                        wordNumber = 0;
                    else wordNumber++;
                }
            }
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
}