package com.androidimpact.app;

import java.util.ArrayList;

/**
 * This class defines a recipe
 * @author Curtis Kan
 * @version 1.0
 */
public class Recipe {

    private ArrayList<Ingredient> ingredients;
    private String title;
    private int prep_time;
    private int servings;
    private String category;
    private String comments;
    private String date;

    /**
     * Constructor for recipe
     * @param ingredients
     *     This is the list of ingredients at initialization
     * @param title
     *     This is the name of the recipe
     * @param prep_time
     *     This is the preparation time in seconds
     * @param servings
     *     This is the amount of servings
     * @param category
     *     This is the category of the recipe
     * @param comments
     *     This is the comments regarding the food
     * @param date
     *     This is the date the recipe was created
     */
    public Recipe(ArrayList<Ingredient> ingredients, String title, int prep_time, int servings,
                  String category, String comments, String date) {
        this.ingredients = new ArrayList<>();
        for (Ingredient i : ingredients) {
            this.addIngredient(i);
        }
        this.title = title;
        this.prep_time = prep_time;
        this.servings = servings;
        this.category = category;
        this.comments = comments;
        this.date = date;
    }

    /**
     * This adds an ingredient to the recipe
     * @param ingredient
     *     This is the ingredient to add
     */
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    /**
     * This removes an ingredient from the recipe
     * @param ingredient
     *     This is the ingredient to remove
     */
    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
    }

    /**
     * This edits an ingredient of the recipe
     * @param index
     *     This is the index of the ingredient to edit
     * @param ingredient
     *     This is the ingredient to replace it with
     */
    public void editIngredient(int index, Ingredient ingredient) {
        ingredients.set(index, ingredient);
    }

    /**
     * This gets the list of ingredients
     * @return
     *     Return the list of ingredients
     */
    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * This gets the title of the recipe
     * @return
     *     Return the title of the recipe
     */
    public String getTitle() {
        return title;
    }

    /**
     * This sets the title of the recipe
     * @param title
     *     This is the title to set for the recipe
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This gets the preparation time of the recipe
     * @return
     *     Return the preparation time of the recipe
     */
    public int getPrep_time() {
        return prep_time;
    }

    /**
     * This sets the preparation time of the recipe
     * @param prep_time
     *     This is the preparation time to set for the recipe
     */
    public void setPrep_time(int prep_time) {
        this.prep_time = prep_time;
    }

    /**
     * This gets the servings of the recipe
     * @return
     *     Return the servings of the recipe
     */
    public int getServings() {
        return servings;
    }

    /**
     * This sets the servings of the recipe
     * @param servings
     *     These are the servings to set for the recipe
     */
    public void setServings(int servings) {
        this.servings = servings;
    }

    /**
     * This gets the category of the recipe
     * @return
     *     Return the category of the recipe
     */
    public String getCategory() {
        return category;
    }

    /**
     * This sets the category of the recipe
     * @param category
     *     This is the category to set for the recipe
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * This gets the comments of the recipe
     * @return
     *     Return the comments of the recipe
     */
    public String getComments() {
        return comments;
    }

    /**
     * This sets the title of the recipe
     * @param comments
     *     These are the comments to set for the recipe
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * This gets the date of the recipe
     * @return
     *     This is the date of the recipe
     */
    public String getDate() {
        return date;
    }

    /**
     * This sets the date of the recipe
     * @param date
     *     This is the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

}
