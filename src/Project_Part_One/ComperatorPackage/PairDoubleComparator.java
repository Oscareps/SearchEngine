package Project_Part_One.ComperatorPackage;

import javafx.util.Pair;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator for Map Entry. Compare between HashMap<String, TermInfo> entries by the String in the key of the entry
 */
public class PairDoubleComparator implements Comparator<Pair<String, Double>> {

    @Override
    public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
        return o2.getValue().compareTo(o1.getValue());
    }
}