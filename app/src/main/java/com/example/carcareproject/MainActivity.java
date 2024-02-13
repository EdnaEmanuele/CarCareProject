package com.example.carcareproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

    // Initializing
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private ImageButton AddNewPostBTN;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostsRef;
    private CircleImageView NavProfileImage;
    private TextView NavProfileCompanyName;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            CheckUserExistence();
        }

        currentUserID = mAuth.getCurrentUser().getUid();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("CarCareHub");
        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        AddNewPostBTN = findViewById(R.id.add_new_post_btn);

        drawerLayout = findViewById(R.id.drawble_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_hamburger);
        navigationView = findViewById(R.id.navigation_view);

        postList = findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = navView.findViewById(R.id.nav_profile_img);
        NavProfileCompanyName = navView.findViewById(R.id.nav_company_name);

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String company_name = snapshot.child("companyName").getValue(String.class);
                    String image = snapshot.child("profileimage").getValue(String.class);

                    NavProfileCompanyName.setText(company_name);
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

        navigationView.setNavigationItemSelectedListener(item -> {
            UserMenuSelector(item);
            return false;
        });

        AddNewPostBTN.setOnClickListener(view -> SendUserToPostActivity());

        DisplayAllUsersPosts();
    }

    private void DisplayAllUsersPosts() {
        // Configurando as opções de recycler
        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(PostsRef.orderByChild("timestamp"), Posts.class)
                        .build();

        // Configurando o adaptador do FirebaseRecyclerAdapter
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {

                        final String PostKey = getRef(position).getKey();

                        // Configurar os dados do modelo nos componentes do ViewHolder
                        holder.setCompany_na(model.getCompany_na());
                        holder.setTime(model.getTime());
                        holder.setDate(model.getDate());
                        holder.setDescription(model.getDescription());
                        holder.setProfile_img(model.getProfile_img());
                        holder.setPost_img(model.getPost_img());

                        holder.mView.setOnClickListener(new View.OnClickListener(){
                           public void onClick (View v){
                               Intent clickPostIntente = new Intent (MainActivity.this, ClickPostActivity.class);
                               clickPostIntente.putExtra("PostKey", PostKey);
                               startActivity(clickPostIntente);
                           }
                        });
                    }

                    @NonNull
                    @Override
                    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Criar e retornar uma instância ViewHolder
                        View view = getLayoutInflater().inflate(R.layout.all_posts_latout, parent, false);
                        return new PostsViewHolder(view);
                    }
                };

        // Definindo o adaptador e inicializando a RecyclerView
        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCompany_na(String company_na) {
            TextView companyname = (TextView) mView.findViewById(R.id.post_company_name);
            companyname.setText(company_na);
        }
        public void setProfile_img(String profile_img){
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);

            if (profile_img != null && !profile_img.isEmpty()) {
                Glide.with(image.getContext())
                        .load(profile_img) // URL da imagem de perfil
                        .into(image);
            }
        }
        public void setTime(String time){
            TextView postTime = (TextView) mView.findViewById(R.id.post_time);
            postTime.setText(time);
        }
        public void setDate(String date){
            TextView postDate = (TextView) mView.findViewById(R.id.post_date);
            postDate.setText(date);
        }
        public void setDescription(String description){
            TextView postDescription = (TextView) mView.findViewById(R.id.post_description);
            postDescription.setText(description);
        }
        public void setPost_img(String post_img){
            ImageView images = (ImageView) mView.findViewById(R.id.post_IMAGENS);

            if (post_img != null && !post_img.isEmpty()) {
                Glide.with(images.getContext())
                        .load(post_img) // URL da imagem do post
                        .into(images);
            }
        }

    }

    private void SendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(current_user_id)) {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_post) {
            SendUserToPostActivity();
        } else if (itemId == R.id.nav_profile) {
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
