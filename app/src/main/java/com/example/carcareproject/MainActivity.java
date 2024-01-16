package com.example.carcareproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar; (com esse da erro)
import androidx.appcompat.widget.Toolbar; //import correto

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    //inicializing
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView carcare;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth; // We will check if the user is signed in
    private DatabaseReference UsersRef;
    private CircleImageView  NavProfileImage;
    private TextView NavProfileCompanyName;
    String courrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        courrentUserID = mAuth.getCurrentUser().getUid();
        // A parte .child("Users") está referindo-se ao nó "Users". Se o nó "Users" não existir no seu banco de dados Firebase, ele será criado quando você tentar acessá-lo ou criar uma referência para ele.
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("CarCareHub");
        // Sets the ActionBar text color to white
        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawble_layout);
        //This code creates an instance of ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout,mToolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_hamburger);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_img);
        NavProfileCompanyName = (TextView) navView.findViewById(R.id.nav_company_name);

//getting elements from DB
        UsersRef.child(courrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String company_name = snapshot.child("companyName").getValue(String.class);
                    String image = snapshot.child("profileimage").getValue(String.class);

                    NavProfileCompanyName.setText(company_name);

                    // Use Glide to load the image into the CircleImageView
                    if (image != null && !image.isEmpty()) {
                        Glide.with(MainActivity.this)
                                .load(image)
                                .into(NavProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });



        navigationView.setNavigationItemSelectedListener((item -> {
                UserMenuSelector(item);
                return false;
        }));
    }

    @Override
    protected void onStart() { //Using mAuth it will check de user
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            SendUserToLoginActivity();//a method
        }
        else {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(current_user_id)){
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class); // Create an Intent to start the SetupActivity
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Configure flags to clear the activity stack
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class); // Create an Intent to start the LoginActivity
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Configure flags to clear the activity stack
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_profile) {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_home) {
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_mechanics) {
            Toast.makeText(this, "Mechanics", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_find_mechanic) {
            Toast.makeText(this, "Find mechanics", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_messsages) {
            Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_logout) {
           mAuth.signOut();
           SendUserToLoginActivity();
        } else {
            // Caso nenhum dos IDs correspondentes seja encontrado
        }
    }


}