package com.tahsinsayeed.webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.requireNonNull;

public class TaskHandler {
    private static final int THREAD_COUNT = 15;
    private final ExecutorService executor;
    private final RequestParserFactory factory;
    private final RequestBus requests;


    public TaskHandler(RequestBus requests, RequestParserFactory factory) {
        this.executor = Executors.newFixedThreadPool(THREAD_COUNT);
        this.factory = factory;
        this.requests = requests;
    }

    public void start() {
        executor.submit(() -> {
            while (!Thread.interrupted()) {
                if (requests.count() == 0)
                    sleep();
                else {
                    handleRequest();
                }
            }
        });
    }

    public void handleRequest() {
        Socket requestSocket = requests.getRequestSocket();
        requireNonNull(requestSocket);
        DataInputStream in = new DataInputStream(getInputStream(requestSocket));
        String request = getRequest(in);
        System.out.println(request);

        RequestParser parser = factory.get(request);


        File requestedFile = new File(parser.getFilePath());
        System.out.println(requestedFile.toString());
        DateFormat format = new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss zzz");
        if (requestedFile.exists()) {
            System.out.println("file exists");
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Date: " + format.format(new Date()) + "\r\n" +
                    "Server: Apache/2.2.14 (Win32)\r\n" +
                    "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n" +
                    "Content-Length: " + requestedFile.length() + "\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Connection: Closed\r\n\r\n";
            DataOutputStream out = new DataOutputStream(getOutputStream(requestSocket));
            sendData(response, requestedFile, out);
            closeStream(out);

        } else {
            System.out.println("file not exist");
            File notFoundFile = new File("notfound.html");
            String response = "HTTP/1.1 404 Not Found\r\n" +
                    "Date: " + format.format(new Date()) + "\r\n" +
                    "Server: Apache/2.2.14 (Win32)\r\n" +
                    "Content-Length: " + notFoundFile.length() + "\r\n" +
                    "Connection: Closed\r\n" +
                    "Content-Type: text/html\r\n\r\n";
            DataOutputStream out = new DataOutputStream(getOutputStream(requestSocket));
            sendData(response, notFoundFile, out);
            closeStream(out);

        }
    }

    public void sendData(String response, File requestedFile, DataOutputStream out) {
        try {
            System.out.println(response);

            System.out.println(Arrays.toString(Files.readAllBytes(requestedFile.toPath())));
            out.writeBytes(response);
            out.write(Files.readAllBytes(requestedFile.toPath()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public OutputStream getOutputStream(Socket requestSocket)  {
        try {
            return requestSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String getRequest(DataInputStream in) {
        StringBuffer sb = new StringBuffer();
        String input;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        try {
            while((input = reader.readLine()) != null){
                sb.append(input).append("\r\n");
                if (input.isEmpty()) break;
            }

            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void closeStream(Closeable closeable) {
        try{
            closeable.close();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private InputStream getInputStream(Socket requestSocket)  {
        try {
            return requestSocket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
