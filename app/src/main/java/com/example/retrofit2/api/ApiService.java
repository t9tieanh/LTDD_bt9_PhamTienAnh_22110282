package com.example.retrofit2.api;

import com.example.retrofit2.model.ImageUpload;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("test/update-profile")
    Call<ImageUpload> upload(
            @Part MultipartBody.Part avatar
    );
}
