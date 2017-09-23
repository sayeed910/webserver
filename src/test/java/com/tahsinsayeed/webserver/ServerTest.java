package com.tahsinsayeed.webserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ServerTest {

    private Server httpServer;
    private RequestBus requests;



    @Before
    public void setup() throws IOException {

        requests = mock(RequestBus.class);
        httpServer = Server.create(new ServerSocket(8080), requests);
    }

    @After
    public void teardown(){
        httpServer.stopListening();
        httpServer.close();
        httpServer = null;
    }

    @Test(expected = NullPointerException.class)
    public void testCreation_NullInput(){
        Server invalidServer = Server.create(null, null);
    }

    @Test
    public void testStartListening_RequestAddedToBus() throws IOException, InterruptedException {

        httpServer.startListening();

        Socket client = new Socket("localhost", 8080);
        Thread.sleep(200);

        verify(requests).pushRequest(any(Socket.class));

    }
    @Test
    public void testStartListening_OnException_RequestNotAddedToBus() throws IOException {
        ServerSocket socket = mock(ServerSocket.class);
        when(socket.accept()).thenThrow(IOException.class);
        Server server = Server.create(socket, requests);
        server.startListening();

        verify(requests, never()).pushRequest(any());
    }

    @Test
    public void testIsListening_AfterStartListening_True() throws Exception {

        httpServer.startListening();

        Thread.sleep(200);

        assertTrue(httpServer.isListening());
    }

    @Test
    public void testIsListening_AfterStopListening_False(){
        httpServer.startListening();
        httpServer.stopListening();

        assertFalse(httpServer.isListening());
    }

    @Test
    public void testStopListening_AfterStop_NoConnectionMade() throws IOException, InterruptedException {
        httpServer.startListening();
        httpServer.stopListening();

        Socket client = new Socket("localhost", 8080);
        Thread.sleep(200);
        verify(requests, never()).pushRequest(any());

    }

    @Test(expected = RuntimeException.class)
    public void testClose_OnIOException_RuntimeExceptionThrown() throws IOException {
        ServerSocket socket = mock(ServerSocket.class);
        doThrow(IOException.class).when(socket).close();

        Server server = Server.create(socket, requests);
        server.close();
    }

    @Test
    public void testNoConnectionMadeBeforeStartListening() throws IOException{
        Socket socket = new Socket("localhost", 8080);
        verify(requests, never()).pushRequest(any());
    }

    @Test
    public void onServerClose_ClearRequestBus(){
        httpServer.close();
        verify(requests).clear();
    }


}
