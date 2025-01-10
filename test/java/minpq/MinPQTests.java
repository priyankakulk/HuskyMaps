package minpq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Abstract class providing test cases for all {@link MinPQ} implementations.
 *
 * @see MinPQ
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class MinPQTests {
    /**
     * Returns an empty {@link MinPQ}.
     *
     * @return an empty {@link MinPQ}
     */
    public abstract <E> MinPQ<E> createMinPQ();

    @Test
    public void wcagIndexAsPriority() throws FileNotFoundException {
        File inputFile = new File("data/wcag.tsv");
        MinPQ<String> reference = new DoubleMapMinPQ<>();
        MinPQ<String> testing = createMinPQ();
        Scanner scanner = new Scanner(inputFile);
        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split("\t", 2);
            int index = Integer.parseInt(line[0].replace(".", ""));
            String title = line[1];
            reference.add(title, index);
            testing.add(title, index);
        }
        while (!reference.isEmpty()) {
            assertEquals(reference.removeMin(), testing.removeMin());
        }
        assertTrue(testing.isEmpty());
    }

    @Test
    public void randomPriorities() {
        int[] elements = new int[1000];
        for (int i = 0; i < elements.length; i = i + 1) {
            elements[i] = i;
        }
        Random random = new Random(373);
        int[] priorities = new int[elements.length];
        for (int i = 0; i < priorities.length; i = i + 1) {
            priorities[i] = random.nextInt(priorities.length);
        }

        MinPQ<Integer> reference = new DoubleMapMinPQ<>();
        MinPQ<Integer> testing = createMinPQ();
        for (int i = 0; i < elements.length; i = i + 1) {
            reference.add(elements[i], priorities[i]);
            testing.add(elements[i], priorities[i]);
        }

        for (int i = 0; i < elements.length; i = i+1) {
            int expected = reference.removeMin();
            int actual = testing.removeMin();

            if (expected != actual) {
                int expectedPriority = priorities[expected];
                int actualPriority = priorities[actual];
                assertEquals(expectedPriority, actualPriority);
            }
        }
    }

    @Test
       public void randomIntegersRandomPriorities() {
            MinPQ<Integer> reference = new DoubleMapMinPQ<>();
            MinPQ<Integer> testing = createMinPQ();
    
            int iterations = 10000;
            int maxElement = 1000;
            Random random = new Random();
            for (int i = 0; i < iterations; i += 1) {
                int element = random.nextInt(maxElement);
                double priority = random.nextDouble();
                reference.addOrChangePriority(element, priority);
                testing.addOrChangePriority(element, priority);
                assertEquals(reference.peekMin(), testing.peekMin());
                assertEquals(reference.size(), testing.size());
                for (int e = 0; e < maxElement; e += 1) {
                    if (reference.contains(e)) {
                        assertTrue(testing.contains(e));
                        assertEquals(reference.getPriority(e), testing.getPriority(e));
                    } else {
                        assertFalse(testing.contains(e));
                    }
                }
            }
            for (int i = 0; i < iterations; i += 1) {
                boolean shouldRemoveMin = random.nextBoolean();
                if (shouldRemoveMin && !reference.isEmpty()) {
                      assertEquals(reference.removeMin(), testing.removeMin());
                } else {
                    int element = random.nextInt(maxElement);
                    double priority = random.nextDouble();
                    reference.addOrChangePriority(element, priority);
                    testing.addOrChangePriority(element, priority);
                }
                if (!reference.isEmpty()) {
                    assertEquals(reference.peekMin(), testing.peekMin());
                    assertEquals(reference.size(), testing.size());
                    for (int e = 0; e < maxElement; e += 1) {
                        if (reference.contains(e)) {
                            assertTrue(testing.contains(e));
                            assertEquals(reference.getPriority(e), testing.getPriority(e));
                        } else {
                            assertFalse(testing.contains(e));
                        }
                    }
                } else {
                    assertTrue(testing.isEmpty());
                }
            }
        }

    @Test
    public void wcagTests() throws IOException {
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

        Random random = new Random();
        MinPQ<String> reference = new DoubleMapMinPQ<>();
        MinPQ<String> testing = createMinPQ();

        List<String> mostCommonTags = new ArrayList<>();
        mostCommonTags.add("Link Purpose (In Context) A");
        mostCommonTags.add("Target Size (Minimum) AA");
        mostCommonTags.add("Name, Role, Value A");

        Map<String, Integer> weightedTags = new HashMap<>();
        int weight = 0;
        for(String tag: wcagTags) {
            if(mostCommonTags.contains(tag)) {
                weight = 5;
            }
            else {
                weight = 1;
            }
        
            if(weightedTags.containsKey(tag)) {
                weightedTags.put(tag, weightedTags.get(tag) + weight);
            }
            else {
                weightedTags.put(tag, weight);
            }
        }

        List<String> tagsForSample = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : weightedTags.entrySet()) {
            String tag = entry.getKey();
            int correspondingWeight = entry.getValue();
            for(int i = 0; i < correspondingWeight; i++) {
                tagsForSample.add(tag);
            }
        }

        int iterations = 10000;
        for(int i = 0; i < iterations; i++) {
            String tag = wcagTags.get(random.nextInt(tagsForSample.size()));
            if(!reference.contains(tag)) {
                reference.add(tag, 1);
                testing.add(tag,1);
            }
            else {
                double occurrence = reference.getPriority(tag);
                reference.changePriority(tag, occurrence + 1);
                testing.changePriority(tag, occurrence + 1);
                assertEquals(reference.getPriority(tag), testing.getPriority(tag));
            }
        }
        assertEquals(reference.size(), testing.size());
        assertEquals(reference.peekMin(), testing.peekMin());

        List<String> referenceRemovalOrder = new ArrayList<>();
        while(!reference.isEmpty()) {
            referenceRemovalOrder.add(reference.removeMin());
        }

        List<String> testingRemovalOrder = new ArrayList<>();
        while(!testing.isEmpty()) {
            testingRemovalOrder.add(testing.removeMin());
        }

        assertEquals(referenceRemovalOrder.size(), testingRemovalOrder.size());
        for(int i = 0; i < referenceRemovalOrder.size() - 3; i++) {
            String ref = referenceRemovalOrder.get(i);
            String test = testingRemovalOrder.get(i);

            assertEquals(ref, test);
        }
        
        assertTrue(reference.isEmpty());
        assertTrue(testing.isEmpty());
    }
}
