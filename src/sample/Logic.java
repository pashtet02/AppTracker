package sample;

import java.io.*;
import java.util.ArrayList;


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
        String command = "powershell.exe " + "Get-ItemProperty" +
                " HKLM:\\Software\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName | Format-Table –AutoSize";
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
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
}

