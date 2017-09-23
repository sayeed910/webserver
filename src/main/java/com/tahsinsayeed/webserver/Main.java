package com.tahsinsayeed.webserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket socket = new ServerSocket(1234);
        Socket client = socket.accept();

        DataInputStream is = new DataInputStream(client.getInputStream());
        int data;
        while( (data =  is.read()) != -1){
            System.out.print((char)data);
        }

        Thread.sleep(1000);
        client.close();
    }
}
