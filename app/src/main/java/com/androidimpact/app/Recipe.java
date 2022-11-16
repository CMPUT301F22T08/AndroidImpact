package com.androidimpact.app;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nullable;

/**
 * This class defines a recipe
 * @author Curtis Kan
 * @version 1.0
 */
public class Recipe implements Serializable  {

    @DocumentId
    private String id;
    private String title;
    private int prep_time;
    private int servings;
    private String category;
    private String comments;
    @ServerTimestamp
    private Date date;
    private String photo;
    // the list of ingredients is stored in a separate collection
    private String collectionPath;


    /**
     * Necessary empty constructor for firebase automatic serialization
     */
    public Recipe() {};

    /**
     * Constructor for recipe
     * @param title
     *     This is the name of the recipe
     * @param prep_time
     *     This is the preparation time in minutes
     * @param servings
     *     This is the amount of servings
     * @param category
     *     This is the category of the recipe
     * @param comments
     *     This is the comments regarding the food
     * @param date
     *     This is the date the recipe was created
     * @param photo
     *     UUID of the photo stored in Firebase Storage
     * @param collectionPath
     *     the list of ingredients is stored in a separate collection
     */
    public Recipe(String id, String title, int prep_time, int servings,
                  String category, String comments, Date date, @Nullable String photo, String collectionPath) {
        this.id = id;
        this.title = title;
        this.prep_time = prep_time;
        this.servings = servings;
        this.category = category;
        this.comments = comments;
        this.date = date;
        this.photo = photo;
        this.collectionPath = collectionPath;
    }

    public String getId() {
        return id;
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

    public Date getDate() {
        return date;
    }

    /**
     * This gets the photo of the recipe
     * @return
     *     This is the photo of the recipe
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * This sets the date of the recipe
     * @param photo
     *     This is the photo URI to set
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCollectionPath() {
        return collectionPath;
    }
}
