package com.example.user.ludifit.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.ludifit.MainScreen.MainActivity;
import com.example.user.ludifit.Profile.AchievementsUtility;
import com.example.user.ludifit.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import Domain.User;

public class AuthenticationActivity extends AppCompatActivity implements LoginFragment.btnClicked, RegisterFragment.btnClicked {

    private LoginFragment lf;
    private RegisterFragment rf;
    private FirebaseAuth mAuth;

    private EditText email, username, password, passwordConfirmation;
    private ImageView imgRegister;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    InputStream stream = null;
    Boolean changedPhoto = false;
    Bitmap bit;
    private String photoUrl;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        lf = new LoginFragment();

        ft.add(R.id.frame_auth,lf).addToBackStack(null).commit();

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        setContentView(R.layout.activity_authentication);
    }

    @Override
    public void signUp() {

        email = findViewById(R.id.emailRegister);
        username = findViewById(R.id.username);
        password = findViewById(R.id.passwordRegister);
        passwordConfirmation = findViewById(R.id.confirmPwd);

        final String editTextEmail = email.getText().toString().trim();
        final String editTextUsername = username.getText().toString().trim();
        final String editTextPassword = password.getText().toString().trim();

        CheckBox checkBox = rf.getView().findViewById(R.id.checkBox);

        if(email.getText().toString().isEmpty()){
            Toast.makeText(this, "You need to add an email.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(editTextEmail).matches()){
            Toast.makeText(this, "Email needs to be valid.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(editTextUsername.isEmpty()) {
            Toast.makeText(this, "Username needs to be filled.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter the password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.getText().toString().length() < 6){
            Toast.makeText(this, "Password length needs to be at least 6.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.getText().toString().isEmpty() && passwordConfirmation.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please confirm your password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.getText().toString().equals(passwordConfirmation.getText().toString())) {
            Toast.makeText(this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!checkBox.isChecked()) {
            Toast.makeText(this, "You need to accept the licencing agreement.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String photoDefault = "http://icons.iconarchive.com/icons/custom-icon-design/flatastic-4/512/User-orange-icon.png";
        mAuth.createUserWithEmailAndPassword(editTextEmail, editTextPassword)
                .addOnCompleteListener(AuthenticationActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    User user = new User(id, editTextEmail,editTextUsername);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) { // USER IS LOGGED IN
                                Toast.makeText(AuthenticationActivity.this, "The registration was successful", Toast.LENGTH_LONG).show();
                                if(!changedPhoto) {   // if the user didn't used his/her photo
                                    updateUserPhoto(photoDefault); // use the default
                                } else {
                                    uploadImage(imageUri);
                                    getPhotoReference(imageUri);
                                }
                            } else {
                                if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_LONG).show();
                                }
                                Toast.makeText(AuthenticationActivity.this, "Something went wrong in the registration", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(AuthenticationActivity.this, ((FirebaseAuthException)task.getException()).getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void getPhotoReference(Uri uri) {
        storageReference = storageReference.child("profile-pics/" + mAuth.getCurrentUser().getUid());

        UploadTask uploadTask = storageReference.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Toast.makeText(AuthenticationActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                    if (downloadUri != null) {
                        photoUrl = downloadUri.toString();
                        updateUserPhoto(photoUrl);
                        finishSignUp();
                    }
                } else {
                    Toast.makeText(AuthenticationActivity.this, "Oops something wrong happened!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUserPhoto(String photo) {
        String user = mAuth.getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(user);
        Map map = new HashMap();
        map.put("image", photo);
        db.updateChildren(map);
    }

    /**
     * This method gets called when
     * the user presses the add photo button
     */
    @Override
    public void addPhoto() {
        changedPhoto = true;
        imgRegister = findViewById(R.id.imageView2);
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);

    }

    private void finishSignUp() {
        final String editTextEmail = email.getText().toString().trim();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("email", editTextEmail);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            changedPhoto = true;
            imageUri = data.getData();
            imgRegister.setImageURI(imageUri);
            imgRegister.setRotation(270);
            try {
                stream = AuthenticationActivity.this.getContentResolver().openInputStream(data.getData());
                bit = MediaStore.Images.Media.getBitmap(AuthenticationActivity.this.getContentResolver(), imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImage(Uri uri) {
       storageReference = storageReference.child("profile-pics/"+mAuth.getCurrentUser().getUid());
       UploadTask ut = storageReference.putFile(uri);
       ut.addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(AuthenticationActivity.this, "There was an error uploading the photo!", Toast.LENGTH_SHORT).show();
           }
       }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               Toast.makeText(AuthenticationActivity.this, "The photo was uploaded succesfully!", Toast.LENGTH_SHORT).show();
           }
       });
    }

    /**
     * This method handles the login instance and
     * if the user is logged in starts the app in the main activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    /**
     * This method is called when the login button is pressed
     */
    @Override
    public void login() {

        TextView email = findViewById(R.id.emailLogin);
        String emailString = email.getText().toString().trim();
        if(emailString.isEmpty()) {
            Toast.makeText(this, "Please enter your email!", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView password = findViewById(R.id.password);
        String passwordString = password.getText().toString().trim();
        if(passwordString.isEmpty()) {
            Toast.makeText(this, "Please enter your password!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method is called when the register text view is pressed
     */
    @Override
    public void linkToRegister() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        rf = new RegisterFragment();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.replace(R.id.frame_auth,rf).addToBackStack(null).commit();
    }

    /**
     * This method regulates the back press
     */
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.frame_auth);
        if(f instanceof LoginFragment) {
            finish();
        }
        if(f instanceof LoginFragment) {
         super.onBackPressed();
        }
        super.onBackPressed();
    }
}