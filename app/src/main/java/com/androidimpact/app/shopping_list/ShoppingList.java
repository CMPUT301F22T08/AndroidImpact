package com.androidimpact.app.shopping_list;

import com.androidimpact.app.SortableItemList;
import com.androidimpact.app.ingredients.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author vedantvyas
 */
public class ShoppingList extends SortableItemList<Ingredient> {

    private static String[] sortChoices;

    /**
     * Constructor for ShoppingList
     * @param shoppingDataList  datalist of type ArrayList<Ingredient> that will populate shopping list
     **/
    public ShoppingList(ArrayList<Ingredient> shoppingDataList)
    {
        super(shoppingDataList,
                new String[]{
                        "Description",
                        "Ingredient Category"
                },
                (Comparator<Ingredient>[]) Arrays.asList(
                        Comparator.comparing(Ingredient::getDescription, String.CASE_INSENSITIVE_ORDER),
                        Comparator.comparing(Ingredient::getCategory, String.CASE_INSENSITIVE_ORDER)
                ).toArray()
        );

        sortChoices = new String[]{
                "Description",
                "Ingredient Category"
        };
    }

    /**
     * Return the sorting choices for the shopping list
     * @return list of available sorting choices
     */
    public static String[] getSortChoices() {
        return sortChoices.clone(); }

    /**
     * this function clears the shopping list data
     */
    public void clear()
    {
        this.getData().clear();
    }
}
