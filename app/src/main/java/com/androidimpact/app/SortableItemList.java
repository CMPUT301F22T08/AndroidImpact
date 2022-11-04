package com.androidimpact.app;

import java.util.ArrayList;
import java.util.Collections;

public abstract class SortableItemList {
    protected ArrayList<Object> objectArrayList;
    protected static String[] sortChoices;
    protected int sortIndex;
    protected ArrayList<Comparable<Object>> comparators;

    public SortableItemList(ArrayList<Object> objectArrayList, String[] sortChoices) {
        this.objectArrayList = objectArrayList;
        this.sortIndex = 0;
        this.sortChoices = sortChoices;
    }

    /**
     * this function returns the element at i index in the list
     * @param i (int)
     * @return (Object)
     */
    public Object get(int i) {
        return this.objectArrayList.get(i);
    }

    /**
     * this function sets the recipes to index i in SortedItemList
     * @param i (int)
     * @param object (Object)
     */
    public void set(int i, Object object){
        this.objectArrayList.set(i, object);
    }

    /**
     * this function adds the recipe to SortedItemList
     * @param object (Object)
     */
    public void add(Object object)
    {

        this.objectArrayList.add(object);
    }

    /**
     * This function removes the element from sortedItemList at index i
     * @param i (int)
     */
    public void remove(int i)
    {
        if (i < this.size() && i >= 0)
            this.objectArrayList.remove(i);
        else
            throw new ArrayIndexOutOfBoundsException("please choose a i between 0 and list size");
    }

    /**
     * This function removes the recipe from the sortedItemList
     * @param object (Object)
     */
    public void remove(Object object)
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
}
