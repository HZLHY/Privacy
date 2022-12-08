package com.example.privacy.utils;

public class getFileNameFromPath {
    public String getName(String path){
        int start = path.lastIndexOf("/");
        if(start!=-1){
           return path.substring(start+1);
        }else {
            return null;
        }
    }
}
