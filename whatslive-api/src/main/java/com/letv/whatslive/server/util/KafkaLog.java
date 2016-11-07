package com.letv.whatslive.server.util;


import com.letv.whatslive.common.http.RequestBody;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;

/**
 * Created by zoran on 14-11-21.
 */
public class KafkaLog implements Runnable {


    private RequestHeader header;

    private RequestBody cmd;

    private ResponseBody result;

    private long execTime;

    public KafkaLog(RequestHeader header, RequestBody cmd) {
        this.header = header;
        this.cmd = cmd;
    }

    public KafkaLog(RequestHeader header, RequestBody cmd, ResponseBody result, long execTime) {
        this.header = header;
        this.cmd = cmd;
        this.result = result;
        this.execTime = execTime;
    }

    @Override
    public void run() {

        //log response to kafka
        if (header != null && cmd != null && result != null && execTime > 0) {
            LogUtils.logResponse(header, cmd, result, execTime);
        }

        //log request to kafka
        else if(header!=null && cmd!=null){
            LogUtils.logRequest(header,cmd);
            LogUtils.logParams(header,cmd);
        }


    }
}
