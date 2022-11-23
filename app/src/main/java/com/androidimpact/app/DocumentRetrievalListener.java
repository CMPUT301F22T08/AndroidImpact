package com.androidimpact.app;

/**
 * This interface lets consumers subscribe to various callbacks when retrieving documents from firebase
 * e.g. see Ingredient.getUnitAsync
 * @param <T>
 * @version 1.0
 * @author: Joshua Ji
 */
public interface DocumentRetrievalListener<T> {
    // document has been successfully loaded, with the correct type
    void onSuccess(T data);
    // document cannot be found
    void onNullDocument();
    // something went wrong
    void onError(Exception e);
}
