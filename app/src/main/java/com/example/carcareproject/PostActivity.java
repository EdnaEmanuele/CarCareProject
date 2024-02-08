package com.example.carcareproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ProgressDialog loadingBar;
    private ImageButton SelectPostImageBTN;
    private Button PostBTN;
    private EditText PostDescription;
    private  static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private String Description;
    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;
    private DatabaseReference UsersRef, PostRef;
    private StorageReference PostImagesReferences;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        PostImagesReferences = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();


        SelectPostImageBTN = (ImageButton) findViewById(R.id.select_post_imageBTN);
        PostBTN = (Button) findViewById(R.id.btn_post);
        PostDescription = (EditText) findViewById(R.id.post_description);
        loadingBar = new ProgressDialog(this);

        mtoolbar =(Toolbar) findViewById(R.id.update_post_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.seta_atras);

        SelectPostImageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        PostBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePosstInfo();
            }
        });

        // Defina a cor desejada
        int corDoTexto = ContextCompat.getColor(this, R.color.White);

        // Defina a cor do texto "Update post"
        String title = "Update post";
        Spannable text = new SpannableString(title);
        text.setSpan(new ForegroundColorSpan(corDoTexto), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
    }


    private void OpenGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent, "Choose your profile image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == Gallery_Pick && data != null) { //If the task was completed successfully, get the image
            ImageUri = data.getData(); // Atualize a variável de instância ImageUri
            SelectPostImageBTN.setImageURI(ImageUri);
        }
    }

    private void ValidatePosstInfo() {
        Description = PostDescription.getText().toString();
        if (ImageUri == null){
            Toast.makeText(this, "Please, select a post image...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Please, say something about your image...", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Add new post");
            loadingBar.setMessage("Please wait, while we are updating your new post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFBStorege();

        }
    }

    private void StoringImageToFBStorege() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currantDate = new SimpleDateFormat("dd-mm-yyyy");
        saveCurrentDate = currantDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currantTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currantTime.format(calForDate.getTime());

        postRandomName = saveCurrentDate+saveCurrentTime;

        StorageReference filePath = PostImagesReferences.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                    Toast.makeText(PostActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                    SavingPostInfoToFB();
                    loadingBar.dismiss();

                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

        });
    }

    private void SavingPostInfoToFB() {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    String company_name = snapshot.child("companyName").getValue().toString();
                    String profile_image = snapshot.child("profileimage").getValue().toString();

                    HashMap postMap = new HashMap<>();
                        postMap.put("uid", current_user_id);
                        postMap.put("date", saveCurrentDate);
                        postMap.put("time", saveCurrentTime);
                        postMap.put("description", Description);
                        postMap.put("post_img", downloadUrl);
                        postMap.put("profile_img", profile_image);
                        postMap.put("company_na", company_name);
                    PostRef.child(current_user_id + postRandomName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(PostActivity.this, "New post is updated successifully", Toast.LENGTH_SHORT).show();
                                        SendUserToMainActivity();
                                    }
                                    else {
                                        Toast.makeText(PostActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            SendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
