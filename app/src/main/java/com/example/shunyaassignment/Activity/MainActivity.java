package com.example.shunyaassignment.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shunyaassignment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    Button select,upload,retrive;
    TextView fileSelectedStaus;
    FirebaseStorage reference;
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    CollectionReference filesDatabase=db.collection("Files");
    public static  final int CODE=123;
    Uri uri;
    ProgressBar progressBar;
    String downloadUrl;
    String fileExtension;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        select=findViewById(R.id.Select);
        upload=findViewById(R.id.uploadFile);
        retrive=findViewById(R.id.retriveFile);
        fileSelectedStaus=findViewById(R.id.fileSelectedStatus);
        progressBar=findViewById(R.id.progressbar);
        reference=FirebaseStorage.getInstance();

        select.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showDialogBox();
            }
        });
        upload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                uploadFileToFirebase(uri);
            }
        });
        retrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retriveFileFromFirebase();
            }
        });
    }
    public void selectFileFromLocal(String fileextension)
    {
        Intent intent=new Intent();
        switch (fileextension)
        {
            case ".pdf":
            {
                intent.setType("application/pdf");
                break;
            }
            case ".docx":
            {
                intent.setType("application/msword");
                break;
            }
            case ".text":
            {
                intent.setType("text/plain");
                break;
            }
            case ".xls":
            {
                intent.setType("application/vnd.ms-excel");
                break;
            }
            case ".ppt":
            {
                intent.setType("application/vnd.ms-powerpoint");
                break;
            }
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CODE&&resultCode==RESULT_OK&&data!=null)
        {
            fileSelectedStaus.setText("File Selected! Click on upload button");
            uri=data.getData();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please select the file",Toast.LENGTH_LONG).show();
        }
    }
    public void uploadFileToFirebase(Uri uri)
    {
        if (uri!=null)
        {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference=reference.getReference("UploadedFiles").child("file"+ Timestamp.now().getSeconds());
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot)
                {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            downloadUrl=uri.toString();
                            Map<String,String>  hmap=new HashMap<>();
                            String fileName=taskSnapshot.getMetadata().getName().toString();
                            hmap.put("FileUrl",downloadUrl);
                            hmap.put("FileName",fileName);
                            hmap.put("FileExt",fileExtension);
                            filesDatabase.add(hmap)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                fileSelectedStaus.setText("No file selected");
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getApplicationContext(),"Uploaded sucessfully",Toast.LENGTH_LONG).show();
                                            }
                                            else
                                            {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getApplicationContext(),""+task.getException().getMessage().toString(),Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            Log.d("FileUrl",""+downloadUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please select the file to upload",Toast.LENGTH_LONG).show();
        }
    }
    public void retriveFileFromFirebase()
    {
        startActivity(new Intent(MainActivity.this, RetriveActivity.class));
    }
    public void showDialogBox()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("AlertDialog");
        final String[] items = {".pdf",".docx",".text",".xls",".ppt"};
        int checkedItem = 1;
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        selectFileFromLocal(items[0]);
                        break;
                    case 1:
                        selectFileFromLocal(items[1]);
                        break;
                    case 2:
                        selectFileFromLocal(items[2]);
                        break;
                    case 3:
                        selectFileFromLocal(items[3]);
                        break;
                    case 4:
                        selectFileFromLocal(items[4]);
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}
