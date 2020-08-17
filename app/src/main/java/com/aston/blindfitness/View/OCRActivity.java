package com.aston.blindfitness.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * OCRActivity to provide recognition and scan options for user
 */
public class OCRActivity extends AppCompatActivity {

    SharedPrefs userPreferences;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermission[];
    String galleryPermission[];

    Uri image_uri;

    String ocrString;
    Button ocr_TextRecognition, ocr_ObjectRecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        userPreferences = new SharedPrefs(OCRActivity.this);
        if (userPreferences.loadDarkTheme() == true) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        // Initialise views
        ocr_TextRecognition = findViewById(R.id.ocr_text_recognition);
        ocr_ObjectRecognition = findViewById(R.id.ocr_object_recognition);
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        galleryPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ocr_TextRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTextRecognitionDialogue();
            }
        });

        ocr_ObjectRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OCRActivity.this, DetectorActivity.class);
                startActivity(intent);

            }
        });

        if (userPreferences.loadDarkTheme()) {
            getSupportActionBar().setTitle(Html.fromHtml("OCR"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("OCR"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    /**
     * Provide text-recognition options for the user to select from
     */
    private void showTextRecognitionDialogue() {
        // Items to display when clicked
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogTheme);

        // Set title of dialog
        dialog.setTitle("Select From");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    // Camera has been picked
                    if (!checkCameraPermission()) {
                        // Camera permission has not been granted, request it from user
                        requestCameraPermission();
                    } else {
                        // Permission has been granted, choose from Camera
                        pickFromCamera();
                    }
                }
                if (i == 1) {
                    // Gallery has been picked
                    if (!checkGalleryPermission()) {
                        // Camera permission has not been granted, request it from user
                        requestGalleryPermission();
                    } else {
                        // Permission has been granted, choose from Camera
                        pickFromGallery();
                    }

                }
            }
        });
        dialog.create().show();
    }

    /**
     * Start new intent to pick from users gallery
     */
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    /**
     * Start new intent to pick from users camera
     */
    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "OCR_Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    /**
     * Request gallery permission from user
     */
    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(this, galleryPermission, STORAGE_REQUEST_CODE);
    }

    /**
     * @return whether the user has granted permission to use their gallery
     */
    private boolean checkGalleryPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    /**
     * Request camera permission from user
     */
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    /**
     * @return whether the user has granted permission to use their camera
     */
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result2;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "BlindFitness needs Camera permission to perform this action", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "BlindFitness needs Camera permission to perform this action", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // If we have gotten the image from camera
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                // Recieved image from gallery, crop image
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }

            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                // Recieved image from camera, crop image
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUi = result.getUri();

                ImageView image = new ImageView(this);
                image.setImageURI(resultUi);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!textRecognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }

                    ocrString = sb.toString();
                    Intent intent = new Intent(OCRActivity.this, TTSActivity.class);
                    intent.putExtra("description", ocrString);
                    startActivity(intent);
                    //Toast.makeText(this, ocrString, Toast.LENGTH_SHORT).show();

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, " " + error, Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
