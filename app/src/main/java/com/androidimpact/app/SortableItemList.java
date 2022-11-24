package com.androidimpact.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * This class creates the functionality to have lists with sorting ability
 * @author Aneeljyot Alagh
 * @version 1.0
 */
public abstract class SortableItemList<T> {
    protected ArrayList<T> objectArrayList;
    //protected static String[] sortChoices;
    private Comparator<T>[] comparators;
    private OrderedHashMap<String, Comparator<T>> sortingHashMap;
    private int sortIndex;

    /**
     * Constructor for SortableItemList class
     * @param objectArrayList
     * @param sortChoices
     */
    public SortableItemList(ArrayList<T> objectArrayList, String[] sortChoices, Comparator<T>[] comparators) {
        this.objectArrayList = objectArrayList;
        this.sortIndex = 0;
        //this.sortChoices = sortChoices;
        this.comparators = comparators;
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
    public T get(int i) {
        return this.objectArrayList.get(i);
    }

    /**
     * this function sets the object at index i in SortedItemList to a new item
     * @param i (int)
     * @param object (T)
     */
    public void set(int i, T object){
        this.objectArrayList.set(i, object);
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
     * This function removes the recipe from the sortedItemList
     * @param object (Object)
     */
    public void remove(T object) throws IllegalArgumentException
    {
        if (this.objectArrayList.contains(object))
            this.objectArrayList.remove(object);
        else
            throw new IllegalArgumentException("Trying to remove item not in list");
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
     * Return the current sorting choice for the item list
     * @return the sorting choice at the index chosen by the user
     */
    public String getSortChoice() {
        //return this.sortChoices[this.sortIndex];
        return this.sortingHashMap.getKey(this.sortIndex);
    }

    /**
     * Set the sorting choice for the item list
     * @param index the index of the sorting choices for the user
     */
    public void setSortChoice(int index) {
        this.sortIndex = index;
    }

    /**
     * Return the sorting choices for the item list
     * @return list of available sorting choices
     */
//    public static String[] getSortChoices() {
//        return sortChoices.clone();
//    }

    /**
     * This function allows us to sort the item list by the user's choice
     */
    public void sortByChoice() {
        Collections.sort(this.objectArrayList, this.sortingHashMap.getValue(this.sortIndex));
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
         * given are equal, and the i'th key corresponds to the i'th value in the list.
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
         * Returns the i'th key in the hashmap.
         * @param i the index of the key in the hashmap
         * @return the i'th key in the hashmap (K)
         */
        public K getKey(int i) {
            return keys[i];
        }

        /**
         * Returns the i'th value in the hashmap.
         * @param i the index of the value in the hashmap
         * @return the value of the i'th key in the hashmap (V)
         */
        public V getValue(int i) {
            return super.get(keys[i]);
        }

        /**
         * Returns a copy of the keys in the OrderedHashMap
         * @return an array of the keys (K[])
         */
        public K[] getKeys() {
            return keys.clone();
        }
    }
}
