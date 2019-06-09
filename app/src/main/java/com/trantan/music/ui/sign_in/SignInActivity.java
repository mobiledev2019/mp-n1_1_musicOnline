package com.trantan.music.ui.sign_in;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.trantan.music.R;
import com.trantan.music.ui.sign_up.SignUpActivity;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN_WITH_GOOGLE = 1;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private TextView mButtonLogin;
    private SignInButton mSignInButton;
    private TextView mTextSignUp;

    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initUi();
        initClickListener();
        configGoogleSignIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mFirebaseAuth.getCurrentUser() != null) {
            Log.d(TAG, "onStart: ");
            finish();
        }
    }

    private void configGoogleSignIn() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("706226132785-jjf2bg6lndce06chu1gp201hf6jhsrjk.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();
    }

    private void initClickListener() {
        mButtonLogin.setOnClickListener(this);
        mSignInButton.setOnClickListener(this);
        mTextSignUp.setOnClickListener(this);
    }

    private void initUi() {
        mEditEmail = findViewById(R.id.text_email);
        mEditPassword = findViewById(R.id.text_password);
        mButtonLogin = findViewById(R.id.buttom_login);
        mSignInButton = findViewById(R.id.sign_in_button);
        mTextSignUp = findViewById(R.id.text_sign_up);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        return intent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttom_login:
                SignIn();
                break;
            case R.id.sign_in_button:
                SignInWithGoogle();
                break;
            case R.id.text_sign_up:
                startActivity(SignUpActivity.getIntent(this));
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_WITH_GOOGLE) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (googleSignInResult.isSuccess()) {
                firebaseAuthWithGoogle(googleSignInResult.getSignInAccount());
            }
        }
    }

    private void SignInWithGoogle() {
        Intent signInGoogle = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInGoogle, RC_SIGN_IN_WITH_GOOGLE);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getEmail() + " "
                + account.getDisplayName() + " "
                + account.getPhotoUrl());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        SignInSuccess();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SignInSuccess() {
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void SignIn() {
        mFirebaseAuth.signInWithEmailAndPassword(
                mEditEmail.getText().toString(),
                mEditPassword.getText().toString()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignInActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
