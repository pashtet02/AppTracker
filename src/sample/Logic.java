package sample;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import static sample.Controller.stepOfNotifications;
import static sample.Controller.totalTimeArr;

class Logic{
    ArrayList<String> getCurrentProcesses(){
        ArrayList<String> lst = new ArrayList<>();
        ProcessHandle.allProcesses().forEach(processHandle -> {
            String str = String.valueOf(processHandle.info());
            //Дивний іф потрібний, щоб забрати порожні системні процеси
            if (str.contains("a") || str.contains("e") || str.contains("y") || str.contains("u") ||str.contains("o") || str.contains("i") )
                lst.add( ""+processHandle.pid() + " " + str);
        });
        return lst;
    }

    public ArrayList<String> getAllImportantPrograms(ObservableList<String> items) throws IOException {
        ArrayList<String> lst = new ArrayList<>();
        ArrayList<String> lst2 = new ArrayList<>();
        String line;
        //Строка щоб забрати всі інстальовані програми системи з павершелу
        String command = "powershell.exe " + "Get-ItemProperty" +
                " HKLM:\\Software\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName";
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();

        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            if ((line.contains("a") || line.contains("i") || line.contains("o") || line.contains("e") || line.contains("u")) && !(line.contains("Windows") || line.contains("Win") || line.contains("vs")
                    || line.contains("Microsoft") || line.contains("icecap") || line.contains("----") || line.contains("DisplayName"))) {
                lst.add(line.replaceAll("[\\s]{2,}", " "));

            }
        }
        return lst;
    }

    public boolean isProcessAlive(String name) {
        String nameWithoutRegistr = name.toLowerCase();
        ArrayList<String> currentProcesses = getCurrentProcesses();
        boolean programIsAlive = false;
        for (int i = 0; i < currentProcesses.size(); i++) {
            if (currentProcesses.get(i).toLowerCase().contains(nameWithoutRegistr)) {
                programIsAlive = true;
                break;
            }
        }
        return programIsAlive;
    }
    public void writeItems(File file, ObservableList<String> items){
        try (FileWriter writer = new FileWriter(file, true)) {
            StringBuilder result = new StringBuilder("Items_");
            String[] str = new String[items.size()];
            Scanner sc = new Scanner(file, StandardCharsets.UTF_8);

            for (int i = 0; i < items.size(); i++) {
                str[i] = String.valueOf(items.get(i));
                if (str[i].charAt(str[i].length()-1) == ' '){
                    result.append(str[i].substring(0, str[i].length()-1) + "_");
                }
                else {
                    result.append(str[i] + "_");
                }

            }
            result.deleteCharAt(result.length()-1);
            System.out.println(result);

            writer.append(result + "\n");
            sc.close();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public ArrayList<String> readItems(File file){
        ArrayList<String> items = new ArrayList<>();
        String[] arrStr;
        Scanner sc = null;
        try {
            sc = new Scanner(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (sc.hasNextLine()) {
            String str = sc.nextLine();
            if (str.contains("Items")){
                items.clear();
                arrStr = str.split("_");
                for (int i = 1; i < arrStr.length; i++) {
                    if(!items.contains(arrStr[i])){
                        items.add(i-1, arrStr[i]);
                    }
                }
            }
        }
        return items;
    }
    public void readTotalTimeOfWork(File file, ObservableList<String> items) {
        int[] totalTime = new int[items.size()];
        int[] notificationsTime = new int[items.size()];
        Scanner sc = null;
        try {
            sc = new Scanner(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int wordNumber = 0; //дорівнює кількості елементів, допомагає писати в масив
        int wordNumber2 = 0;

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
            }else if (str.contains("minutes") && str.contains(items.get(wordNumber2))) {
                char[] arr = str.toCharArray();
                for (int i = 0; i < arr.length - 1; i++) {
                    if (arr[i] == '_') {
                        int j = i + 1;
                        StringBuilder number = new StringBuilder();
                        while (arr[j] != '_') {
                            number.append(arr[j]);
                            j++;
                        }
                        notificationsTime[wordNumber2] = Integer.parseInt(String.valueOf(number));
                    }
                }
                if (wordNumber2 == notificationsTime.length -1)
                    wordNumber2 = 0;
                else wordNumber2++;
            }
        }
        sc.close();
        for (int i = 0; i < totalTime.length; i++) {
            totalTimeArr.add(i, totalTime[i]);
            System.out.println("Index is " + i + "Value is " + totalTime[i]);
        }
        for (int i = 0; i < notificationsTime.length; i++) {
            stepOfNotifications.add(i, notificationsTime[i]);
            System.out.println("Index is " + i + "Step is " + notificationsTime[i]);
        }
    }


    public void writeInfo(File file, ObservableList<String> items, ArrayList<Integer> execTimeInSec, ArrayList<Integer> stepNotiff, ArrayList<Integer> totalTimeArr) {
        try (FileWriter writer = new FileWriter(file)) {
            Date date = new Date();
            writer.append("Востаннє програма відкривалась " + date + "\n");
            for (int i = 0; i < items.size(); i++) {
                writer.append(items.get(i) + " execution time: " + execTimeInSec.get(i) + "\n");
                writer.append(items.get(i) + " total time_" + totalTimeArr.get(i) + "_\n");
                writer.append(items.get(i) + " minutes_" + stepNotiff.get(i) + "_" + "\n");
                System.out.println(stepNotiff.get(i));
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void chartCreator(PieChart pieChart ,AnchorPane pane, ObservableList<String> names, ArrayList<Integer> currTime){
        Label header = new Label("Статистика за увесь час");
        PieChart.Data[] slices = new PieChart.Data[names.size()];
        for (int i = 0; i < names.size(); i++) {
            String[] arr = names.get(i).split("\\s+");
            slices[i] = new PieChart.Data(arr[0], currTime.get(i) / 60);
        }
        for (int i = 0; i < slices.length; i++) {
            pieChart.getData().add(slices[i]);
        }

        pieChart.setPrefSize(pane.getWidth(), pane.getHeight());
        pieChart.setLegendSide(Side.LEFT);
        pieChart.setLegendVisible(true);
        pieChart.setStartAngle(30);
        final Label caption = new Label("");
        caption.setTextFill(Color.WHITE);
        caption.setStyle("-fx-font: 12 arial;");
        for (final PieChart.Data data : pieChart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    caption.setTranslateX(e.getSceneX());
                    caption.setTranslateY(e.getSceneY());

                    caption.setText(String.valueOf(data.getPieValue() + "хв."));
                }
            });
        }
        pane.getChildren().addAll(header,pieChart, caption);
    }
}