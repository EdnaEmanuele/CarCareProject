package com.example.carcareproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText CompanyName, UserName, Address;
    private Button BTNsave;
    private CircleImageView ProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private ProgressDialog loadingBar;
    private StorageReference UserProfileImageRef;
    String currentUserID;
    final static int Gallery_Pick = 1;
    private Activity activity;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Image");

        CompanyName = (EditText) findViewById(R.id.setup_company_name);
        UserName = (EditText) findViewById(R.id.setup_user_name);
        Address = (EditText) findViewById(R.id.setup_address);
        BTNsave = (Button)  findViewById(R.id.setup_btn_save);
        ProfileImage = (CircleImageView) findViewById(R.id.setup_profile_img);

        loadingBar = new ProgressDialog(this);

        BTNsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAccountSetupInfo();
            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //To choose the picture from the gallery
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(galleryIntent, "Choose your profile image"), 1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == Gallery_Pick) {
            Uri selectedImageUri = data.getData();
            ProfileImage.setImageURI(selectedImageUri);

            if (selectedImageUri != null) {
                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                filePath.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
                            // Imagem enviada com sucesso
                            Toast.makeText(SetupActivity.this, "Profile image stored successfully", Toast.LENGTH_SHORT).show();
                            filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Ocorreu um erro ao enviar a imagem
                            Toast.makeText(SetupActivity.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }



    private void SaveAccountSetupInfo() {
        String companyName = CompanyName.getText().toString();
        String adress = Address.getText().toString();
        String userName = UserName.getText().toString();

        if (TextUtils.isEmpty(companyName)){
            Toast.makeText(this, "Please, write your company name", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(adress)){
            Toast.makeText(this, "Please, write your company address", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Please, write your username", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Saving information");
            loadingBar.setMessage("Please wait, while we are creating your new account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("companyName", companyName);
            userMap.put("adress", adress);
            userMap.put("oppenigHours", "none");
            userMap.put("phone", "none");
            userMap.put("userName", userName);

            UserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SetupActivity.this, "Your account is created successfully", Toast.LENGTH_LONG).show();
                        SendUserToMainActivity();
                        loadingBar.dismiss();
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "ERROR " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}