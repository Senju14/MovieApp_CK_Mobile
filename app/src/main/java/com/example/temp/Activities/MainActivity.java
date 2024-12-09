package com.example.temp.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.temp.Adapters.SlidersAdapter;
import com.example.temp.Domains.SliderItems;
import com.example.temp.R;
import com.example.temp.databinding.ActivityMainBinding;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private FirebaseDatabase database;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable=new Runnable() {
        @Override
        public void run() {
            binding.viewPager2.setCurrentItem( binding.viewPager2.getCurrentItem()+1 );
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivityMainBinding.inflate( getLayoutInflater() );
        EdgeToEdge.enable( this );
        setContentView( binding.getRoot() );

        database=FirebaseDatabase.getInstance();

        Window w=getWindow();
        w.setFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        initBanner();

        ViewCompat.setOnApplyWindowInsetsListener( findViewById( R.id.main ), (v, insets) -> {
            Insets systemBars = insets.getInsets( WindowInsetsCompat.Type.systemBars() );
            v.setPadding( systemBars.left, systemBars.top, systemBars.right, systemBars.bottom );
            return insets;
        } );
    }

    private void initBanner() {
        DatabaseReference myRef = database.getReference( "Banners" );
        binding.progressBarBanner.setVisibility( View.VISIBLE );
        ArrayList<SliderItems> items=new ArrayList<>();
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
}