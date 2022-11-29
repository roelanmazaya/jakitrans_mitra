package com.jakitrans.mc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.jakitrans.mc.json.AddEditKategoriRequestJson;
import com.jakitrans.mc.utils.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakitrans.mc.R;
import com.jakitrans.mc.constants.BaseApp;
import com.jakitrans.mc.constants.Constants;
import com.jakitrans.mc.json.ResponseJson;
import com.jakitrans.mc.models.User;
import com.jakitrans.mc.utils.SettingPreference;
import com.jakitrans.mc.utils.api.ServiceGenerator;
import com.jakitrans.mc.utils.api.service.MerchantService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class AddkategoriActivity extends AppCompatActivity {

    ImageView backbtn, menuimage;
    EditText namamenu;
    SwitchCompat activepromo;
    Button submit, addimage;
    String active,idkategori;
    byte[] imageByteArray;
    Bitmap decoded;
    SettingPreference sp;
    private String Switchstring;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addkategori);
        backbtn = findViewById(R.id.close);
        menuimage = findViewById(R.id.menuimage);
        namamenu = findViewById(R.id.textadd);
        submit = findViewById(R.id.submit);
        addimage = findViewById(R.id.addimage);
        sp = new SettingPreference(this);

        Intent intent = getIntent();
        idkategori = intent.getStringExtra("idkategori");

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        SwitchCompat switchadd = findViewById(R.id.switchactive);
        switchadd.setChecked(true);
        Switchstring = "1";
        switchadd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Switchstring = "1";
                } else {
                    Switchstring = "0";
                }

                Log.e("switch",Switchstring);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (namamenu.getText().toString().isEmpty()){
                    Toast.makeText(AddkategoriActivity.this, "Category name cant be empty!", Toast.LENGTH_SHORT).show();
                } else if (imageByteArray ==null) {
                    Toast.makeText(AddkategoriActivity.this, "Image cant be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    addmenu();
                }
            }
        });

    }

    private void selectImage() {
        if (check_ReadStoragepermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }

    private boolean check_ReadStoragepermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.permission_Read_data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    public String getPath(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        imageByteArray = baos.toByteArray();
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                Uri selectedImage = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = this.getContentResolver().openInputStream(Objects.requireNonNull(selectedImage));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

                String path = getPath(selectedImage);
                Matrix matrix = new Matrix();
                ExifInterface exif;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        exif = new ExifInterface(path);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                matrix.postRotate(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                matrix.postRotate(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                matrix.postRotate(270);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                menuimage.setImageBitmap(rotatedBitmap);
                imageByteArray = baos.toByteArray();
                decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));

            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void addmenu() {
        submit.setEnabled(false);
        submit.setText("Please Wait");
        submit.setBackground(getResources().getDrawable(R.drawable.button_round_3));
        User loginUser = BaseApp.getInstance(this).getLoginUser();
        MerchantService merchantService = ServiceGenerator.createService(MerchantService.class, loginUser.getNoTelepon(), loginUser.getPassword());
        AddEditKategoriRequestJson param = new AddEditKategoriRequestJson();
        param.setNotelepon(loginUser.getNoTelepon());
        param.setIdmerchant(loginUser.getId_merchant());
        param.setNamakategori(namamenu.getText().toString());
        param.setStatus(Switchstring);
        param.setFoto(getStringImage(decoded));
        merchantService.addkategori(param).enqueue(new Callback<ResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<ResponseJson> call,@NonNull Response<ResponseJson> response) {
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        finish();
                    } else {
                        submit.setEnabled(true);
                        submit.setText("Save");
                        submit.setBackground(getResources().getDrawable(R.drawable.button_round_1));
                        Toast.makeText(AddkategoriActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    submit.setEnabled(true);
                    submit.setText("Save");
                    submit.setBackground(getResources().getDrawable(R.drawable.button_round_1));
                    Toast.makeText(AddkategoriActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseJson> call,@NonNull Throwable t) {
                t.printStackTrace();
                submit.setEnabled(true);
                submit.setText("Save");
                submit.setBackground(getResources().getDrawable(R.drawable.button_round_1));
                Toast.makeText(AddkategoriActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
