package autocomplete;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * Ternary search tree (TST) implementation of the {@link Autocomplete} interface.
 *
 * @see Autocomplete
 */
public class TernarySearchTreeAutocomplete implements Autocomplete {
    /**
     * The overall root of the tree: the first character of the first autocompletion term added to this tree.
     */
    private Node overallRoot;

    /**
     * Constructs an empty instance.
     */
    public TernarySearchTreeAutocomplete() { 
        overallRoot = null; 
    } 
    
    private Node put(Node node, CharSequence key, int i) { 
        char c = key.charAt(i); 
        if(node == null) { 
            node = new Node(c); 
        } 
        
        if(c < node.data){ 
            node.left = put(node.left, key, i); 
        } 
    
        else if (c > node.data) { 
            node.right = put(node.right, key, i); 
        } 
    
        else if (i < key.length() - 1) { 
            node.mid = put(node.mid, key, i+1); 
        
        } else {
            node.isTerm = true; 	
        } 
        
        return node; 
    
    } 

    @Override 
    public void addAll(Collection<? extends CharSequence> terms) { 
        for (CharSequence c : terms) { 
            overallRoot = put(overallRoot, c, 0); 
        } 
    } 
    
    @Override 
    public List<CharSequence> allMatches(CharSequence prefix) { 
        if(prefix == null) {
            throw new IllegalArgumentException("calls with null");
        }
        List<CharSequence> matches = new ArrayList<>(); 
        Node x = get(overallRoot, prefix, 0); 
        if(x == null){ 
            return matches; 
        } 
        
        if(x.isTerm){ 
            matches.add(prefix); 
        } 
        collect(x.mid, new StringBuilder(prefix), matches); 
        return matches; 
    } 
    
    
    private Node get(Node node, CharSequence key, int i) {
        if(node == null) {
            return null;
        }

        if(key.length() == 0) {
            throw new IllegalArgumentException("Key must have length >=1");
        }
        char c = key.charAt(i);
        
        if(c < node.data) {
            return get(node.left, key, i);
        }

        if(c > node.data) {
            return get(node.right, key, i);
        }

        else if(i < key.length() -1) {
            return get(node.mid, key, i + 1);
        }

        else {
            return node;
        }
    }

    private void collect(Node node, StringBuilder prefix, List<CharSequence> matches) {
        if(node == null) {
            return;
        }
        collect(node.left, prefix, matches);

        if(node.isTerm) {
            matches.add(prefix.toString() + node.data);
        }

        collect(node.mid, prefix.append(node.data), matches);
        prefix.deleteCharAt(prefix.length() - 1);

        collect(node.right, prefix, matches);
    }
    private static class Node {
        private final char data;
        private boolean isTerm;
        private Node left;
        private Node mid;
        private Node right;

        public Node(char data) {
            this.data = data;
            this.isTerm = false;
            this.left = null;
            this.mid = null;
            this.right = null;
        }
    }
}
