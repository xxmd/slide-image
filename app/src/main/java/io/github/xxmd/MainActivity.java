package io.github.xxmd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import io.github.xxmd.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindEvent();
    }

    private void bindEvent() {
        binding.btnSet.setOnClickListener(v -> {
            binding.slideImage.setLeftBitmap(R.drawable.old);
            binding.slideImage.setRightBitmap(R.drawable.refresh);
        });
    }
}