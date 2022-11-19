package com.androidimpact.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * This class creates the functionality to have lists with sorting ability
 * @version 1.0
 */
public abstract class SortableItemList<T> {
    protected ArrayList<T> objectArrayList;
    protected static String[] sortChoices;
    //protected Comparator<T>[] comparators;
    protected int sortIndex;

    /**
     * Constructor for SortableItemList class
     * @param objectArrayList
     * @param sortChoices
     */
    public SortableItemList(ArrayList<T> objectArrayList, String[] sortChoices) {
        this.objectArrayList = objectArrayList;
        this.sortIndex = 0;
        this.sortChoices = sortChoices;
        //this.comparators = comparators;
    }

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
        return this.sortChoices[this.sortIndex];
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
    public static String[] getSortChoices() {
        return sortChoices.clone();
    }

    /**
     * This function allows us to sort the item list by the user's choice
     */
    public abstract void sortByChoice();

    public class OrderedHashMap extends HashMap<String, Comparator<T>> {
        private String[] keys;

        public OrderedHashMap(String[] keys) {
            super();
            this.keys = keys;
        }

        public Comparator<T> get(int i) {
            return super.get(keys[i]);
        }
    }
}
