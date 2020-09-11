package com.example.shunyaassignment.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.shunyaassignment.Model.File;
import com.example.shunyaassignment.Respositry.FileRepo;

import java.util.ArrayList;

public class FileViewModel extends ViewModel
{
    FileRepo fileRepo=null;
    LiveData<ArrayList<File>> listLiveData;
    public FileViewModel()
    {
        fileRepo=FileRepo.getInstance();
    }
    public LiveData<ArrayList<File>> getFileData()
    {
        if (listLiveData==null)
        {
            listLiveData=fileRepo.retriveFilesFromFirestore();
        }
        return listLiveData;
    }

}
