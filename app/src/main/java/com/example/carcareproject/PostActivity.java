package com.example.carcareproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class PostActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ImageButton SelectPostImageBTN;
    private Button PostBTN;
    private EditText PostDescription;
    private  static final int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        SelectPostImageBTN = (ImageButton) findViewById(R.id.select_post_imageBTN);
        PostBTN = (Button) findViewById(R.id.btn_post);
        PostDescription = (EditText) findViewById(R.id.post_description);

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
