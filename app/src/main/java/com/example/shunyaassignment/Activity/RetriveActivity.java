package com.example.shunyaassignment.Activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.shunyaassignment.Adapters.RecyclerViewAdapte;
import com.example.shunyaassignment.Model.File;
import com.example.shunyaassignment.R;
import com.example.shunyaassignment.ViewModel.FileViewModel;
import com.google.firebase.storage.FirebaseStorage;
import java.util.ArrayList;


public class RetriveActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    FileViewModel fileViewModel;
    RecyclerView.Adapter adapter;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrive);
        recyclerView=findViewById(R.id.allFilesList);
        firebaseStorage=FirebaseStorage.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        fileViewModel=ViewModelProviders.of(this).get(FileViewModel.class);
        fileViewModel.getFileData().observe(this, new Observer<ArrayList<File>>()
        {
            @Override
            public void onChanged(ArrayList<File> fileArrayList)
            {
                adapter=new RecyclerViewAdapte(getApplicationContext(),fileArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

    }

}