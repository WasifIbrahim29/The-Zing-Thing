package wasif.whatevervalue.com.instagramclone.Likes;

import android.app.Application;

import com.google.firebase.auth.FirebaseUser;

import wasif.whatevervalue.com.instagramclone.Models.User;


public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
