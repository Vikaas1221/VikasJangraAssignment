package com.example.shunyaassignment.Adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shunyaassignment.Model.File;
import com.example.shunyaassignment.R;


import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class RecyclerViewAdapte extends RecyclerView.Adapter<RecyclerViewAdapte.ViewHolder>
{

    Context context;
    ArrayList<File> fileArrayList;
    public RecyclerViewAdapte(Context context,ArrayList<File> fileArrayList)
    {
        this.context=context;
        this.fileArrayList=fileArrayList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapte.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_file_layout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapte.ViewHolder holder, int position)
    {
        final File file=fileArrayList.get(position);
        holder.filename.setText(file.getFilename());
        holder.download.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                downloadFile(context,file.getFilename(),file.getFileExt(),DIRECTORY_DOWNLOADS,file.getFileurl());
            }
        });
    }
    public void downloadFile(Context context,String filename,String fileextension,String destinationdirectory,String url)
    {
        DownloadManager downloadManager=(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationdirectory,filename);
        downloadManager.enqueue(request);
    }

    @Override
    public int getItemCount()
    {
        return fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView filename,download;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            filename=itemView.findViewById(R.id.filename);
            download=itemView.findViewById(R.id.download);
        }
    }
}
