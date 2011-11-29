import java.io.*;
import java.util.*;

public class TFGenerator {
    private static HashMap<String, Double> map = new HashMap<String, Double>();

    public static void main(String... args) throws FileNotFoundException {
        File inputFile = new File("RedirectResolver/data/entriesENG.txt");
        Scanner sc = new Scanner(new FileInputStream(inputFile));
        double sum = 0;
        while (sc.hasNext()) {
            sc.next();
            double value = sc.nextDouble();
            String key = sc.next().toLowerCase();
            sc.next();
            sum += value;

            if (key.length() > 1) {
                map.put(key, value);
            }
        }

        PrintWriter printWriter = new PrintWriter(new FileOutputStream("RedirectResolver/data/tfData.txt"));

        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        for (Map.Entry<String, Double> entry : list) {
            printWriter.print(entry.getKey());
            printWriter.print('\t');
            printWriter.println(Double.toString(entry.getValue() / sum));
        }

        printWriter.close();
    }
}