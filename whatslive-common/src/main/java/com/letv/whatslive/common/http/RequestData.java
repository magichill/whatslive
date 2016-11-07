package com.letv.whatslive.common.http;

import java.util.List;

/**
 * Created by zoran on 14-9-15.
 */
public class RequestData {

    private List<RequestBody> commands;

    private RequestHeader header;

    public List<RequestBody> getCommands() {
        return commands;
    }

    public void setCommands(List<RequestBody> commands) {
        this.commands = commands;
    }

    public RequestHeader getHeader() {
        return header;
    }

    public void setHeader(RequestHeader header) {
        this.header = header;
    }
}
