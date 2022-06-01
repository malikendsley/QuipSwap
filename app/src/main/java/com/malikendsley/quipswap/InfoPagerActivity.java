package com.malikendsley.quipswap;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class InfoPagerActivity extends AppCompatActivity {

    ViewPager pager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_view_pager);
        pager = findViewById(R.id.viewPagerOnboarding);
    }
}
