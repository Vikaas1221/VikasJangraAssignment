package com.example.shunyaassignment.Respositry;

import androidx.lifecycle.MutableLiveData;

import com.example.shunyaassignment.Model.File;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class FileRepo
{
    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    CollectionReference FileDatabase=firestore.collection("Files");
    private static FileRepo instance;
    public static FileRepo getInstance()
    {
        if (instance==null)
        {
            instance=new FileRepo();
        }
        return instance;
    }
    public MutableLiveData<ArrayList<File>> retriveFilesFromFirestore()
    {
        final MutableLiveData<ArrayList<File>> data=new MutableLiveData<>();
        final ArrayList<File> arrayList=new ArrayList<>();
        FileDatabase.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    String filename=documentSnapshot.getString("FileName");
                    String fileurl=documentSnapshot.getString("FileUrl");
                    String fileext=documentSnapshot.getString("FileExt");
                    arrayList.add(new File(filename,fileurl,fileext));
                }
                data.setValue(arrayList);
            }
        });
        return data;
    }
}
