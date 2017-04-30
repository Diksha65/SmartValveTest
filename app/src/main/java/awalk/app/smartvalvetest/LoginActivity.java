package awalk.app.smartvalvetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            startActivity(new Intent(LoginActivity.this, UserActivity.class));
            finish();
        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAllowNewEmailAccounts(false)
                            .build(), REQUEST_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                notifyUser("Logged In");
                //if(!mIsAdmin){
                    startActivity(new Intent(LoginActivity.this, UserActivity.class));
                    finish();
                //}
            }
            else {
                if (response == null) {
                    // User pressed back button
                    notifyUser("Sign In Cancelled");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    notifyUser("No Internet");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    notifyUser("Unknown Error");
                    return;
                }
                notifyUser("Unknown Response");
            }
        }

    }

    public void notifyUser(String message){
        Snackbar.make(findViewById(R.id.LoginActivity),message,Snackbar.LENGTH_SHORT).show();
    }
}
