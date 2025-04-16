import java.io.*;
import java.util.*;
import java.util.regex.*;

public class GenerateFastfileLanesFromFlavors {

    private static final String FLAVORS_GRADLE_PATH = "C:\\MY File\\inferloom All File\\ispjasonandproductcompercode\\src\\flavors.gradle";
    private static final String OUTPUT_FILE = "GeneratedFastfileLanes.txt";
    private static final String PACKAGE_PREFIX = "com.softifybd.";

    public static void main(String[] args) {
        List<String> appIds = extractAppIds(FLAVORS_GRADLE_PATH);

        if (appIds.isEmpty()) {
            System.out.println("❌ No app IDs found in flavors.gradle");
            return;
        }

        System.out.println("🔢 Total App IDs found: " + appIds.size());

        List<String> fastlaneLanes = new ArrayList<>();
        for (String id : appIds) {
            fastlaneLanes.add(generateFastlaneLane(id));
        }

        writeToFile(fastlaneLanes, OUTPUT_FILE);
        System.out.println("✅ Lanes generated and written to: " + OUTPUT_FILE);
    }

    private static List<String> extractAppIds(String filePath) {
        List<String> ids = new ArrayList<>();
        Pattern pattern = Pattern.compile("applicationId\\s+\"(com\\.softifybd\\.[a-zA-Z0-9_]+)\"");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    ids.add(matcher.group(1));
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error reading flavors.gradle: " + e.getMessage());
        }
        return ids;
    }

    private static String generateFastlaneLane(String fullAppId) {
        String appName = fullAppId.replace(PACKAGE_PREFIX, "");
        String flavorName = appName.replace("app", "");
        return String.format(
                "\n#===========================================\n" +
                        "desc \"deploy each flavor\"\n" +
                        "lane :%s do\n\n" +
                        "    gradle(\n" +
                        "            task: 'bundle',\n" +
                        "            flavor: \"%s\",\n" +
                        "            build_type: 'Release')\n\n" +
                        "    AAB_LOCATION = \"#{lane_context[SharedValues::GRADLE_AAB_OUTPUT_PATH]}\"\n\n" +
                        "    copy_artifacts(\n" +
                        "            artifacts: [AAB_LOCATION],\n" +
                        "            target_path: '~/Desktop/'\n" +
                        "    )\n\n" +
                        "    supply(\n" +
                        "            track: 'production',\n" +
                        "            package_name: '%s',\n" +
                        "            skip_upload_images: true,\n" +
                        "            skip_upload_screenshots: true,\n" +
                        "            skip_upload_metadata: true,\n" +
                        "            skip_upload_apk: true,\n" +
                        "            aab: AAB_LOCATION\n" +
                        "    )\n" +
                        "end\n", appName.toLowerCase(), flavorName.toLowerCase(), fullAppId);
    }

    private static void writeToFile(List<String> lines, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("❌ Error writing output file: " + e.getMessage());
        }
    }
}
