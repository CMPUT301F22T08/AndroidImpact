package com.androidimpact.app.meal_plan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.androidimpact.app.ingredients.IngredientStorage;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.shopping_list.ShopIngredient;
import com.androidimpact.app.shopping_list.ShoppingList;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class mealPlanListTest {


    /**
     * This function creates a meal plan list with a single mealPlan and returns
     * @return type(MealPlanList)
     */
    private MealPlanList mockMealPlanList()
    {
        ArrayList<MealPlan> objectArrayList = new ArrayList<>();
        MealPlanList mealPlanList = new MealPlanList(objectArrayList);
        mealPlanList.add(mockMealPlan());

        return mealPlanList;
    }

    /**
     * This function creates a meal plan object and returns it
     * @return type(MealPlan)
     */
    private MealPlan mockMealPlan()
    {

        return new MealPlan("June 1",
                "1" );
    }

    /**
     * This function creates a meal plan object and returns it
     * @return type(MealPlan)
     */
    private MealPlan mockMealPlan(String date)
    {

        return new MealPlan(date,
                "1" );
    }


    /**
     * This function tests addition of mealPlans to mealPlan list
     */
    @Test
    public void testAdd(){
        MealPlanList mealPlanList = mockMealPlanList();
        assertEquals(1, mealPlanList.size());
        MealPlan mealPlan = mockMealPlan();
        mealPlanList.add(mealPlan);
        assertEquals(2, mealPlanList.size());
    }


    /**
     * This function checks the getter method getmealPlan()
     */
    @Test
    public void testGetMealPlan() {
        MealPlanList mealPlanList = mockMealPlanList();
        assertEquals(1, mealPlanList.size());
        MealPlan mealPlan = mockMealPlan("july 1");
        mealPlanList.add(mealPlan);

        //to add a compareTo method in store mealPlan
        assertEquals(true, mealPlan == mealPlanList.get(1));

        assertEquals(false, mealPlan == mealPlanList.get(0));
    }

    /**
     *  this function allows us to test setmealPlan() method
     */
    @Test
    public void testSetMealPlan() {

        MealPlanList mealPlanList = mockMealPlanList();
        assertEquals(1, mealPlanList.size());
        MealPlan mealPlan = mockMealPlan("july 1");
        mealPlanList.set(0, mealPlan);

        //to add a compareTo method in store mealPlan

        assertEquals(true,  mealPlan == mealPlanList.get(0));

    }

    /**
     * This function tests if we can remove the mealPlan from mealPlanList
     */
    @Test
    public void testRemove()
    {

        MealPlanList mealPlanList = mockMealPlanList();
        MealPlan mealPlan = mockMealPlan("july 1");
        mealPlanList.add(mealPlan);

        assertEquals(2, mealPlanList.size());
        mealPlanList.remove(1);
        assertEquals(1, mealPlanList.size());

        // test deleting invalid store mealPlan
        assertThrows( ArrayIndexOutOfBoundsException.class, () -> {
            mealPlanList.remove(4); });


        assertEquals(1, mealPlanList.size());
    }

    /**
     * Test that the default sort choice is as below
     * Initialize the mealPlanlist and ensure value of default sort choice is "Date"
     */
    @Test
    public void getSortChoiceTest() {
        MealPlanList mealPlanList = mockMealPlanList();
        assertEquals(mealPlanList.getSortChoice(), "Date");
    }


    /**
     * Test that the set sort choice is correct
     * Initialize the recipe list and ensure the value when we set the sort choice to one of our
     * choosing, then it reflects the choice we want
     */
    @Test
    public void setSortChoiceTest() {
        MealPlanList mealPlanList = mockMealPlanList();
        //only one possible sort choice but still for the sake of testing
        mealPlanList.setSortChoice(0);
        assertEquals(mealPlanList.getSortChoice(), "Date");

        // testing when we set invalid choice for sorting
        mealPlanList.setSortChoice(1);
        assertThrows( ArrayIndexOutOfBoundsException.class, () -> {
           mealPlanList.getSortChoice() ; });
    }


    /**
     * This function tests if clear method for mealPlan List works properly
     */
    @Test
    public void testClear()
    {
        MealPlanList mealPlanList = mockMealPlanList();
        MealPlan mealPlan = mockMealPlan("july 1");
        mealPlanList.add(mealPlan);
        assertEquals(2, mealPlanList.size());
        mealPlanList.clear();
        assertEquals(0, mealPlanList.size());

    }

    /**
     * This function tests if size method for mealPlan List works properly
     */
    @Test
    public void testSize()
    {
        MealPlanList mealPlanList = mockMealPlanList();
        assertEquals(1, mealPlanList.size());
        MealPlan mealPlan = mockMealPlan();
        mealPlanList.add(mealPlan);
        assertEquals(2, mealPlanList.size());
        mealPlanList.clear();
        assertEquals(0, mealPlanList.size());

    }


}
