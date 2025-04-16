import java.io.*;
import java.util.*;
import java.util.regex.*;

public class MissingAppsFromGradle {
    public static void main(String[] args) {
        String clientAppPath = "C:\\MY File\\inferloom All File\\ispjasonandproductcompercode\\src\\client_app.txt";
        String adminAppPath = "C:\\MY File\\inferloom All File\\ispjasonandproductcompercode\\src\\adminapplist.txt";
        String buildGradlePath = "C:\\MY File\\inferloom All File\\ispjasonandproductcompercode\\src\\build.gradle"; // or actual build.gradle file path

        String prefix = "com.softifybd.";

        // Read & process lists
        List<String> clientPackages = readAppNames(clientAppPath);
        List<String> clientApps = extractNamesFromPackages(clientPackages, prefix);

        List<String> adminApps = readAppNames(adminAppPath);
        List<String> buildGradleAppIds = extractApplicationIds(buildGradlePath);
        List<String> gradleAppNames = extractNamesFromPackages(buildGradleAppIds, prefix);

        // Merge client + admin (unique set)
        Set<String> combined = new HashSet<>();
        combined.addAll(clientApps);
        combined.addAll(adminApps);

        // Find only those missing in build.gradle
        List<String> missingInBuildGradle = new ArrayList<>();
        for (String app : combined) {
            if (!gradleAppNames.contains(app)) {
                missingInBuildGradle.add(app);
            }
        }

        // Print results
        System.out.println("❌ Apps present in admin/client but missing in build.gradle:");
        missingInBuildGradle.forEach(System.out::println);
        System.out.println("🔢 Total missing: " + missingInBuildGradle.size());
    }

    private static List<String> readAppNames(String filePath) {
        List<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim().toLowerCase();
                if (!trimmed.isEmpty()) {
                    names.add(trimmed);
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + e.getMessage());
        }
        return names;
    }

    private static List<String> extractNamesFromPackages(List<String> packageList, String prefix) {
        List<String> appNames = new ArrayList<>();
        for (String pkg : packageList) {
            if (pkg.startsWith(prefix)) {
                appNames.add(pkg.substring(prefix.length()).toLowerCase());
            }
        }
        return appNames;
    }

    private static List<String> extractApplicationIds(String filePath) {
        List<String> ids = new ArrayList<>();
        Pattern pattern = Pattern.compile("applicationId\\s+\"(.*?)\"");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    ids.add(matcher.group(1).trim().toLowerCase());
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error reading build.gradle: " + e.getMessage());
        }
        return ids;
    }
}
