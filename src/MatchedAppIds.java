import java.io.*;
import java.util.*;
import java.util.regex.*;

public class MatchedAppIds {

    private static final String GRADLE_FILE_PATH = "C:\\MY File\\inferloom All File\\ispjasonandproductcompercode\\src\\build.gradle";
    private static final String LIVE_APP_LIST_PATH = "C:\\MY File\\inferloom All File\\ispjasonandproductcompercode\\src\\live_app_list.txt";
    private static final String FASTFILE_PATH = "C:\\MY File\\inferloom All File\\ispjasonandproductcompercode\\src\\Fastfile";
    private static final String PACKAGE_PREFIX = "com.softifybd.";

    public static void main(String[] args) {
        List<String> gradleAppIds = removePrefix(extractApplicationIds(GRADLE_FILE_PATH));
        List<String> liveAppIds = removePrefix(readLiveAppList(LIVE_APP_LIST_PATH));
        List<String> fastfileLanes = extractFastfileLanes(FASTFILE_PATH);

        gradleAppIds.retainAll(liveAppIds);

        List<String> newLanes = new ArrayList<>();

        for (String app : gradleAppIds) {
            if (!fastfileLanes.contains(app.toLowerCase())) {
                newLanes.add(generateFastlaneCode(app));
            }
        }

        if (!newLanes.isEmpty()) {
            appendToFastfile(newLanes);
            System.out.println("✅ New lanes written to Fastfile:");
            newLanes.forEach(System.out::println);
        } else {
            System.out.println("✅ All matched apps already have lanes in Fastfile. Nothing new to add.");
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
                    ids.add(matcher.group(1).trim());
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error reading build.gradle file: " + e.getMessage());
        }
        return ids;
    }

    private static List<String> readLiveAppList(String filePath) {
        List<String> ids = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    ids.add(trimmed);
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error reading live_app_list.txt: " + e.getMessage());
        }
        return ids;
    }

    private static List<String> removePrefix(List<String> fullIds) {
        List<String> result = new ArrayList<>();
        for (String id : fullIds) {
            if (id.startsWith(PACKAGE_PREFIX)) {
                result.add(id.substring(PACKAGE_PREFIX.length()));
            } else {
                result.add(id);
            }
        }
        return result;
    }

    private static List<String> extractFastfileLanes(String filePath) {
        List<String> lanes = new ArrayList<>();
        Pattern pattern = Pattern.compile("lane\\s+:([a-zA-Z0-9_]+)\\s+do");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    lanes.add(matcher.group(1).toLowerCase());
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error reading Fastfile: " + e.getMessage());
        }
        return lanes;
    }

    private static String generateFastlaneCode(String appName) {
        String flavorName = appName.toUpperCase();
        String laneName = appName.toLowerCase();
        String fullPackage = PACKAGE_PREFIX + appName;

        return String.format(
                "\n#===========================================\n" +
                        "desc \"deploy %s\"\n" +
                        "lane :%s do\n\n" +
                        "    gradle(\n" +
                        "        task: 'bundle',\n" +
                        "        flavor: \"%s\",\n" +
                        "        build_type: 'Release')\n\n" +
                        "    AAB_LOCATION = \"#{lane_context[SharedValues::GRADLE_AAB_OUTPUT_PATH]}\"\n\n" +
                        "    copy_artifacts(\n" +
                        "        artifacts: [AAB_LOCATION],\n" +
                        "        target_path: '~/Desktop/'\n" +
                        "    )\n\n" +
                        "    supply(\n" +
                        "        track: 'production',\n" +
                        "        package_name: '%s',\n" +
                        "        skip_upload_images: true,\n" +
                        "        skip_upload_screenshots: true,\n" +
                        "        skip_upload_metadata: true,\n" +
                        "        skip_upload_apk: true,\n" +
                        "        aab: AAB_LOCATION\n" +
                        "    )\n" +
                        "end\n", appName, laneName, flavorName, fullPackage);
    }

    private static void appendToFastfile(List<String> lanes) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FASTFILE_PATH, true))) {
            for (String lane : lanes) {
                bw.write(lane);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("❌ Error writing to Fastfile: " + e.getMessage());
        }
    }
}

