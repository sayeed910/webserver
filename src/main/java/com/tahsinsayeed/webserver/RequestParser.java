package com.tahsinsayeed.webserver;

import com.sun.media.jfxmedia.logging.Logger;

import java.io.File;
import java.util.Scanner;

public class RequestParser {
    private final String request;

    public RequestParser(String request) {
        this.request = request;
    }

    public String getFilePath(){
        Logger.logMsg(1, "inside");
        Scanner sc = new Scanner(request);
        sc.next();
        String file = sc.next();
        String path = new File( file.replace("/", "")).getAbsolutePath();
//        String path = getClass().getResource(file).getPath();
        System.out.println("upto here");
        System.out.println(path);
        return path;
    }
}
