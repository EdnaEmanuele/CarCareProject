package com.example.carcareproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView PostDescription;
    private Button DelitePostBNT, EditPostBNT;
    private String PostKey, currenrUid,DataBaseUid, description, image;
    private DatabaseReference ClickPostRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        mAuth = FirebaseAuth.getInstance();
        currenrUid = mAuth.getCurrentUser().getUid();

        PostImage = (ImageView) findViewById(R.id.post_image_click);
        PostDescription = (TextView) findViewById(R.id.post_descriptin_click);
        DelitePostBNT = (Button) findViewById(R.id.delit_post_BTN);
        EditPostBNT = (Button) findViewById(R.id.edit_post_BTN);

       DelitePostBNT.setVisibility(View.INVISIBLE);
       EditPostBNT.setVisibility(View.INVISIBLE);

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                description = snapshot.child("description").getValue().toString();
                image = snapshot.child("post_img").getValue().toString();
                DataBaseUid = snapshot.child("uid").getValue().toString();

                PostDescription.setText(description);
                Glide.with(getApplicationContext()).load(image).into(PostImage);

                if(currenrUid.equals(DataBaseUid)){
                    DelitePostBNT.setVisibility(View.VISIBLE);
                    EditPostBNT.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}