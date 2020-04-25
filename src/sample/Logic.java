package sample;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

class Logic{
    public static int getExecutionTime() {
        return executionTime;
    }

    static int executionTime = 0;

    private ArrayList<String> getCurrentProcesses(){
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
                " HKLM:\\Software\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName | Format-Table –AutoSize";
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            //цей іф нада прошо щоб відфільтрувати системні програмки, бо їх забагато
            if (!(line.contains("Windows") || line.contains("Win") || line.contains("vs") || line.contains("Microsoft") || line.contains("icecap"))) {
                lst.add(line);
            }
        }
        stdout.close();
        return lst;
    }

    public boolean isProcessAlive(String name) {
        ArrayList<String> currentProcesses = getCurrentProcesses();
        boolean programIsAlive = false;
        for (int i = 0; i < currentProcesses.size(); i++) {
            if (currentProcesses.get(i).contains(name)) {
                programIsAlive = true;
                break;
            }
        }
        return programIsAlive;
    }

    public void countExecutionTimeOfProgram(String nameOfProgram){
        final Timer timer = new Timer();
        //final int[] timeOfWork = {0};
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isProcessAlive(nameOfProgram)){
                    System.out.println(isProcessAlive(nameOfProgram));
                    executionTime++;

                }
                else{
                    timer.cancel();
                    // timeOfWork[0] = executionTime;
                    System.out.println("Час виконання програми: " + nameOfProgram + new SimpleDateFormat(" HH:mm:ss ")
                            .format(new Date(TimeUnit.SECONDS.toMillis(executionTime))));
                }
            }
        }, 1000, 1000);
        //return timeOfWork[0];
    }
}

