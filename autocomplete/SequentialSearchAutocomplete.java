package autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Sequential search implementation of the {@link Autocomplete} interface.
 *
 * @see Autocomplete
 */
public class SequentialSearchAutocomplete implements Autocomplete {
    /**
     * {@link List} of added autocompletion terms.
     */
    private final List<CharSequence> elements;

    /**
     * Constructs an empty instance.
     */
    public SequentialSearchAutocomplete() {
        elements = new ArrayList<>();
    }

    @Override
    public void addAll(Collection<? extends CharSequence> terms) {
        for(CharSequence c: terms) {
            elements.add(c);
        }
    }

    @Override
    public List<CharSequence> allMatches(CharSequence prefix) {
        List<CharSequence> matchList = new ArrayList<>();
        for(int i = 0; i < elements.size(); i++) {
            if(elements.get(i).toString().startsWith(prefix.toString())) {
                matchList.add(elements.get(i));
            }
        }
        return matchList;
    }
}
