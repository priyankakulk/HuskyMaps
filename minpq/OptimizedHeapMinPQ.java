package minpq;

import java.util.*;

/**
 * Optimized binary heap implementation of the {@link MinPQ} interface.
 *
 * @param <E> the type of elements in this priority queue.
 * @see MinPQ
 */
public class OptimizedHeapMinPQ<E> implements MinPQ<E> {
    /**
     * {@link List} of {@link PriorityNode} objects representing the heap of element-priority pairs.
     */
    private final List<PriorityNode<E>> elements;
    /**
     * {@link Map} of each element to its associated index in the {@code elements} heap.
     */
    private final Map<E, Integer> elementsToIndex;

    /**
     * Constructs an empty instance.
     */
    public OptimizedHeapMinPQ() {
        elements = new ArrayList<>();
        elementsToIndex = new HashMap<>();
        elements.add(null);
    }

    /**
     * Constructs an instance containing all the given elements and their priority values.
     *
     * @param elementsAndPriorities each element and its corresponding priority.
     */
    public OptimizedHeapMinPQ(Map<E, Double> elementsAndPriorities) {
        elements = new ArrayList<>(elementsAndPriorities.size());
        elementsToIndex = new HashMap<>(elementsAndPriorities.size());
        elements.add(null);

        for (Map.Entry<E, Double> entry : elementsAndPriorities.entrySet()) {
            elements.add(new PriorityNode<>(entry.getKey(), entry.getValue()));
        }

        for(int k = (size()/2); k >= 1; k--) {
            sink(k);
        }

        for(int i = 1; i < elements.size(); i++) {
            elementsToIndex.put(elements.get(i).getElement(), i);
        }
    }

    @Override
    public void add(E element, double priority) {
        if (contains(element)) {
            throw new IllegalArgumentException("Already contains " + element);
        }
        elements.add(new PriorityNode<E>(element, priority));
        elementsToIndex.put(element, size());
        swim(size());
    }

    private void sink(int k) {
        while(2*k <= size()) {
            int j = 2*k;
            if(j<size() && greater(j, j+1)) {
                j++;
            }
            if(!greater(k,j)) {
                break;
            }
            exchange(k,j);
            k = j;
        }
    }

    private void swim(int k) {
        while(k > 1 && greater(k/2, k)) {
            exchange(k/2,k);
            k = k/2;
        }
    }

    private boolean greater(int i, int j) {
        if(elements.get(i).getPriority() > elements.get(j).getPriority()) {
            return true;
        }
        return false;
    }

    private void exchange(int i, int j) {
        PriorityNode<E> swap1 = elements.get(i);
        PriorityNode<E> swap2 = elements.get(j);
        elements.set(j, swap1);
        elements.set(i, swap2);

        elementsToIndex.put(elements.get(i).getElement(), i);
        elementsToIndex.put(elements.get(j).getElement(), j);
    }

    @Override
    public boolean contains(E element) {
        if(elementsToIndex.containsKey(element)) {
            return true;
        }
        return false;
    }

    @Override
    public double getPriority(E element) {
        PriorityNode<E> curr = elements.get(elementsToIndex.get(element));
        return curr.getPriority();
    }

    @Override
    public E peekMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("PQ is empty");
        }
        return elements.get(1).getElement();
    }

    @Override
    public E removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("PQ is empty");
        }
        PriorityNode<E> min = elements.get(1);
        exchange(1, size());
        elements.remove(size());
        elementsToIndex.remove(min.getElement());
        sink(1);
        return min.getElement();
    }

    @Override
    public void changePriority(E element, double priority) {
        if (!contains(element)) {
            throw new NoSuchElementException("PQ does not contain " + element);
        }
        int index = elementsToIndex.get(element);
        double oldPriority = elements.get(index).getPriority();
        elements.get(index).setPriority(priority);
        
        if(priority < oldPriority) {
            swim(index);
        }
        else {
            sink(index);
        }
    }
    
    @Override
    public int size() {
        return elements.size() - 1;
    }
}
