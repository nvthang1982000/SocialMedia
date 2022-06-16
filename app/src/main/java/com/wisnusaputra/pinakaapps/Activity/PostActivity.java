package com.wisnusaputra.pinakaapps.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.wisnusaputra.pinakaapps.R;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Uri mImageUri;
    String miUrlOk = "";
    private StorageTask uploadTask;
    StorageReference storageRef;
    private Uri selectedUri;
    MediaController mediaController;


    private static final int PICK_FILE = 1;
    VideoView video_added;
    ImageView close, image_added;
    TextView post;
    EditText description;
    Spinner category;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        video_added = findViewById(R.id.video_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        category = findViewById(R.id.category);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(video_added);

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(PostActivity.this, R.array.category, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapterSpinner);

        storageRef = FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage_10();
            }
        });


        Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        it.setType("image/* video/*");
        startActivityForResult(it,PICK_FILE);

//        CropImage.activity()
//                .setAspectRatio(2,1)
//                .start(PostActivity.this);
    }


    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage_10(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting....");
        pd.show();
        if (mImageUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);

            String desc = description.getText().toString();
            if (desc.length() <= 0){
                description.setError("Please Insert Document to long");
                if (TextUtils.isEmpty(desc)) {
                    description.setError("Please insert Description");
                    Toast.makeText(this, "Please Insert Description", Toast.LENGTH_SHORT).show();
                }
                pd.dismiss();
            }else {
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            miUrlOk = downloadUri.toString();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                            String postid = reference.push().getKey();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("postid", postid);
                            hashMap.put("postimage", miUrlOk);
                            hashMap.put("description", description.getText().toString());
                            hashMap.put("category", category.getSelectedItem().toString());
                            hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            hashMap.put("type", type);

                            reference.child(postid).setValue(hashMap);

                            pd.dismiss();

                            startActivity(new Intent(PostActivity.this, MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(PostActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(PostActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            mImageUri = result.getUri();
//
//            image_added.setImageURI(mImageUri);
//        } else {
//            startActivity(new Intent(PostActivity.this, MainActivity.class));
//            finish();
//        }

        if(requestCode == PICK_FILE || resultCode == RESULT_OK || data != null || data.getData() != null){
            mImageUri = data.getData();
            Log.d("URILOG", "onActivityResult: "+mImageUri );
            if(mImageUri.toString().contains("IMG")){
                image_added.setVisibility(View.VISIBLE);
                video_added.setVisibility(View.INVISIBLE);
                image_added.setImageURI(mImageUri);
                type = "iv";
            }
            else if(mImageUri.toString().contains("mp4")){
                video_added.setVisibility(View.VISIBLE);
                image_added.setVisibility(View.INVISIBLE);
                video_added.setMediaController(mediaController);

                video_added.setVideoURI(mImageUri);
                video_added.start();
                type = "vv";
            }else{
                Toast.makeText(PostActivity.this, "No file selected",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        }
    }

}
