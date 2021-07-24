package com.koddev.instagram;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("ALL")
public class Add_Story_Activity extends AppCompatActivity {

    private Uri mImageUri;
    String miUrlOk = "";
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        storageRef = FirebaseStorage.getInstance().getReference("story");

        CropImage.activity()
                .setAspectRatio(9,16)
                .start(Add_Story_Activity.this);

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage_10(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting");
        pd.show();
        if (mImageUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            StorageTask uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    miUrlOk = downloadUri.toString();

                    String myid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                            .child(myid);

                    String storyid = reference.push().getKey();
                    long timeend = System.currentTimeMillis()+86400000; // 1 day later

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageurl", miUrlOk);
                    hashMap.put("timestart", ServerValue.TIMESTAMP);
                    hashMap.put("timeend", timeend);
                    hashMap.put("storyid", storyid);
                    hashMap.put("userid", myid);

                    reference.child(storyid).setValue(hashMap);

                    pd.dismiss();

                    finish();

                } else {
                    Toast.makeText(Add_Story_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(Add_Story_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show());

        } else {
            Toast.makeText(Add_Story_Activity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage_10();

        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Add_Story_Activity.this, Main_Activity.class));
            finish();
        }
    }
}
