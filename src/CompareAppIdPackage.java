import java.io.*;
import java.util.*;
import java.util.regex.*;

public class CompareAppIdPackage {
    public static void main(String[] args) {
        String buildGradlePath = "C:\\MY File\\inferloom All File\\ispjasonandproductcompercode\\src\\build.gradle";
        String googleServicesPath = "C:\\MY File\\inferloom All File\\ispjasonandproductcompercode\\src\\google-services.json";

        List<String> applicationIds = extractApplicationIds(buildGradlePath);
        List<String> packageNames = extractPackageNames(googleServicesPath);

        List<String> missing = new ArrayList<>(applicationIds);
        missing.removeAll(packageNames);

        System.out.println("List of applicationIds:");
        for (String id : applicationIds) {
            System.out.println(id);
        }

        System.out.println("\nTotal applicationIds found: " + applicationIds.size());
        System.out.println("Total package_names found: " + packageNames.size());
        System.out.println("Total missing applicationIds: " + missing.size());

        if (!missing.isEmpty()) {
            System.out.println("\nMissing package_names for the following applicationIds:");
            for (String id : missing) {
                System.out.println(id);
            }
        } else {
            System.out.println("\nAll applicationIds have matching package_names.");
        }
    }

    private static List<String> extractApplicationIds(String filePath) {
        List<String> ids = new ArrayList<>();
        Pattern pattern = Pattern.compile("applicationId\\s+\"(.*?)\"");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    ids.add(matcher.group(1));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading build.gradle file: " + e.getMessage());
        }
        return ids;
    }

    private static List<String> extractPackageNames(String filePath) {
        List<String> packageNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"package_name\":\\s*\"(.*?)\"");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    packageNames.add(matcher.group(1));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading google-services.json file: " + e.getMessage());
        }
        return packageNames;
    }
}
