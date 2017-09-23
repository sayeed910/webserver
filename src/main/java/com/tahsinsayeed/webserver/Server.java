package com.tahsinsayeed.webserver;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;

import static java.util.Objects.*;

public class Server {
    private final static Logger logger = Logger.getGlobal();

    private final RequestBus requestBus;
    private final ServerSocket serverSocket;
    private volatile Boolean listening;
    private final ExecutorService listener;

    public static Server create(ServerSocket serverSocket, RequestBus requestBus) {
        return new Server(serverSocket, requestBus);
    }

    private Server(ServerSocket serverSocket, RequestBus requestBus) {
        requireNonNull(serverSocket, "ServerSocket can not be null");
        requireNonNull(requestBus, "RequestBus can not be null");

        this.serverSocket = serverSocket;
        this.requestBus = requestBus;
        this.listening = false;
        this.listener = Executors.newSingleThreadExecutor();
    }

    public void startListening() {
        listener.submit(this::acceptConnection);

    }

    private void acceptConnection() {
        synchronized (listening) {
            listening = true;
        }

        while (!Thread.interrupted()) {
            try {
                Socket clientSocket = serverSocket.accept();
                requestBus.pushRequest(clientSocket);
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }

        synchronized (listening) {
            listening = false;
        }

    }


    public boolean isListening() {
        return listening;
    }

    public void stopListening() {
        listener.shutdownNow();
        listening = false;

    }

    public void close() {
        if (isListening()) stopListening();
        try {
            requestBus.clear();
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
