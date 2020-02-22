package Project_Part_One.ComperatorPackage;

import Project_Part_One.InfoPackage.TermInfo;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator for Map Entry. Compare between HashMap<String, TermInfo> entries by the String in the key of the entry
 */
public class PairIntegerBackwardsComparator implements Comparator<Pair<String, Integer>> {

    @Override
    public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
        int res = o1.getValue() - o2.getValue();
        return res;
    }
}