import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import minpq.OptimizedHeapMinPQ;

/**
 * Display the most commonly-reported WCAG recommendations.
 */
public class ReportAnalyzer {
    public static void main(String[] args) throws IOException {
        File inputFile = new File("data/wcag.tsv");
        Map<String, String> wcagDefinitions = new LinkedHashMap<>();
        Scanner scanner = new Scanner(inputFile);
        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split("\t", 2);
            String index = "wcag" + line[0].replace(".", "");
            String title = line[1];
            wcagDefinitions.put(index, title);
        }

        Pattern re = Pattern.compile("wcag\\d{3,4}");
        List<String> wcagTags = Files.walk(Paths.get("data/reports"))
                .map(path -> {
                    try {
                        return Files.readString(path);
                    } catch (IOException e) {
                        return "";
                    }
                })
                .flatMap(contents -> re.matcher(contents).results())
                .map(MatchResult::group)
                .toList();

        //initialize a minpq-optimized heap and count every element
        OptimizedHeapMinPQ<String> minPQ = new OptimizedHeapMinPQ<>();
        for(int i = 0; i < wcagTags.size(); i++) {
            if(!minPQ.contains(wcagTags.get(i))) {
                minPQ.add(wcagTags.get(i), 1);
            }
            else {
                double count = minPQ.getPriority(wcagTags.get(i));
                minPQ.changePriority(wcagTags.get(i), count + 1);
            }
        }

        //then only keep and print the top 3
        int initialSize = minPQ.size();
        for(int i = 0; i < initialSize - 3; i++){
            minPQ.removeMin();
        }

        System.out.println(wcagDefinitions.get(minPQ.removeMin()));
        System.out.println(wcagDefinitions.get(minPQ.removeMin()));
        System.out.println(wcagDefinitions.get(minPQ.removeMin()));

    }
}