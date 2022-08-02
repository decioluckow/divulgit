package org.divulgit.remote.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Response {

    private int status;
    private String statusLine;
    private String body;

    public static Response build(int status, String statusLine, String body) {
        return new Response(status, statusLine, body);
    }
}
