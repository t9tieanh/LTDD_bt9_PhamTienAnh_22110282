package com.example.retrofit2;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.retrofit2.api.ApiService;
import com.example.retrofit2.api.RetrofitClient;
import com.example.retrofit2.model.ImageUpload;
import com.example.retrofit2.utils.RealPathUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView uploadImagePreview;
    private Uri imageUri;
    private ApiService serviceAPI; // Service API để upload ảnh
    private ProgressDialog progressDialog;

    Button btnChooseFile, btnUploadImages;

    String fileUrl = "http://10.0.2.2:8080/files/image/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Khởi tạo các view
        uploadImagePreview = findViewById(R.id.upload_image_preview);
        btnChooseFile = findViewById(R.id.btn_choose_file);
        btnUploadImages = findViewById(R.id.btn_upload_images);

        // Khởi tạo progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải lên...");
        progressDialog.setCancelable(false);

        // Xử lý khi ấn nút "Chọn file"
        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Xử lý khi ấn nút "Upload images"
        btnUploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadImage(imageUri, "image");
                } else {
                    Toast.makeText(UploadActivity.this, "Vui lòng chọn một ảnh trước!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // lấy avartar lưu săẵn
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String imgUrl = preferenceManager.getUserImageUrl();

        if (imgUrl != null) {
            Glide.with(this).load(imgUrl).into(uploadImagePreview);
        }
    }

    // Mở trình chọn ảnh
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Nhận kết quả từ trình chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                uploadImagePreview.setImageBitmap(bitmap); // Hiển thị ảnh đã chọn lên ImageView
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Upload ảnh lên server
    public void uploadImage(Uri imageUri, String imageKey) {
        progressDialog.show(); // Hiển thị progress dialog

        String imagePath = RealPathUtil.getRealPath(this, imageUri); // Lấy đường dẫn thực của ảnh
        File imageFile = new File(imagePath);


        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", imageFile.getName(), requestFile);

        serviceAPI = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Gọi API upload ảnh
        Call<ImageUpload> call = serviceAPI.upload(body);
        call.enqueue(new Callback<ImageUpload>() {
            @Override
            public void onResponse(Call<ImageUpload> call, Response<ImageUpload> response) {
                progressDialog.dismiss(); // Tắt progress dialog
                if (response.isSuccessful() && response.body() != null) {
                    ImageUpload imageUpload = response.body();
                    String imgUrl = fileUrl + imageUpload.getAvatar();

                    PreferenceManager preferenceManager = new PreferenceManager(UploadActivity.this);

                    // Lưu URL ảnh
                    preferenceManager.saveUserImageUrl(imgUrl);

                    Glide.with(UploadActivity.this).load(imgUrl).into(uploadImagePreview);

                    // chuyển về main
                    Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(UploadActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageUpload> call, Throwable t) {
                progressDialog.dismiss(); // Tắt progress dialog
                Toast.makeText(UploadActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                t.printStackTrace(); // In lỗi để debug
            }
        });
    }
}
