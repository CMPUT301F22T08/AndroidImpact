package com.androidimpact.app.recipes;

import android.content.Context;
import android.util.Log;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecipeController {
    final String TAG = "RecipeController";
    final String firestorePath = "recipes";

    private Context context;
    private FirebaseFirestore db;
    private CollectionReference recipeCollection;
    private StorageReference photoStorage;
    private RecipeList recipeList;

    public RecipeController(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        recipeCollection = db.collection(firestorePath);
        recipeList = new RecipeList();
        photoStorage = FirebaseStorage.getInstance().getReference();
    }

    private void pushSnackBarToContext(String s) {
        Snackbar.make(((MainActivity)context).findViewById(R.id.nav_fragment), s, Snackbar.LENGTH_LONG)
                .setAction("OK", (v)->{}).show();
    }

    public void addDataUpdateSnapshotListener(RecipeListAdapter recipeListAdapter){
        recipeCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            // Clear the old list
            recipeList.clear();

            if (queryDocumentSnapshots == null) {
                return;
            }
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Recipe recipeToAdd = doc.toObject(Recipe.class);
                recipeList.add(recipeToAdd); // Adding the recipe attributes from FireStore
            }

            Log.i(TAG, "Snapshot listener: Added " + recipeList.size() + " elements");
            recipeListAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
        });
    }

    public void addEdit(Recipe recipe){
        // Adds if id is null else edits
        String id = recipe.getId();
        if (id == null){
            UUID uuid = UUID.randomUUID();
            id = uuid.toString();
            recipe.setID(id);
        }
        recipeCollection.document(id).set(recipe);
    }


    public void delete(int position) {

        // Get the swiped item at a particular position.
        Recipe deletedRecipe = recipeList.get(position);
        String title = deletedRecipe.getTitle();
        String id = deletedRecipe.getId();
        String photo = deletedRecipe.getPhoto();

        List<Task<?>> futures = new ArrayList<>();

        // Delete all the ingredients associated with the Recipe
        CollectionReference ingredients = db.collection(deletedRecipe.getCollectionPath());
        ingredients.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                futures.add(ingredients.document(doc.getId()).delete());
            }
        });

        // Delete the photo associated with the Recipe
        if (photo != null) {
            futures.add(photoStorage.child("images/" + photo).delete()
                    .addOnSuccessListener(aVoid -> Log.i(TAG, "Successfully deleted image from recipe: " + deletedRecipe.getTitle())));
        }

        // Delete the Recipe
        futures.add(recipeCollection.document(id)
                .delete()
                .addOnSuccessListener(unused -> Log.i(TAG, "Successfully deleted recipeDocument: " + deletedRecipe.getTitle())));

        Tasks.whenAll(futures)
                .addOnSuccessListener(o -> {
                    Log.d(TAG, title + " has been deleted successfully!");
                    pushSnackBarToContext("Deleted " + title);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, title + " could not be deleted!" + e);
                    pushSnackBarToContext("Could not delete " + title);
                });
    }


    public Recipe get(int position) throws IndexOutOfBoundsException {
        if (position < recipeList.size() && position>=0){
            return recipeList.get(position);
        }
        throw new IndexOutOfBoundsException("Please enter an invalid index");
    }

    public String[] getSortingChoices() {
        return recipeList.getSortChoices();
    }

    public void sortData(){
        recipeList.sortByChoice();
    }

    public void sortData(int sortChoice){
        recipeList.setSortChoice(sortChoice);
        recipeList.sortByChoice();
    }

    public ArrayList<Recipe> getData(){
        return recipeList.getData();
    }

    // TODO: Get rid of this ASAP
    public RecipeList getRecipeList(){
        return recipeList;
    }

    public void processPhoto(int pos, OnSuccessListener sl, OnFailureListener fl){
        String photoURI = this.get(pos).getPhoto();
        StorageReference photoRef = photoStorage.child("images/" + photoURI);
        photoRef.getDownloadUrl()
                .addOnSuccessListener(sl)
                .addOnFailureListener(fl);
    }

    public int size(){
        return recipeList.size();
    }
}
