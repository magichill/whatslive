package com.letv.whatslive.common.http;

import java.util.List;

/**
 * Created by zoran on 14-9-15.
 */
public class ResponseData {


    public ResponseData(){
        header = new ResponseHeader();
        header.setEncode("0");
    }

    private List<ResponseBody> results;

    private ResponseHeader header;

    public ResponseHeader getHeader() {
        return header;
    }

    public void setHeader(ResponseHeader header) {
        this.header = header;
    }

    public List<ResponseBody> getResults() {
        return results;
    }

    public void setResults(List<ResponseBody> results) {
        this.results = results;
    }
}
