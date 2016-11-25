package com.example.mypc.fastfoodfinder.model.User;

import android.util.Log;

import com.example.mypc.fastfoodfinder.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by nhoxb on 11/24/2016.
 */
public class User {

    public User() {
    }

    public User(String name, String email, String photoUrl, String uid)
    {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    private String name;
    private String email;
    private String uid;
    private String photoUrl;

    //TODO: Save user data to Firebase. Can extense to save other attribute like address, avatar image link
    public void saveUserData(final DatabaseReference mFirebaseDatabaseReference)
    {
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(uid)) {
                    mFirebaseDatabaseReference.child(uid).setValue(new User(name,email,photoUrl,uid));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MAPP", "Error checking user exists");
            }
        });
    }
}
