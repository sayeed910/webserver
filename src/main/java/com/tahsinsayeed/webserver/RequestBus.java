package com.tahsinsayeed.webserver;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RequestBus {
    private final ConcurrentLinkedQueue<Socket> requestBuffer;

    private RequestBus() {
        this.requestBuffer = new ConcurrentLinkedQueue<>();
    }

    public static RequestBus create() {
        return new RequestBus();
    }

    public void pushRequest(Socket socket){
        System.out.println("socket added");
        if (socket != null) requestBuffer.add(socket);
    }

    public Socket getRequestSocket(){

        System.out.println("socket taken");
        return requestBuffer.poll();
    }

    public int count() {
        return requestBuffer.size();
    }

    public void clear() {
        requestBuffer.clear();
    }


}
