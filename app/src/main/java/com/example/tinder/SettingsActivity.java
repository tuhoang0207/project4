package com.example.tinder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
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

    private ProgressBar progressBar;
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

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // lấy giá trị của userSex từ bên main
        String userSex = getIntent().getExtras().getString("userSex");

        ArrayList<Uri> imgList = new ArrayList<>();

        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userId);
        // lấy thông tin user
        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {

                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                //set ảnh
                                Intent data = result.getData();
                                Uri selectedImageUri = data.getData();
                                mProfileImage.setImageURI(selectedImageUri);

                                resultUri = (Uri) selectedImageUri;

                            }
                        }
                    });


            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //chọn ảnh từ thẻ nhớ máy ảo
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                setResult(1);
                // mở album
                startActivityForResult(Intent.createChooser(intent, "Select picture"), 1);
            }
        });


        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                uploadImage();
//                String phonePattern = "/^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$/";
//                if (phone.isEmpty()){
//                    Toast.makeText(SettingsActivity.this, "khong duoc de trong", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (phone.matches(phonePattern)){
//                    Toast.makeText(getApplicationContext(),"valid phone",Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(),"invalid phone",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                uploadToFirebase(resultUri);
                finish();
                return;
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

    Uri imageUrl;
    private void getUserInfo() {
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // lấy dữ liệu ở trên firebase
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

                    if(map.get("imageUrl")!=null){
                        profileImageUrl = map.get("imageUrl").toString();
                        imageUrl = Uri.parse(map.get("imageUrl").toString());
//                        switch(profileImageUrl){
//                            // nếu
//                            case "default":
//                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(mProfileImage);
//                                break;
//                            default:
//                                Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
//                                break;
//                        }
                        if (map.get("imageUrl").toString().equals("default") && map.get("sex").toString().equals("Male")) {
                            Glide.with(getApplication()).load(R.mipmap.man).into(mProfileImage);
                        } else if(map.get("imageUrl").toString().equals("default") && map.get("sex").toString().equals("Female")) {
                            Glide.with(getApplication()).load(R.mipmap.woman).into(mProfileImage);
                        } else {
                            Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
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
        name = mNameField.getText().toString();
        phone = mPhoneField.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(SettingsActivity.this, "name mustn't empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty()) {
            Toast.makeText(SettingsActivity.this, "phone mustn't empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        mCustomerDatabase.updateChildren(userInfo);
        if(uri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance()
                    .getReference()
                    .child(System.currentTimeMillis() + "." + getFileExtension(uri));
            fileRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {


                                    Model model = new Model(name, phone, uri.toString());
                                    String modelId = mCustomerDatabase.push().getKey();

                                    Map userInfo = new HashMap();
                                    userInfo.put("name", name);
                                    userInfo.put("phone", phone);
                                    userInfo.put("imageUrl", uri.toString());
                                    mCustomerDatabase.updateChildren(userInfo);

                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(SettingsActivity.this, "success", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SettingsActivity.this, "fall", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}