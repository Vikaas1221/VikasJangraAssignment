package com.example.shunyaassignment.Model;

public class File
{
    private String filename;
    private String fileurl;
    private String fileExt;
    public File(String filename, String fileurl,String fileExt)
    {
        this.filename = filename;
        this.fileurl = fileurl;
        this.fileExt=fileExt;
    }

    public String getFilename()
    {
        return filename;
    }

    public String getFileurl()
    {
        return fileurl;
    }
    public String getFileExt()
    {
        return fileExt;
    }
}
