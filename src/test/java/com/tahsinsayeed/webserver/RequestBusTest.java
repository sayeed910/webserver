package com.tahsinsayeed.webserver;

import org.junit.Before;
import org.junit.Test;

import java.net.Socket;

import static org.junit.Assert.*;

public class RequestBusTest {

    private RequestBus requestBus;

    @Before
    public void setup(){
        requestBus = RequestBus.create();
    }
    @Test
    public void testPushRequest(){
        requestBus.pushRequest(new Socket());

        assertEquals(1, requestBus.count());
    }

    @Test
    public void testPushRequest_Null_NotAdded(){
        requestBus.pushRequest(null);
        assertEquals(0, requestBus.count());
    }
}