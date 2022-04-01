package com.malikendsley.firebaseutils.schema;

import androidx.annotation.Nullable;

import com.google.firebase.database.PropertyName;

public class User {
    @PropertyName("Username")
    public final String Username;
    @PropertyName("Email")
    public final String Email;

    @SuppressWarnings("unused")
    public User() {
        Username = "unset";
        Email = "unset";
        //necessary for firebase
    }

    //for the case where the UID isn't known at the time of creation
    public User(String username, String email) {
        this.Username = username;
        this.Email = email;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        return Username.equals(other.Username) && Email.equals(other.Email);
    }
}
