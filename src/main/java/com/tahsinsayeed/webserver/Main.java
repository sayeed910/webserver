package com.tahsinsayeed.webserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket socket = new ServerSocket(8080);
        RequestBus bus = RequestBus.create();
        Server server = Server.create(socket, bus);
        TaskHandler handler = new TaskHandler(bus, new RequestParserFactory());
        server.startListening();
        handler.start();
    }
}
