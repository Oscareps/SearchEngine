package Project_Part_One.ComperatorPackage;

import Project_Part_One.InfoPackage.TermInfo;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator for Map Entry. Compare between HashMap<String, TermInfo> entries by the String in the key of the entry
 */
public class MapEntryStringComparator implements Comparator<Map.Entry<String, TermInfo>> {

    @Override
    public int compare(Map.Entry<String, TermInfo> o1, Map.Entry<String, TermInfo> o2) {
        return o1.getKey().compareTo(o2.getKey());
    }
}
