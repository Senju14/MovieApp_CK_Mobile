package com.example.temp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.temp.Adapters.FilmListAdapter;
import com.example.temp.Adapters.MovieAdapter;
import com.example.temp.Adapters.SlidersAdapter;
import com.example.temp.Domains.Cast;
import com.example.temp.Domains.Film;
import com.example.temp.Domains.MovieItem;
import com.example.temp.Domains.SliderItems;
import com.example.temp.R;
import com.example.temp.databinding.ActivityMainBinding;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.example.temp.Activities.BookingHistoryActivity;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    ImageView profileImageView;
    private EditText searchEditText;
    private RecyclerView searchResultRecyclerView;
    private List<MovieItem> movieList = new ArrayList<>();
    private List<MovieItem> searchResultList = new ArrayList<>();
    private MovieAdapter searchAdapter;


    RecyclerView.Adapter adapterNewMovies;
    ActivityMainBinding binding;
    private FirebaseDatabase database;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            binding.viewPager2.setCurrentItem( binding.viewPager2.getCurrentItem()+1 );
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load ngôn ngữ đã lưu nhưng không áp dụng lại nếu không cần thiết
        applySavedLocale();

        super.onCreate( savedInstanceState );
        binding = ActivityMainBinding.inflate( getLayoutInflater() );
        EdgeToEdge.enable( this );
        setContentView( binding.getRoot() );

        database=FirebaseDatabase.getInstance();

        Window w=getWindow();
        w.setFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        initBanner();
        initTopMoving();
        initUpComming();

        ViewCompat.setOnApplyWindowInsetsListener( findViewById( R.id.main ), (v, insets) -> {
            Insets systemBars = insets.getInsets( WindowInsetsCompat.Type.systemBars() );
            v.setPadding( systemBars.left, systemBars.top, systemBars.right, systemBars.bottom );
            return insets;
        } );

        binding.langSwitchIcon.setOnClickListener(v -> showLanguageDialog());


        searchEditText = findViewById(R.id.editTextText);
        searchResultRecyclerView = findViewById(R.id.searchResultRecyclerView); // Bạn cần thêm RecyclerView này vào layout

        searchAdapter = new MovieAdapter(this, searchResultList);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultRecyclerView.setAdapter(searchAdapter);

        setupSearchFunction();

        loadMoviesFromFirebase();

        displayUserInfo();

        bottomMenuNavigation();
    }

    private void bottomMenuNavigation() {
        // Add this after your other initialization code in onCreate
        ChipNavigationBar bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setItemSelected(R.id.explorer, true); // Set default selected item
        bottomNavigationView.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {


                if (id == R.id.profile) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        DatabaseReference userRef = database.getReference("users").child(uid);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String name = snapshot.child("name").getValue(String.class);
                                    String email = snapshot.child("email").getValue(String.class);
                                    String username = snapshot.child("username").getValue(String.class);
                                    String password = snapshot.child("password").getValue(String.class);

                                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("email", email);
                                    intent.putExtra("username", username);
                                    intent.putExtra("password", password);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("DatabaseError", error.getMessage());
                            }
                        });
                    }
                } else if (id == R.id.explorer) {
                    // Handle explorer selection

                } else if (id == R.id.favorites) {
                    Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                    startActivity(intent);
                } else if (id == R.id.cart) {
                    Intent intent = new Intent(MainActivity.this, BookingHistoryActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    private void displayUserInfo() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String uid = currentUser.getUid();
        DatabaseReference userRef = database.getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    String password = snapshot.child("password").getValue(String.class);
                    updateUserInfoUI(name, email);
                    profileImageView = findViewById(R.id.imgProfile);
                    profileImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("email", email);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", error.getMessage());
            }
        });
    }

    private void updateUserInfoUI(String name, String email) {

        TextView nameTextView = findViewById(R.id.txtName);
        TextView emailTextView = findViewById(R.id.txtGmail);

        nameTextView.setText(name);
        emailTextView.setText(email);
    }
    private void setupSearchFunction() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadMoviesFromFirebase() {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("Items");
        DatabaseReference upcommingRef = FirebaseDatabase.getInstance().getReference("Upcomming");

        // Tải phim từ "Items"
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MovieItem movie = dataSnapshot.getValue(MovieItem.class);
                    if (movie != null) {
                        movieList.add(movie);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        // Tải phim từ "Upcomming"
        upcommingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MovieItem movie = dataSnapshot.getValue(MovieItem.class);
                    if (movie != null) {
                        movieList.add(movie);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String searchText) {
        searchResultList.clear();

        if (searchText.isEmpty()) {
            // If no search text, hide the search results
            searchResultRecyclerView.setVisibility(View.GONE);
            return;
        }

        for (MovieItem movie : movieList) {
            boolean matched = false;

            // Search by movie title
            if (movie.getTitle() != null &&
                    movie.getTitle().toLowerCase().contains(searchText)) {
                matched = true;
            }

            // Search by cast
            if (!matched && movie.getCasts() != null) {
                for (Cast cast : movie.getCasts()) {
                    if (cast.getActor() != null &&
                            cast.getActor().toLowerCase().contains(searchText)) {
                        matched = true;
                        break;
                    }
                }
            }

            // Search by genre
            if (!matched && movie.getGenre() != null) {
                for (String genre : movie.getGenre()) {
                    if (genre.toLowerCase().contains(searchText)) {
                        matched = true;
                        break;
                    }
                }
            }

            // If a match is found, add to search results
            if (matched) {
                searchResultList.add(movie);
            }
        }

        // Update adapter and visibility
        searchAdapter.notifyDataSetChanged();
        searchResultRecyclerView.setVisibility(
                searchResultList.isEmpty() ? View.GONE : View.VISIBLE
        );
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Tiếng Việt", "日本語", "中文", "Русский"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_language))
                .setItems(languages, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            saveLocale("en");
                            break;
                        case 1:
                            saveLocale("vi");
                            break;
                        case 2:
                            saveLocale("ja");
                            break;
                        case 3:
                            saveLocale("zh");
                            break;
                        case 4:
                            saveLocale("ru");
                            break;
                    }
                })
                .show();
    }

    private void saveLocale(String lang) {
        // Lưu trạng thái ngôn ngữ
        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("My_Lang", lang);
        editor.apply();

        // Áp dụng ngôn ngữ và làm mới activity
        setLocale(lang);

        // Khởi động lại toàn bộ task
        Intent intent = new Intent(this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void applySavedLocale() {
        // Tải ngôn ngữ từ SharedPreferences và chỉ áp dụng nếu khác ngôn ngữ mặc định
        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String savedLanguage = prefs.getString("My_Lang", Locale.getDefault().getLanguage());
        setLocale(savedLanguage);
    }

    private void setLocale(String lang) {
        // Cập nhật cấu hình ngôn ngữ
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void initUpComming() {
        DatabaseReference myRef = database.getReference( "Upcomming" );
        binding.progressBarUpcoming.setVisibility( View.VISIBLE );
        ArrayList<Film> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add( issue.getValue(Film.class ) );
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                                LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewUpcoming.setAdapter(new FilmListAdapter(items));
                    }
                    binding.progressBarUpcoming.setVisibility( View.GONE );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void initTopMoving() {
        DatabaseReference myRef = database.getReference( "Items" );
        binding.progressBarTop.setVisibility( View.VISIBLE );
        ArrayList<Film> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add( issue.getValue(Film.class ) );
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewTopMovies.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                                LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewTopMovies.setAdapter(new FilmListAdapter(items));
                    }
                    binding.progressBarTop.setVisibility( View.GONE );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void initBanner() {
        DatabaseReference myRef = database.getReference( "Banners" );
        binding.progressBarBanner.setVisibility( View.VISIBLE );
        ArrayList<SliderItems> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add( issue.getValue( SliderItems.class ) );
                    }
                    banners( items );
                    binding.progressBarBanner.setVisibility( View.GONE );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void banners(ArrayList<SliderItems> items) {
        binding.viewPager2.setAdapter( new SlidersAdapter( items, binding.viewPager2 ) );
        binding.viewPager2.setClipToPadding( false );
        binding.viewPager2.setClipChildren( false );
        binding.viewPager2.setOffscreenPageLimit( 3 );
        binding.viewPager2.getChildAt( 0 ).setOverScrollMode( RecyclerView.OVER_SCROLL_NEVER );

        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer( new MarginPageTransformer( 40 ) );
        compositePageTransformer.addTransformer( new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r=1-Math.abs( position );
                page.setScaleY( 0.85f+r*0.15f );
            }
        });

        binding.viewPager2.setPageTransformer( compositePageTransformer );
        binding.viewPager2.setCurrentItem( 1 );
        binding.viewPager2.registerOnPageChangeCallback( new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected( position );
                sliderHandler.removeCallbacks( sliderRunnable );
            }
        } );
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks( sliderRunnable );
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed( sliderRunnable, 2000 );
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}