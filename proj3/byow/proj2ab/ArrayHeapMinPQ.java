package byow.proj2ab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;


public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {

    private ArrayList<T> array = new ArrayList<>();
    private int size = 0;
    private HashMap<T, Values> hash = new HashMap<>();

    public ArrayHeapMinPQ() {
        array.add(null);
    }

    /* Adds an item with the given priority value. Throws an
     * IllegalArgumentException if item is already present. */
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException();
        } else {
            array.add(item);
            int currentIndex = size + 1;
            hash.put(item, new Values(priority, currentIndex));
            shiftUpwards(currentIndex, priority);
            size++;
        }
    }

    /* Returns true if the PQ contains the given item. */
    public boolean contains(T item) {
        return hash.containsKey(item);
    }

    /* Returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    public T getSmallest() {
        if (size == 0) {
            throw new NoSuchElementException();
        } else {
            return array.get(1);
        }
    }

    /* Removes and returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    public T removeSmallest() {
        if (size == 0) {
            throw new NoSuchElementException();
        } else if (size == 1) {
            size--;
            T item = array.remove(1);
            hash.remove(item);
            return item;
        } else {
            T minCopy = array.remove(1);
            array.add(1, array.remove(array.size() - 1));
            hash.get(array.get(1)).index = 1;
            shiftDownwards(1);
            size--;
            hash.remove(minCopy);
            return minCopy;
        }
    }

    /* Returns the number of items in the PQ. */
    public int size() {
        return size;
    }

    /* Changes the priority of the given item. Throws NoSuchElementException if the item
     * doesn't exist. */
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }
        hash.get(item).priority = priority;
        int currentindex = hash.get(item).index;
        int parentindex = currentindex / 2;

        if (parentindex == 0) {
            shiftDownwards(currentindex);
            return;
        }

        double parentPriority = hash.get(array.get(parentindex)).priority;
        if (priority < parentPriority) {
            shiftUpwards(currentindex, priority);
        } else {
            shiftDownwards(currentindex);
        }
    }

    private void shiftUpwards(int currentIndex, double priority) {
        int parentIndex = currentIndex / 2;
        while (parentIndex != 0) {
            T parent = array.get(parentIndex);
            double parentPriority = hash.get(parent).priority;
            if (priority >= parentPriority /*&& (r.nextBoolean() || priority > parentPriority)*/) {
                break;
            } else {
                Collections.swap(array, currentIndex, parentIndex);
                hash.get(array.get(currentIndex)).index = currentIndex;
                hash.get(array.get(parentIndex)).index = parentIndex;
                currentIndex = parentIndex;
                parentIndex = currentIndex / 2;
            }
        }
    }

    private void shiftDownwards(int currentIndex) {
        int leftChildIndex = currentIndex * 2;
        int rightChildIndex = currentIndex * 2 + 1;
        double priority = hash.get(array.get(currentIndex)).priority;
        while (leftChildIndex < array.size() && rightChildIndex < array.size()) {
            double leftPriority = hash.get(array.get(leftChildIndex)).priority;
            double rightPriority = hash.get(array.get(rightChildIndex)).priority;
            if (priority <= leftPriority && priority <= rightPriority /*&& r.nextBoolean()*/) {
                break;
            }
            int minIndex = (leftPriority <= rightPriority /*&& (r.nextBoolean()
                    || leftPriority < rightPriority)*/) ? leftChildIndex : rightChildIndex;
            Collections.swap(array, currentIndex, minIndex);
            hash.get(array.get(currentIndex)).index = currentIndex;
            hash.get(array.get(minIndex)).index = minIndex;
            currentIndex = minIndex;
            leftChildIndex = currentIndex * 2;
            rightChildIndex = currentIndex * 2 + 1;
        }


        if (leftChildIndex < array.size()) {
            if (priority > hash.get(array.get(leftChildIndex)).priority) {
                Collections.swap(array, currentIndex, leftChildIndex);
                hash.get(array.get(currentIndex)).index = currentIndex;
                hash.get(array.get(leftChildIndex)).index = leftChildIndex;
            }
        }
    }

    private class Values {
        double priority;
        int index;

        Values(double priority, int index) {
            this.priority = priority;
            this.index = index;
        }
    }
}
