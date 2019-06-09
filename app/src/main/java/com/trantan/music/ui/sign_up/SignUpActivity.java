package com.trantan.music.ui.sign_up;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.PatternsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.trantan.music.R;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private TextInputLayout mTextInputLayoutEmail;
    private TextInputLayout mTextInputLayoutPassword;
    private TextInputLayout mTextInputLayoutDisplayName;
    private TextInputEditText mTextInputEditEmail;
    private TextInputEditText mTextInputEditPassword;
    private TextInputEditText mTextInputEditDisplayname;
    private TextView mButtonSignUp;
    private FirebaseAuth mFirebaseAuth;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mFirebaseAuth = FirebaseAuth.getInstance();

        initUi();
    }

    private void initUi() {
        mTextInputLayoutEmail = findViewById(R.id.text_layout_email);
        mTextInputLayoutPassword = findViewById(R.id.text_layout_password);
        mTextInputLayoutDisplayName = findViewById(R.id.text_layout_display_name);
        mTextInputEditEmail = findViewById(R.id.text_edit_email);
        mTextInputEditPassword = findViewById(R.id.text_edit_password);
        mTextInputEditDisplayname = findViewById(R.id.text_edit_display_name);
        progressBar = findViewById(R.id.progress_load);
        mButtonSignUp = findViewById(R.id.text_sign_up);

        mButtonSignUp.setOnClickListener(v -> {
            boolean isValid = isValidateData(mTextInputEditEmail.getText().toString(),
                    mTextInputEditPassword.getText().toString());
            if (isValid) {
                progressBar.setVisibility(View.VISIBLE);
                mButtonSignUp.setClickable(false);
                mFirebaseAuth
                        .createUserWithEmailAndPassword(mTextInputEditEmail.getText().toString(),
                                mTextInputEditPassword.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this,
                                        "Sign up success!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this,
                                        "Authentication failed : "
                                                + task.getException().getMessage()
                                        , Toast.LENGTH_SHORT).show();
                                mButtonSignUp.setClickable(true);
                                progressBar.setVisibility(View.GONE);
                            }
                        });

            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private boolean isValidateData(String email, String pass) {
        if (email.isEmpty()) {
            mTextInputLayoutEmail.setError("Enter email address");
            return false;
        }
        if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            mTextInputLayoutEmail.setError("Email invalid");
            return false;
        }
        if (pass.isEmpty()) {
            mTextInputLayoutPassword.setError("Enter password");
            return false;
        }
        if (pass.length() < 6) {
            mTextInputLayoutPassword.setError("Password should be at least 6 characters");
            return false;
        }
        return true;
    }


    public static Intent getIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }
}
