package sample;
import java.io.*;
import java.util.ArrayList;

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
}