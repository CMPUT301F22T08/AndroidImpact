package com.androidimpact.app;

import java.util.Date;

/**
 * This interface defines classes that has the getDate method
 *
 * This is useful in AddEditStoreIngredientActivity, where we make some abstractions in the code and
 * need to guarantee that our classes can be sorted by date.
 */
public interface Timestamped {
    public Date getDateAdded();
}
