package com.androidimpact.app;

import com.androidimpact.app.location.Location;
import com.androidimpact.app.unit.Unit;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * StoreIngredient object that is stored in IngredientStorage
 * Extended from Ingredient
 * Extra attributes:
 *  - bestBeforeDate (Calendar) - The best before date for the stored ingredient
 *  - location (String) - Where the ingredient is stored e.g. Pantry or Freezer
 *  - id (String) - id of the ingredient inside Firebase
 * @author Kailash Seshadri
 * @version 1.0
 * @see Ingredient
 */
public class StoreIngredient extends Ingredient implements Serializable {
    @ServerTimestamp
    private Date bestBeforeDate;
    // instead of a document, this is a path to the location document
    // https://stackoverflow.com/a/57225579
    private String locationDocumentPath;
    @DocumentId
    private String id;

    /**
     * Empty constructor or Firebase to use when deserializing
     */
    public StoreIngredient() {}

    /**
     * Constructor for the StoreIngredient class
     * @param id (String) - Document id in Firebase
     * @param description (String) - A short description of the ingredient e.g. peppercorn ranch
     * @param amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
     * @param categoryDocumentPath (String) - Document path of the category
     * @param bestBeforeDate (Date) - The best before date for the stored ingredient
     * @param locationDocumentPath (String) - document path of where the ingredient is being stored
     * @param unitDocumentPath (String) - document path of where the unit is being stored
     * @see Ingredient
     */
    public StoreIngredient(String id, String description, float amount, String categoryDocumentPath,
                           Date bestBeforeDate, String locationDocumentPath, String unitDocumentPath){
        super(description, amount, unitDocumentPath, categoryDocumentPath);
        this.id = id;
        this.bestBeforeDate = bestBeforeDate;
        this.locationDocumentPath = locationDocumentPath;
    }

    /**
     * Gets the id of the element
     * @return (String) the id of the document
     */
    public String getId() {
        return id;
    }

    /**
     * Get the best-before date of the stored ingredient
     * @return (Date) The best before date for the stored ingredient
     */
    public Date getBestBeforeDate() {
        return bestBeforeDate;
    }

    /**
     * Get the best-before date of the stored ingredient as a Calendar object
     * @return (Calendar) The best before date for the stored ingredient
     */
    @Exclude
    public Calendar getBestBeforeCalendar(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, bestBeforeDate.getYear());
        cal.set(Calendar.MONTH, bestBeforeDate.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, bestBeforeDate.getDate());
        return cal;
    }

    /**
     * Change the best-before date of the stored ingredient
     * @param bestBeforeDate (Date) - The new best-before date of the stored ingredient
     */
    public void setBestBeforeDate(Date bestBeforeDate) {
        this.bestBeforeDate = bestBeforeDate;
    }


    /**
     * gets the location document path
     * not used by the user, but used by Firebase to know they need to serialize the locationDocumentPath
     * @return
     */
    public String getLocationDocumentPath() {
        return locationDocumentPath;
    }

    /**
     * Get the location document - where the ingredient is currently stored
     *
     * @return (DocumentReference) A (possibly null) reference to the a location document
     */
    @Exclude
    public DocumentReference getLocationDocument() {
        return FirebaseFirestore.getInstance().document(locationDocumentPath);
    }

    /**
     * A fully-featured function to retrieve the location from firestore
     *
     * this architecture lets us reduce the callback hell somewhat using listeners, i think...
     */
    @Exclude
    public void getLocationAsync(DocumentRetrievalListener<Location> listener) {
        FirebaseFirestore.getInstance().document(locationDocumentPath).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Location u = document.toObject(Location.class);
                    listener.onSuccess(u);
                } else {
                    listener.onNullDocument();
                }
            } else {
                listener.onError(task.getException());
            }
        });
    }


}
