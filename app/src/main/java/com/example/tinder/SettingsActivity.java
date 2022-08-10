package com.example.tinder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tinder.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField;
    private Button mBack, mConfirm;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference("Images");
    private StorageReference mStorageRef;

    private String userId, name, phone, profileImageUrl, userSex;

    private Uri resultUri;
    //private Uri image;
    ProgressDialog progressDialog;

    private String currentUId;
    ActivitySettingsBinding binding;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setContentView(R.layout.activity_settings);


        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        String userSex = getIntent().getExtras().getString("userSex");

        ArrayList<Uri> imgList = new ArrayList<>();

        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userId);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {


                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                // There are no request codes
                                Intent data = result.getData();
                                Uri selectedImageUri = data.getData();
                                mProfileImage.setImageURI(selectedImageUri);
//                                Drawable drawable =  mProfileImage.getDrawable();
//                                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                resultUri = (Uri) selectedImageUri;
//                                saveUserInformation();
                            }
                        }
                    });


            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                Intent intent = new Intent(
//                        MediaStore.ACTION_IMAGE_CAPTURE);
//                File f = new File(android.os.Environment
//                        .getExternalStorageDirectory(), "temp.jpg");
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                        Uri.fromFile(f));


                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                setResult(1);
//                someActivityResultLauncher.launch(intent);
//                imgList.add(intent);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), 1);
            }
        });


        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                uploadImage();
                uploadToFirebase(resultUri);
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });
    }

    private void getUserInfo() {
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                    if (map.get("phone") != null) {
                        phone = map.get("phone").toString();
                        mPhoneField.setText(phone);
                    }
//                    if (map.get("imageUrl") != null) {
//                        profileImageUrl = map.get("imageUrl").toString();
//                        Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
//                    }
                    if(map.get("imageUrl")!=null){
                        profileImageUrl = map.get("imageUrl").toString();
                        switch(profileImageUrl){
                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(mProfileImage);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private UploadTask uploadTask;

    private void saveUserInformation() {
        name = mNameField.getText().toString();
        phone = mPhoneField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        mCustomerDatabase.updateChildren(userInfo);
        if (resultUri != null) {
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //phai lay tu cho nay taskSnapshot
//                    Uri downloadUrl = Uri.parse(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                    //taskSnapshot.getMetadata()

                    Uri uri = taskSnapshot.getUploadSessionUri();
                    StorageReference riversRef = mStorageRef.child("images/" + uri.getLastPathSegment());
                    uploadTask = riversRef.putFile(uri);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            Map userInfo = new HashMap();

                            userInfo.put("profileImages", uri.toString());


                            mCustomerDatabase.updateChildren(userInfo);
                        }
                    });



                    finish();
                    return;
                }
            });
        } else {
            finish();
        }
    }

    private void uploadImage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading file....");
        progressDialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINESE);
        Date now = new Date();
        String fileName = formatter.format(now);


        mStorageRef = FirebaseStorage.getInstance().getReference("images/" + fileName);

        mStorageRef.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProfileImage.setImageURI(null);
                Toast.makeText(SettingsActivity.this, "fall to upload", Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(SettingsActivity.this, "fall to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //final Uri imageUri = data.getData();
            resultUri = data.getData();
//            resultUri = imageUri;
//            image = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }

    private void uploadToFirebase(Uri uri) {
        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        name = mNameField.getText().toString();
                        phone = mPhoneField.getText().toString();

                        Model model = new Model(name,phone,uri.toString());
                        String modelId = mCustomerDatabase.push().getKey();

                        Map userInfo = new HashMap();
                        userInfo.put("name", name);
                        userInfo.put("phone", phone);
                        userInfo.put("imageUrl", uri.toString());
                        mCustomerDatabase.updateChildren(userInfo);


                        Toast.makeText(SettingsActivity.this, "success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, "fall", Toast.LENGTH_SHORT).show();
            }
        });
    }
}