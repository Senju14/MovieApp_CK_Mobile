package com.example.temp.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.temp.Adapters.CastListAdapter;
import com.example.temp.Adapters.CategoryEachFilmAdapter;
import com.example.temp.Domains.Film;
import com.example.temp.R;
import com.example.temp.databinding.ActivityDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

import eightbitlab.com.blurview.RenderScriptBlur;

public class DetailActivity extends AppCompatActivity {

    private ImageView bookmarkImageView;
    private Film currentFilm;
    private boolean isBookmarked = false;
    private DatabaseReference databaseReference;

    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load ngôn ngữ đã lưu nhưng không áp dụng lại nếu không cần thiết
        applySavedLocale();

        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVarianle();

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Thiết lập chức năng cho nút chuyển đổi ngôn ngữ
        binding.langSwitchIcon.setOnClickListener(v -> showLanguageDialog());

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("bookmarks");

        // Get Film object from Intent
        currentFilm = (Film) getIntent().getSerializableExtra("object");

        // Initialize bookmark ImageView
        bookmarkImageView = findViewById(R.id.imageView8);

        // Set the initial state of bookmark icon based on the current bookmark status
        loadBookmarkStatus();

        // Handle click on bookmark
        bookmarkImageView.setOnClickListener(v -> {
            isBookmarked = !isBookmarked;  // Toggle bookmark state
            updateBookmarkStatus();
            updateFirebase();
        });
    }

    private void loadBookmarkStatus() {
        // Retrieve bookmark status from Firebase (you can check if the film is already bookmarked or not)
        // For now, we assume the state is false (not bookmarked)
        if (isBookmarked) {
            bookmarkImageView.setImageResource(R.drawable.bookmark_filled);  // icon for bookmarked state
        } else {
            bookmarkImageView.setImageResource(R.drawable.bookmark);  // icon for un-bookmarked state
        }
    }

    private void updateBookmarkStatus() {
        if (isBookmarked) {
            bookmarkImageView.setImageResource(R.drawable.bookmark_filled);  // Filled bookmark
        } else {
            bookmarkImageView.setImageResource(R.drawable.bookmark);  // Empty bookmark
        }
    }

    private void updateFirebase() {
        if (isBookmarked) {
            // Add to Firebase Realtime Database as a bookmarked film
            String filmId = databaseReference.push().getKey();
            if (filmId != null) {
                databaseReference.child(filmId).setValue(currentFilm).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(DetailActivity.this, "Bookmarked!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DetailActivity.this, "Failed to bookmark", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            // Remove from Firebase Realtime Database
            databaseReference.orderByChild("title").equalTo(currentFilm.getTitle()).get()
                    .addOnSuccessListener(snapshot -> {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(DetailActivity.this, "Bookmark removed", Toast.LENGTH_SHORT).show();
                    });
        }
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



    private void setVarianle() {
        Film item = (Film) getIntent().getSerializableExtra("object");
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new GranularRoundedCorners(0, 0, 50, 50));

        Glide.with(this).load(item.getPoster()).apply(requestOptions).into(binding.filmPic);

        binding.titleTxt.setText(item.getTitle());
        binding.imdbTxt.setText("IMDB" + item.getImdb());
        binding.movieTimesTxt.setText(item.getYear() + " - " + item.getTime());
        binding.movieSummery.setText(item.getDescription());

        binding.watchTrailerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = item.getTrailer().replace("https://www.youtube.com/watch?v=", "");
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getTrailer()));

                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });

        binding.buyTicketBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeatListActivity.class);
            intent.putExtra("film", item);
            startActivity(intent);
        });



        binding.imageView7.setOnClickListener(v -> {
            if (item != null) {
                // Tạo nội dung để chia sẻ
                String shareContent = "Check out this movie:\n" +
                        "Title: " + item.getTitle() + "\n" +
                        "Year: " + item.getYear() + "\n" +
                        "IMDB Rating: " + item.getImdb() + "\n" +
                        "Description: " + item.getDescription() + "\n" +
                        "Trailer: " + item.getTrailer();

                // Intent chia sẻ
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);

                // Hiển thị giao diện chia sẻ
                startActivity(Intent.createChooser(shareIntent, "Share movie details via"));
            }
        });

        binding.backImg.setOnClickListener(v -> finish());

        float radius = 10f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowsBackground = decorView.getBackground();

        binding.blurView2.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowsBackground)
                .setBlurRadius(radius);
        binding.blurView2.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blurView2.setClipToOutline(true);

        if (item.getGenre() != null) {
            binding.genreView.setAdapter(new CategoryEachFilmAdapter(item.getGenre()));
            binding.genreView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }

        if (item.getCasts() != null) {
            binding.CastView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.CastView.setAdapter(new CastListAdapter(item.getCasts()));
        }
    }
}