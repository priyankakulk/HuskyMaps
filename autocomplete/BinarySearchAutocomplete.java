package autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Binary search implementation of the {@link Autocomplete} interface.
 *
 * @see Autocomplete
 */
public class BinarySearchAutocomplete implements Autocomplete {
    /**
     * {@link List} of added autocompletion terms.
     */
    private final List<CharSequence> elements;

    /**
     * Constructs an empty instance.
     */
    public BinarySearchAutocomplete() {
        elements = new ArrayList<>();
    }

    @Override
    public void addAll(Collection<? extends CharSequence> terms) {
        for(CharSequence c: terms) {
            elements.add(c);
        }
        Collections.sort(elements, CharSequence::compare);
    }

    @Override
    public List<CharSequence> allMatches(CharSequence prefix) {
        List<CharSequence> matchList = new ArrayList<>();
        int start = Collections.binarySearch(elements, prefix, CharSequence::compare);
        if(start < 1) {
            start = -(start +1);
        }

        for(int i = start; i < elements.size(); i++) {
            if(elements.get(i).toString().startsWith(prefix.toString())) {
                matchList.add(elements.get(i));
            }
            else {
                break;
            }
        }
        
        return matchList;
    }
}
