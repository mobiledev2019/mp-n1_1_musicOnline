package com.trantan.music.ui.editprofile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.trantan.music.R;

public class EditProfileActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageView mImageAvatar;
    private EditText mTextDisplayName;
    private Button mButtonSubmit;
    private Uri avatarUri;
    private StorageReference mStorageReference;
    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initUi();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    private void initUi() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progress_load);
        mTextDisplayName = findViewById(R.id.text_edit_display_name);
        mImageAvatar = findViewById(R.id.image_avatar);
        mButtonSubmit = findViewById(R.id.button_submit);
        mButtonSubmit.setOnClickListener(v -> updateProfile());
        mImageAvatar.setOnClickListener(v -> selectFuntion());
    }

    private void updateProfile() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        StorageReference reference = mStorageReference.child(mFirebaseAuth.getCurrentUser().getUid());
        progressBar.setVisibility(View.VISIBLE);
        UserProfileChangeRequest.Builder changeRequest = new UserProfileChangeRequest.Builder();
        if (mTextDisplayName.getText().toString().length() > 0) {
            changeRequest.setDisplayName(mTextDisplayName.getText().toString());
            if (avatarUri == null) {
                user.updateProfile(changeRequest.build())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditProfileActivity.this,
                                    "Update success!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
            }
        }
        if (avatarUri != null) {
            reference
                    .putFile(avatarUri)
                    .addOnSuccessListener(taskSnapshot ->
                    {
                        reference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    changeRequest.setPhotoUri(uri);
                                    user.updateProfile(changeRequest.build())
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(EditProfileActivity.this,
                                                        "Update success!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                });
                    });
        }
    }

    private void selectFuntion() {
        final String[] item = {"Mở thư viện", "Huỷ"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Thêm ảnh");
        builder.setItems(item, (dialogInterface, i) -> {
            if (item[i].equals("Mở thư viện")) {
                galleryIntent();
            } else {
                dialogInterface.dismiss();
            }
        }).show();

    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*"); // mở tất cả các folder lưa trữ ảnh
        intent.setAction(Intent.ACTION_GET_CONTENT); // đi đến folder mình chọn
        startActivityForResult(Intent.createChooser(intent, "Chọn Ảnh"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            avatarUri = data.getData();
            Glide.with(this)
                    .load(data.getData())
                    .apply(new RequestOptions().transform(new CircleCrop()))
                    .into(mImageAvatar);
        }
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, EditProfileActivity.class);
        return intent;
    }
}
