package com.example.retrofit2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView avatarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        avatarImageView = findViewById(R.id.profile_image);


        // lấy avartar lưu săẵn
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String imgUrl = preferenceManager.getUserImageUrl();

        if (imgUrl != null) {
            Glide.with(this).load(imgUrl).into(avatarImageView);
        }
    }

    public void profileImageOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, UploadActivity.class);
        startActivity(intent);
    }
}