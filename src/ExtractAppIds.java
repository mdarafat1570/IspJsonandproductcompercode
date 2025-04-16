import java.io.*;
import java.util.*;

public class ExtractAppIds {
    public static void main(String[] args) {
        String adminAppsPath = "C:\\MY File\\inferloom All File\\ispjasonandproductcompercode\\src\\adminapplist.txt";

        List<String> packageNames = extractPackageNames(adminAppsPath);

        System.out.println("List of application IDs found in adminapplist.txt:");
        for (String name : packageNames) {
            System.out.println(name);
        }
    }

    private static List<String> extractPackageNames(String filePath) {
        List<String> packageNames = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("id=")) {
                    String packageName = line.substring(line.indexOf("id=") + 3);
                    packageNames.add(packageName);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading adminapplist.txt file: " + e.getMessage());
        }
        return packageNames;
    }
}
