package com.androidimpact.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * This class creates the functionality to have lists with sorting ability
 * @author Aneeljyot Alagh
 * @version 1.0
 */
public abstract class SortableItemList<T> {
    protected ArrayList<T> objectArrayList;
    private Comparator<T>[] comparators;
    private OrderedHashMap<String, Comparator<T>> sortingHashMap;
    private int sortChoice;

    //`
    private String[] sortChoices;

    /**
     * Constructor for SortableItemList class
     * @param objectArrayList
     * @param sortChoices
     */
    public SortableItemList(ArrayList<T> objectArrayList, String[] sortChoices, Comparator<T>[] comparators) {
        this.objectArrayList = objectArrayList;
        this.sortChoice = 0;
        this.comparators = comparators;
        this.sortChoices = sortChoices;
        this.sortingHashMap = new OrderedHashMap<>(sortChoices, comparators);
    }

    /**
     * This function returns the data in the list
     * @return list (ArrayList&lt;T&gt;)
     */
    public ArrayList<T> getData() { return this.objectArrayList; }

    /**
     * this function returns the element at i index in the list
     * @param i (int)
     * @return object (T) at position i
     */
    public T get(int i)  throws ArrayIndexOutOfBoundsException{
        if (i < this.size() && i >= 0)
            return this.objectArrayList.get(i);
        else
            throw new ArrayIndexOutOfBoundsException("Trying to get item at an invalid index");
    }

    /**
     * this function sets the object at index i in SortedItemList to a new item
     * @param i (int)
     * @param object (T)
     */
    public void set(int i, T object){
        if (i < this.size() && i >= 0)
            this.objectArrayList.set(i, object);
        else if (i > this.size()){
            this.objectArrayList.set(this.size()-1, object);
        }
        else{
            this.objectArrayList.set(0, object);
        }
    }

    /**
     * this function adds the object to SortedItemList
     * @param object (T)
     */
    public void add(T object)
    {
        this.objectArrayList.add(object);
    }

    /**
     * This function removes the element from sortedItemList at index i
     * @param i (int)
     */
    public void remove(int i) throws ArrayIndexOutOfBoundsException
    {
        if (i < this.size() && i >= 0)
            this.objectArrayList.remove(i);
        else
            throw new ArrayIndexOutOfBoundsException("Trying to remove item at an index not between 0 and list size");
    }

    /**
     * this function returns the size of the list
     * @return (int) integer length of item list
     */
    public int size()
    {
        return this.objectArrayList.size();
    }

    /**
     * Set the sorting choice for the item list
     * @param i the index of the sorting choices for the user
     */
    public void setSortChoice(int i) {
        this.sortChoice = i;
    }


    /**
     * Get the sorting choice for the item list
     */
    public String getSortChoice() {
        return sortChoices[this.sortChoice];
    }



    /**
     * This function allows us to sort the item list by the user's choice
     */
    public void sortByChoice() {
        Collections.sort(this.objectArrayList, this.sortingHashMap.getValue(this.sortChoice));
    }

    /**
     * Clears the list
     */
    public void clear() {
        this.objectArrayList.clear();
    }

    /**
     * This class creates a version of a hashmap that maintains an order.
     * @param <K> The key type
     * @param <V> The value type
     */
    private class OrderedHashMap<K, V> extends HashMap<K, V> {
        private K[] keys;

        /**
         * Initialize a hashmap that contains a natural ordering.
         * <br>
         * <strong>NB:</strong> This structure requires that the length of keys given and values
         * given are equal, and the ith key corresponds to the ith value in the list.
         * <strong>NB:</strong> If two keys are given that are equal, the hashmap will only consider
         * the latter of these in the keys array, i.e. the key with the maximal i-value.
         * @param keys an array of keys to be added
         * @param values an array of values to be added
         * @throws IllegalArgumentException if the arrays of keys and values have different sizes
         */
        public OrderedHashMap(K[] keys, V[] values) throws IllegalArgumentException {
            super();
            if(keys.length == values.length) {
                this.keys = keys;
                for(int i = 0; i < keys.length; i++) {
                    super.put(keys[i], values[i]);
                }
            } else {
                throw new IllegalArgumentException("Incorrect arguments, too many " + (keys.length > values.length ? "keys": "values") + "given.");
            }
        }

        /**
         * Returns the ith key in the hashmap.
         * @param i the index of the key in the hashmap
         * @return the ith key in the hashmap (K)
         */
        public K getKey(int i) {
            return keys[i];
        }

        /**
         * Returns the ith value in the hashmap.
         * @param i the index of the value in the hashmap
         * @return the value of the ith key in the hashmap (V)
         */
        public V getValue(int i) {
            return super.get(keys[i]);
        }
    }
}
