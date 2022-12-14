package com.androidimpact.app.shopping_list;

import com.androidimpact.app.SortableItemList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Class for Shopping List
 * @version 1.0
 * @author vedantvyas
 */
public class ShoppingList extends SortableItemList<ShopIngredient> {

    private static String[] sortChoices;

    /**
     * Constructor for ShoppingList
     * @param shoppingDataList  datalist of type ArrayList<ShopIngredient> that will populate shopping list
     **/
    public ShoppingList(ArrayList<ShopIngredient> shoppingDataList)
    {
        super(shoppingDataList,
                new String[]{
                        "Description",
                        "ShopIngredient Category"
                },
                new Comparator[]{
                        Comparator.comparing(ShopIngredient::getDescription, String.CASE_INSENSITIVE_ORDER),
                        Comparator.comparing(ShopIngredient::getCategory, String.CASE_INSENSITIVE_ORDER)
                }
        );

        sortChoices = new String[]{
                "Description",
                "ShopIngredient Category"
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
