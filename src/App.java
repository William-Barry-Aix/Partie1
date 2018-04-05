import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class App {
    private static String fileName = "tweets";
    private static String freq = "2";
    public static void main(String[] args) {
        if (args.length == 4) {
            fileName = args[1];
            freq = args[2];
        }
        csvToTrans();
        //exec();
        //transToCsv();
    }
    private static void csvToTrans(){
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName + ".csv"));
            FileWriter out = new FileWriter(fileName + ".trans");
            FileWriter dic = new FileWriter(fileName + ".dico");
            Map<String, Integer> dicoList = new HashMap<>();
            String row;
            int i = 0;
            System.out.println("Ecriture de la sortie...");
            while ((row = in.readLine()) != null){
                for (String val :row.split(",")) {
                    if (!dicoList.containsKey(val)){
                        dicoList.put(val, i);
                        ++i;
                    }
                    out.write(dicoList.get(val) + " ");
                }
                out.write("\n");
            }
            System.out.println("OK!");
            for (String key : dicoList.keySet()) {
                dic.write(dicoList.get(key) + " : " + key + "\n");
            }
            in.close();
            out.close();
            dic.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void transToCsv(){
        try {
            BufferedReader out = new BufferedReader(new FileReader(fileName + ".out"));
            BufferedReader dic = new BufferedReader(new FileReader(fileName + ".dico"));
            FileWriter csv = new FileWriter(fileName + ".csv");
            Map<Integer, String> dicoList = new HashMap<>();
            String row = "";
            while ((row = dic.readLine()) != null){
                String[] data = row.split(":");
                data[0] = data[0].trim();
                if (data[1].trim().length() != 0){
                    data[1] = data[1].trim();
                } else
                    data[1] = " ";

                dicoList.put(Integer.parseInt(data[0]), data[1]);
            }

            while ((row = out.readLine()) != null){
                row = row.replace("(", "");
                row = row.replace(")", "");
                String[] data = row.split(" ");
                //Faire ça
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void exec() {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        String homeDirectory = System.getProperty("user.home");
        Process process = null;
        try {
            if (isWindows) {
                process = Runtime.getRuntime()
                        .exec(String.format("", homeDirectory));
            } else {
                process = Runtime.getRuntime()
                        .exec(String.format("./apriori/apriori "+ fileName + ".trans " + freq + " " + fileName + ".out", homeDirectory));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode;
        try {
            exitCode = process.waitFor();
            assert exitCode == 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}