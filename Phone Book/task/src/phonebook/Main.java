package phonebook;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File dirFile = new File("C:\\Users\\sp\\Downloads\\directory.txt");
        File findFile = new File("C:\\Users\\sp\\Downloads\\find.txt");
        List<String> directory = new ArrayList<>();
        List<String> find = new ArrayList<>();
        try (Scanner scanner = new Scanner(dirFile)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] split = line.split(" ", 2);
                directory.add(split[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try (Scanner scanner = new Scanner(findFile)) {
            while (scanner.hasNext()) {
                find.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Start searching (linear search)...");
        long startLinear = System.currentTimeMillis();
        int found = linearSearch(directory, find);
        long searchTimeLinear = System.currentTimeMillis() - startLinear;
        System.out.printf("Found %d / %d entries. Time taken: %3$tM min. %3$tS sec. %3$tL ms.%n", found, find.size(), searchTimeLinear);

        System.out.println("Start searching (bubble sort + jump search)...");
        List<String> sortedDirectory = new ArrayList<>(directory);
        long sortStart = System.currentTimeMillis();

        bubbleSort(sortedDirectory, searchTimeLinear * 10);

        long sortTime = System.currentTimeMillis() - sortStart;
        long secondSearchStart = System.currentTimeMillis();
        found = jumpSearch(sortedDirectory, find);

        long secondSearch = System.currentTimeMillis() - secondSearchStart;
        System.out.printf("Found %d / %d entries. Time taken: %3$tM min. %3$tS sec. %3$tL ms.%n", found, find.size(), secondSearch + secondSearchStart - sortStart);
        System.out.printf("Sorting time: %1$tM min. %1$tS sec. %1$tL ms.%n", sortTime);
        System.out.printf("Searching time: %1$tM min. %1$tS sec. %1$tL ms.%n", secondSearch);

        //----------------------------
        System.out.println("Start searching (quick sort + binary search)...");
        sortedDirectory = new ArrayList<>(directory);
        sortStart = System.currentTimeMillis();

        quickSort(sortedDirectory, 0, sortedDirectory.size() - 1);

        sortTime = System.currentTimeMillis() - sortStart;
        secondSearchStart = System.currentTimeMillis();
        found = binarySearch(sortedDirectory, find);

        secondSearch = System.currentTimeMillis() - secondSearchStart;
        System.out.printf("Found %d / %d entries. Time taken: %3$tM min. %3$tS sec. %3$tL ms.%n", found, find.size(), secondSearch + secondSearchStart - sortStart);
        System.out.printf("Sorting time: %1$tM min. %1$tS sec. %1$tL ms.%n", sortTime);
        System.out.printf("Searching time: %1$tM min. %1$tS sec. %1$tL ms.%n", secondSearch);

        //----------------------------
        System.out.println("Start searching (hash table)...");
        Hashtable<String, String> hashTable = new Hashtable<String, String>();
        sortStart = System.currentTimeMillis();

        for (String element : directory) {
            hashTable.put(element, element);
        }

        sortTime = System.currentTimeMillis() - sortStart;
        secondSearchStart = System.currentTimeMillis();

        found = 0;
        for (String element : find) {
            if (hashTable.containsKey(element)) {
                found++;
            }
        }

        secondSearch = System.currentTimeMillis() - secondSearchStart;
        System.out.printf("Found %d / %d entries. Time taken: %3$tM min. %3$tS sec. %3$tL ms.%n", found, find.size(), secondSearch + secondSearchStart - sortStart);
        System.out.printf("Creating time: %1$tM min. %1$tS sec. %1$tL ms.%n", sortTime);
        System.out.printf("Searching time: %1$tM min. %1$tS sec. %1$tL ms.%n", secondSearch);

    }

    private static void bubbleSort(List<String> array, long limit) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < array.size() - 1; i++) {
            if (System.currentTimeMillis() - start > limit) {
                break;
            }
            for (int j = 0; j < array.size() - i - 1; j++) {
                if (array.get(j).compareTo(array.get(j + 1)) > 0) {
                    String temp = array.get(j);
                    array.set(j, array.get(j + 1));
                    array.set(j + 1, temp);
                }
            }
        }
    }

    public static void quickSort(List<String> arr, int low, int high) {
        if (low < high) {
            int p = partition(arr, low, high);
            quickSort(arr, low, p - 1);
            quickSort(arr, p + 1, high);
        }
    }

    static void swap(List<String> arr, int low, int pivot) {
        String tmp = arr.get(low);
        arr.set(low, arr.get(pivot));
        arr.set(pivot, tmp);
    }

    static int partition(List<String> arr, int low, int high) {
        int p = low, j;
        for (j = low + 1; j <= high; j++) {
            if (arr.get(j).compareTo(arr.get(low)) < 0) {
                swap(arr, ++p, j);
            }
        }

        swap(arr, low, p);
        return p;
    }

    private static int linearSearch(List<String> directory, List<String> find) {
        int found = 0;
        for (String element : find) {
            for (String dirElement : directory) {
                if (dirElement.compareTo(element) == 0) {
                    found++;
                    break;
                }
            }
        }
        return found;
    }

    private static int jumpSearch(List<String> directory, List<String> find) {
        int found = 0;
        int step = (int) Math.floor(Math.sqrt(directory.size()));
        for (String element : find) {
            if (getFound(directory, step, element) > 0) {
                found++;
            }
        }
        return found;
    }

    private static int getFound(List<String> directory, int step, String element) {
        int curr = 0;
        int ind;
        while (curr < directory.size()) {
            String s = directory.get(curr);
            if (s.compareTo(element) == 0) {
                return curr;
            } else if (s.compareTo(element) > 0) {
//                    System.out.println(s.substring(0,s.length() - element.length()));
//                    System.out.println(element);
                ind = curr - 1;
                while (ind > curr - step && ind >= 1) {
                    if (s.compareTo(element) == 0) {
                        return ind;
                    }
                    ind -= 1;
                }
            }
            curr += step;
            ind = directory.size() - 1;
            while (ind > curr - step) {
                if (directory.get(ind).compareTo(element) == 0) {
                    return ind;
                }
                ind -= 1;
            }
        }
        return -1;
    }

    private static int binarySearch(List<String> directory, List<String> find) {
        int found = 0;

        for (String element : find) {
            if (getAnInt(directory, element) >= 0) {
                found++;
            };
        }
        return found;
    }

    private static int getAnInt(List<String> directory, String element) {
        int left = 0;
        int right = directory.size() - 1;
        while (left <= right) {
            int middle = (left + right) / 2;
            String s = directory.get(middle);
            if (s.compareTo(element) == 0) {
                return middle;
            } else if (s.compareTo(element) > 0) {
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }
        return -1;
    }
}
