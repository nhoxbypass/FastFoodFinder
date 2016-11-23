package com.example.mypc.fastfoodfinder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.model.Store;
import com.example.mypc.fastfoodfinder.model.StoreDataSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class SplashActivity extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    SharedPreferences mSharedPreferences;

    public static final String KEY_FIRST_RUN = "firstRun";
    public boolean isFirstRun = false;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Realm.init(SplashActivity.this);
        mSharedPreferences = getSharedPreferences("com.example.mypc.fastfoodfinder", MODE_PRIVATE);
        isFirstRun = mSharedPreferences.getBoolean(KEY_FIRST_RUN, true);

        //Else
        if (isFirstRun) {
            readDataFromFirebase(new OnGetDataListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(DataSnapshot data) {
                    startMyActivity(LoginActivity.class);
                    mSharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply();
                    if (mFirebaseAuth != null) {
                        mFirebaseAuth.signOut();
                        Toast.makeText(SplashActivity.this, "Update database successfully", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    Toast.makeText(SplashActivity.this, "Update database failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        else
        {
            startMyActivity(MainActivity.class);
        }
    }

    private void startMyActivity(Class<?> activity)
    {
        Intent intent = new Intent(SplashActivity.this, activity);
        startActivity(intent);
        finish();
    }

    public interface OnGetDataListener {
        public void onStart();
        public void onSuccess(DataSnapshot data);
        public void onFailed(DatabaseError databaseError);
    }

    public void readDataFromFirebase(final OnGetDataListener listener) {
        listener.onStart();
        // Do first run stuff here then set 'firstrun' as false
        // using the following line to edit/commit prefs
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in
            mFirebaseAuth.signInAnonymously()
                    .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Firebase instance variables
                                // Get a reference to our posts
                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference ref = database.getReference("markers_add");

                                // Attach a listener to read the data at our posts reference
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        List<Store> storeList = new ArrayList<Store>();
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            Store store = child.getValue(Store.class);
                                            storeList.add(store);
                                        }

                                        StoreDataSource storeDataSource = new StoreDataSource();
                                        storeDataSource.store(storeList);
                                        listener.onSuccess(dataSnapshot);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w("MAPP", "The read failed: " + databaseError.getCode());
                                        listener.onFailed(databaseError);
                                    }
                                });
                            } else {
                                Log.w("MAPP", "signInAnonymously", task.getException());
                                Toast.makeText(SplashActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            Log.d("MAPP", "signInAnonymously outside");
        }
    }
}
