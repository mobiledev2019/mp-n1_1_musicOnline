package com.trantan.music.ui.change_password;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trantan.music.R;

public class ChangePasswordActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mTextNewPass;
    private Button mButtonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initUi();
    }

    private void initUi() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        mTextNewPass = findViewById(R.id.text_new_pass);
        mButtonSubmit = findViewById(R.id.button_submit);

        mButtonSubmit.setOnClickListener(v -> changePass());
    }

    private void changePass() {
        if (isValidData(mTextNewPass.getText().toString())) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            user.updatePassword(mTextNewPass.getText().toString()).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Password changed!!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private boolean isValidData(String pass) {
        if (pass.isEmpty()) {
            mTextNewPass.setError("Enter password");
            return false;
        }
        if (pass.length() < 6) {
            mTextNewPass.setError("Password should be at least 6 characters");
            return false;
        }
        return true;
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        return intent;
    }
}
