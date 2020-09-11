package com.csemaster.adminnovelapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

public class AddChapterActivity extends AppCompatActivity {
    TextView title,status;
    Button upload,save;
    EditText titleOfChapter,ChapterNo;
    boolean isUploaded=false;
    Uri filePath;
    String downloadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);
        title=findViewById(R.id.titleOfNovel);
        status=findViewById(R.id.uploadStatus);
        upload=findViewById(R.id.uploadFile);
        save=findViewById(R.id.saveChapter);
        titleOfChapter=findViewById(R.id.chapterTitle);
        ChapterNo=findViewById(R.id.chapterNo);


        title.setText(getIntent().getStringExtra("title"));


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean hasPermission=(ContextCompat.checkSelfPermission(AddChapterActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED);

                if(!hasPermission)
                {
                    ActivityCompat.requestPermissions(AddChapterActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            125);

                }
                else
                {
                    showFileChooser();
                    //UploadFile();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(titleOfChapter.getText().toString()) && !TextUtils.isEmpty(ChapterNo.getText().toString()) && isUploaded)
                {
                    DocumentReference collectionReference=FirebaseFirestore.getInstance().collection("AllNovels").document(getIntent().getStringExtra("title"))
                            .collection("Chapters").document("Chapter "+ChapterNo.getText().toString());

                    HashMap<String,Object> chapterHashMap=new HashMap<>();

                    chapterHashMap.put("title",titleOfChapter.getText().toString());
                    chapterHashMap.put("story",downloadUrl);

                    collectionReference.set(chapterHashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddChapterActivity.this, "Chapter Successfully Added", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                }
            }
        });


    }

    private void UploadFile()
    {
        if(!TextUtils.isEmpty(ChapterNo.getText().toString()))
        {
            FirebaseStorage storage= FirebaseStorage.getInstance();
            status.setText("Uploading....");
            final StorageReference storageReference=storage.getReference(getIntent().getStringExtra("title")+" Chapters").child("Chapter "+ChapterNo.getText().toString());
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.show();

            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                  downloadUrl=uri.toString();
                                }
                            });
                            progressDialog.hide();
                            Toast.makeText(AddChapterActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            status.setText("Uploaded");
                            isUploaded=true;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.hide();
                    Toast.makeText(AddChapterActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    status.setText("Failed"+e.getMessage());
                }
            });
        }

    }

    private void showFileChooser() {
        Intent intent=new Intent();

        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.setType("text/plain");
        startActivityForResult(intent,1212);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==125 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            showFileChooser();
            //UploadFile();
        }
        else
        {
            Toast.makeText(this, "Please give all permission", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1212 && resultCode==RESULT_OK)
        {
            filePath=data.getData();
            UploadFile();
        }
    }
}
