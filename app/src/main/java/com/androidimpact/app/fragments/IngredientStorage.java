package com.androidimpact.app.fragments;

import static java.util.Objects.isNull;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidimpact.app.R;
import com.androidimpact.app.StoreIngredient;
import com.androidimpact.app.StoreIngredientViewAdapter;
import com.androidimpact.app.activities.AddEditStoreIngredientActivity;
import com.androidimpact.app.activities.IngredientStorageActivity;
import com.androidimpact.app.activities.MainActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.ValueEventListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IngredientStorage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IngredientStorage extends Fragment {
    final String TAG = "IngredientStorageFragment";

    private static IngredientStorage instance;

    // Declare the variables so that you will be able to reference it later.
    RecyclerView ingredientListView;
    StoreIngredientViewAdapter storeingredientViewAdapter;
    com.androidimpact.app.IngredientStorage ingredientDataList;

    // adding cities to firebase
    FirebaseFirestore db;
    CollectionReference ingredientsCollection;
    FloatingActionButton addIngredientFAB;
    Spinner sortSpinner2;
    String[] sortingChoices;
    TextView sortText;

    public IngredientStorage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IngredientStorage.
     */
    // TODO: Rename and change types and number of parameters
    public static IngredientStorage newInstance() {



        if (instance == null)
        {
            //To be Changed
            IngredientStorage fragment = new IngredientStorage();
            fragment.bootUp();
            //instance = new IngredientStorage();

            return fragment;
        }

        IngredientStorage fragment = new IngredientStorage();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // TODO: change this to fragment_ingredient_storage
        return inflater.inflate(R.layout.activity_ingredient_storage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG+":onViewCreated", "onViewCreated called!");
        super.onViewCreated(view, savedInstanceState);

        super.onCreate(savedInstanceState);

        Activity a = getActivity();

      //  System.out.println(a);

        if (a == null) {
            Log.i(TAG + ":onViewCreated", "Fragment is not associated with an activity!");
            return;
        }

//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    entries.add(ds.getValue(LogEntry.class));
//                }
//
//                // Declare adapter and set here
//
//                // OR... adapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException());
//            }
//        };

        // initialize adapters and customList
        ingredientListView = a.findViewById(R.id.ingredient_listview);

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        ingredientListView.setLayoutManager(manager);
        ingredientListView.setAdapter(storeingredientViewAdapter);


       // MainActivity main = (MainActivity) getActivity();
        addIngredientFAB = a.findViewById(R.id.addStoreIngredientFAB);

        // Launch AddStoreIngredientActivity when FAB is clicked
        addIngredientFAB.setOnClickListener(v -> {
            Log.i(TAG + ":addStoreIngredient", "Adding ingredient!");
            Intent intent = new Intent(getContext(), AddEditStoreIngredientActivity.class);
            addStoreIngredientLauncher.launch(intent);
        });

        // listen for edits in `storeingredientViewAdapter`
        storeingredientViewAdapter.setEditClickListener((storeIngredient, position) -> {
            // runs whenever a store ingredient edit btn is clicked
            Log.i(TAG + ":setEditClickListener", "Editing ingredient at position " + position);
            Intent intent = new Intent(getContext(), AddEditStoreIngredientActivity.class);
            intent.putExtra("storeIngredient", storeIngredient);
            editStoreIngredientLauncher.launch(intent);
        });

        sortSpinner2 = a.findViewById(R.id.sort_ingredient_spinner);
        sortText = a.findViewById(R.id.sort_ingredient_info);

        sortingChoices = ingredientDataList.getSortChoices();
        ArrayAdapter<String> sortingOptionsAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item,
                sortingChoices
        );
        sortingOptionsAdapter.setDropDownViewResource(
                android.R.layout.simple_list_item_1
        );
        sortSpinner2.setAdapter(sortingOptionsAdapter);

        sortSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ingredientDataList.setSortChoice(i);
                ingredientDataList.sortByChoice();
                //  sortText.setText("Sort by: "+ ingredientDataList.getSortChoice());
                storeingredientViewAdapter.notifyDataSetChanged();
            }

            /**
             * This function ensures default sorting if no other sorting selected
             * @param adapterView   The adapterView that does not contain any user selection
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ingredientDataList.setSortChoice(0);
                ingredientDataList.sortByChoice();
                //  sortText.setText("Sort by: "+ ingredientDataList.getSortChoice());

                storeingredientViewAdapter.notifyDataSetChanged();
            }
        });

        // drag to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // this method is called when the item is moved.
                return false;
            }

            @Override
            // this method is called when we swipe our item to right direction.
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // get the position of the selected item
                int position = viewHolder.getAdapterPosition();

                // Get the swiped item at a particular position.
                StoreIngredient deletedIngredient = ingredientDataList.get(position);
                String description = deletedIngredient.getDescription();
                String id = deletedIngredient.getId();

                Log.d(TAG, "Swiped " + description + " at position " + position);

                // delete item from firebase
                ingredientsCollection.document(id)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, description + " has been deleted successfully!");
                            Snackbar.make(a.findViewById(R.id.frameLayout), "Deleted " + description, Snackbar.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            Snackbar.make(a.findViewById(R.id.frameLayout), "Could not delete " + description + "!", Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, description + " could not be deleted!" + e);
                        });
            }
            // finally, we add this to our recycler view.
        }).attachToRecyclerView(ingredientListView);

        // on snapshot listener for the collection
        ingredientsCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }

            // Clear the old list
            ingredientDataList.clear();

            if (queryDocumentSnapshots == null) { return; }

            int errorCount = 0;
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                try {
                    String description = doc.get("description", String.class);
                    float amount = doc.get("amount", float.class);
                    Date bestBefore = doc.get("bestBeforeDate", Date.class);
                    String category = doc.get("category", String.class);
                    String location = doc.get("location", String.class);
                    String unit = doc.get("unit", String.class);

                    StoreIngredient store = new StoreIngredient(id, description, amount, unit, category, bestBefore, location);

                    ingredientDataList.add(store); // Adding the cities and provinces from FireStore

                    //sorting the list again
                    ingredientDataList.setSortChoice(0);
                    ingredientDataList.sortByChoice();

                } catch (Exception e) {
                    Log.i(TAG + ":snapshotListener", "Error retrieving document " + id + ":" + e);
                    errorCount += 1;
                }
            }

            if (errorCount > 0) {
                Snackbar.make(ingredientListView, "Error reading " + errorCount + " documents!", Snackbar.LENGTH_LONG).show();
            }
            Log.i(TAG, "Snapshot listener: Added " + ingredientDataList.size() + " elements");
            storeingredientViewAdapter.notifyDataSetChanged();
        });
    }

    final private ActivityResultLauncher<Intent> editStoreIngredientLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (isNull(result.getData())) {
                    return;
                }
                Bundle bundle = result.getData().getExtras();

                Log.i(TAG + ":editStoreIngredientLauncher", "Got bundle");

                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Ok - we have an updated ingredient!
                    // edit firebase directly
                    StoreIngredient ingredient = (StoreIngredient) bundle.getSerializable("ingredient");
                    ingredientsCollection.document(ingredient.getId()).set(ingredient);
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // cancelled request - do nothing.
                    Log.i(TAG + ":editStoreIngredientLauncher", "Received cancelled");
                }
            });


    public void bootUp()
    {
        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        ingredientsCollection = db.collection("ingredientStorage");


        ingredientDataList = new com.androidimpact.app.IngredientStorage();
        storeingredientViewAdapter = new StoreIngredientViewAdapter(getContext(), ingredientDataList.getIngredientStorageList());
    }

    /**
     * AddIngredientLauncher uses the ActivityResultAPIs to handle data returned from
     * AddStoreIngredientActivity
     */
    final private ActivityResultLauncher<Intent> addStoreIngredientLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (isNull(result.getData())) {
                    return;
                }
                Bundle bundle = result.getData().getExtras();

                Log.i(TAG + ":addIngredientResult", "Got bundle");

                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Ok - we have an ingredient!
                    StoreIngredient ingredient = (StoreIngredient) bundle.getSerializable("ingredient");

                    Activity a = getActivity();
                    final KonfettiView confetti = a.findViewById(R.id.confetti_view);
                    confetti.build()
                            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(500L)
                            .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                            .addSizes(new Size(8, 4f))
                            .setPosition(-50f, confetti.getWidth() + 50f, -50f, -50f)
                            .streamFor(300, 2000L);

                    Log.i(TAG + ":addIngredientResult", ingredient.getDescription());
                    ingredientsCollection.document(ingredient.getId()).set(ingredient);
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // cancelled request - do nothing.
                    Log.i(TAG + ":addIngredientResult", "Received cancelled");
                }
            });

}