package com.example.temp.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.temp.R;
import com.example.temp.databinding.ActivityIntroBinding;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Locale;

public class IntroActivity extends AppCompatActivity {

    ActivityIntroBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Load ngôn ngữ đã lưu nhưng không áp dụng lại nếu không cần thiết
        applySavedLocale();

        super.onCreate( savedInstanceState );
        binding=ActivityIntroBinding.inflate( getLayoutInflater() );
        EdgeToEdge.enable( this );
        setContentView( binding.getRoot() );


        Window w=getWindow();
        w.setFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ViewCompat.setOnApplyWindowInsetsListener( findViewById( R.id.main ), (v, insets) -> {
            Insets systemBars = insets.getInsets( WindowInsetsCompat.Type.systemBars() );
            v.setPadding( systemBars.left, systemBars.top, systemBars.right, systemBars.bottom );
            return insets;
        } );

        // Thiết lập chức năng cho nút chuyển đổi ngôn ngữ
        binding.langSwitchIcon.setOnClickListener(v -> showLanguageDialog());

        // Thiết lập chức năng cho nút bắt đầu
        binding.startBtn.setOnClickListener(v ->
                startActivity(new Intent(IntroActivity.this, MainActivity.class))
        );

        // Cập nhật ngôn ngữ cho các view
        updateViewsLanguage();
    }

    private void updateViewsLanguage() {
        // Cập nhật các text view và button với các tài nguyên đúng ngôn ngữ
        binding.startBtn.setText(R.string.get_started);
        // Thêm các view khác cần cập nhật ngôn ngữ
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

}