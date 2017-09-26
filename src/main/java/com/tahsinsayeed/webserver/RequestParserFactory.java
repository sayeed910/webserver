package com.tahsinsayeed.webserver;

public class RequestParserFactory {
    public RequestParser get(String request) {
        return new RequestParser(request);
    }
}
