package ph.alephzero.finance.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class, used to store dates in dated CashFlows
 * 
 * @author jon
 *
 * @param <E>
 */
public class SortedList<E extends Comparable<E>> {
    private LinkedList<E> store;
    
    public SortedList() {
        store = new LinkedList<E>();
    }
    
    public void add(E element) {
        if (store.contains(element)) return;
        
        int i = 0;
        for (E elem : store) {
            if (elem.compareTo(element) > 0) break;
            i++;
        }
        
        store.add(i, element);
    }
    
    public boolean remove(E element) {
        return store.remove(element);
    }
    
    public E get(int i) {        
        return store.get(i);
    }
    
    public int indexOf(E element) {
        return store.indexOf(element);
    }
    
    public int size() {
        return store.size();
    }
    
    public boolean contains(E element) {
        return store.contains(element);
    }

    public List<E> list() {
        return Collections.unmodifiableList(store);
    }
}
