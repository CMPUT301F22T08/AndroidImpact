package com.androidimpact.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.androidimpact.app.recipes.Recipe;

import org.junit.Test;

import java.util.Date;

/**
 * Tests for the Recipe class
 * <br>
 * As this class largely consists of getters and setters, the getters were tested by getting the value and asserting it was equal to the initial.
 * The setters were tested by first asserting the initial values were correctly retrieved, and then changing the value and then asserting the values changed accordingly.
 *
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class RecipeTest {

    private String id = "MockRecipeId";
    private String title = "Mac and Cheese";
    private int prep_time = 2;
    private int servings = 3;
    private String category = "Lunch";
    private String comments = "-Add milk";
    private Date date = new Date();
    private String photo = "MockPhotoAddress";
    private String collectionPath = "MockCollectionPath";

    /**
     * Returns a mock recipe object
     * @return (Recipe) a mock recipe to test the class functions
     */
    private Recipe mockRecipe() {
        return new Recipe(id, title, prep_time, servings, category, comments, date, photo, collectionPath);
    }

    /**
     * Test for getter function getId()
     */
    @Test
    public void testGetId() {
        Recipe recipe = mockRecipe();
        assertEquals(id, recipe.getId());
    }

    /**
     * Test for getter function getTitle()
     */
    @Test
    public void testGetTitle() {
        Recipe recipe = mockRecipe();
        assertEquals(title, recipe.getTitle());
    }

    /**
     * Test for setter function setTitle()
     */
    @Test
    public void testSetTitle() {
        Recipe recipe = mockRecipe();
        assertEquals(title, recipe.getTitle());
        String newTitle = "newTitle";
        recipe.setTitle(newTitle);
        assertEquals(newTitle, recipe.getTitle());
    }

    /**
     * Test for getter function getPrep_time()
     */
    @Test
    public void testGetPrepTime() {
        Recipe recipe = mockRecipe();
        assertEquals(prep_time, recipe.getPrep_time());
    }

    /**
     * Test for setter function setPrep_time()
     */
    @Test
    public void testSetPrepTime() {
        Recipe recipe = mockRecipe();
        assertEquals(prep_time, recipe.getPrep_time());
        int newPrepTime = recipe.getPrep_time() + 1;
        recipe.setPrep_time(newPrepTime);
        assertEquals(newPrepTime, recipe.getPrep_time());
    }

    /**
     * Test for getter function getServings()
     */
    @Test
    public void testGetServings() {
        Recipe recipe = mockRecipe();
        assertEquals(servings, recipe.getServings());
    }

    /**
     * Test for setter function setServings()
     */
    @Test
    public void testSetServings() {
        Recipe recipe = mockRecipe();
        assertEquals(servings, recipe.getServings());
        int newServings = recipe.getServings() + 1;
        recipe.setServings(newServings);
        assertEquals(newServings, recipe.getServings());
    }

    /**
     * Test for getter function getCategory()
     */
    @Test
    public void testGetCategory() {
        Recipe recipe = mockRecipe();
        assertEquals(category, recipe.getCategory());
    }

    /**
     * Test for setter function setCategory()
     */
    @Test
    public void testSetCategory() {
        Recipe recipe = mockRecipe();
        assertEquals(category, recipe.getCategory());
        String newCategory = recipe.getCategory() + "!";
        recipe.setCategory(newCategory);
        assertEquals(newCategory, recipe.getCategory());
    }

    /**
     * Test for getter function getComments()
     */
    @Test
    public void testGetComments() {
        Recipe recipe = mockRecipe();
        assertEquals(comments, recipe.getComments());
    }

    /**
     * Test for setter function setComments()
     */
    @Test
    public void testSetComments() {
        Recipe recipe = mockRecipe();
        assertEquals(comments, recipe.getComments());
        String newComments = recipe.getComments() + "!";
        recipe.setComments(newComments);
        assertEquals(newComments, recipe.getComments());
    }

    /**
     * Test for getter function getDate()
     */
    @Test
    public void testGetDate() {
        Recipe recipe = mockRecipe();
        assertTrue(date.equals(recipe.getDate()));
    }

    /**
     * Test for getter function getPhoto()
     */
    @Test
    public void testGetPhoto() {
        Recipe recipe = mockRecipe();
        assertEquals(photo, recipe.getPhoto());
    }

    /**
     * Test for setter function setPhoto()
     */
    @Test
    public void testSetPhoto() {
        Recipe recipe = mockRecipe();
        assertEquals(photo, recipe.getPhoto());
        String newPhoto = recipe.getPhoto() + "!";
        recipe.setPhoto(newPhoto);
        assertEquals(newPhoto, recipe.getPhoto());
    }

    /**
     * Test for getter function getCollectionPath()
     */
    @Test
    public void testGetCollectionPath() {
        Recipe recipe = mockRecipe();
        assertEquals(collectionPath, recipe.getCollectionPath());
    }

    /**
     * Test for setter function setId()
     */
    @Test
    public void testSetId() {
        Recipe recipe = new Recipe();
        String newId = "new id";
        recipe.setID(newId);
        assertEquals(newId, recipe.getId());
    }

    /**
     * Test that setId() throws appropriate error
     */
    @Test
    public void testSetIdThrows() {
        Recipe recipe = mockRecipe();
        assertThrows(IllegalArgumentException.class, () -> recipe.setID("new id"));
    }
}