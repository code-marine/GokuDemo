package com.contri;

import android.app.Application;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;

public class Contri extends Application {
    public static FirebaseUser mUser;
    public static AuthCredential mCredential;

}
