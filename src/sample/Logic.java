package sample;

import javafx.collections.ObservableList;

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

    public ArrayList<String> getAllImportantPrograms() throws IOException {
        ArrayList<String> lst = new ArrayList<>();
        String line;
        //Строка щоб забрати всі інстальовані програми системи з павершелу
        String command = "powershell.exe " + "Get-ItemProperty" +
                " HKLM:\\Software\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName";
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();

        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));

        while ((line = stdout.readLine()) != null) {
            if ((line.contains("a") || line.contains("i") || line.contains("o") || line.contains("e") ||line.contains("u")) && !(line.contains("Windows") || line.contains("Win") || line.contains("vs")
                    || line.contains("Microsoft") || line.contains("icecap") || line.contains("----") || line.contains("DisplayName"))) {
                lst.add(line.substring(0,31));
            }
        }

        stdout.close();
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

    public ArrayList<String> readItemsFromFile(File file){

       ArrayList<String> items = new ArrayList<>(1);
            items.add(0, "*");

        int wordNumber = 0;
        Scanner sc = null;
        try {
            sc = new Scanner(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (sc.hasNextLine()) {
            String str = sc.nextLine();
            if (str.contains("execution time")){
                char[] arr = str.toCharArray();
                StringBuilder item = new StringBuilder();
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] == ' '){
                        System.out.println(String.valueOf(item));
                        for (int j = 0; j < items.size(); j++) {
                            if (!items.get(j).equals(String.valueOf(item))){
                                items.add(wordNumber ,String.valueOf(item));
                                System.out.println("reader  = = = == "  + item);
                                wordNumber++;

                            }
                        }
                        break;
                    }else {
                        item.append(arr[i]);
                    }
                }
            }
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println(items.get(i));
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
            }else if (str.contains("minutes")) {
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


    public void writeInfo(File file, ObservableList<String> items, ArrayList<Integer> execTimeInSec, ArrayList<Integer> stepNotiff) {
        try (FileWriter writer = new FileWriter(file, true)) {
            int[] totalTime = new int[items.size()];
            int[] notificationsTime = new int[items.size()];
            Scanner sc = new Scanner(file, StandardCharsets.UTF_8);
            int wordNumber = 0; //дорівнює кількості елементів, допомагає писати в масив
            int wordNumber2 = 0;
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
                }else if (str.contains("minutes")) {
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

            writer.append("Востаннє програма відкривалась " + date + "\n");
            for (int i = 0; i < items.size(); i++) {
                int f = execTimeInSec.get(i) + totalTime[i];
                writer.append(items.get(i) + " execution time: " + execTimeInSec.get(i) + "\n");
                writer.append(items.get(i) + " total time_" + f + "_\n");
                writer.append(items.get(i) + " minutes_" + stepNotiff.get(i) + "_" + "\n");
                System.out.println(stepNotiff.get(i));
            }
            writer.append('\n');
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}