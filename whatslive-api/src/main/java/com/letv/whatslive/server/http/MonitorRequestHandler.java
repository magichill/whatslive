package com.letv.whatslive.server.http;


import me.zhuoran.amoeba.netty.server.http.AbstractExecutor;
import me.zhuoran.amoeba.netty.server.http.AmoebaHttpRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

/**
 * Created by zoran on 14-9-25.
 */
@Service("monitor")
public class MonitorRequestHandler extends AbstractExecutor {

    private final static Logger logger = LoggerFactory.getLogger(MonitorRequestHandler.class);


    @Override
    public Object execute(AmoebaHttpRequest request) {

        try {
            StringBuilder sb = new StringBuilder();
            InputStream in = MonitorRequestHandler.class.getClassLoader().getResourceAsStream(
                    "ServerInfo");
            List<String> lines = IOUtils.readLines(in);
            for(String line : lines){
                sb.append(line).append("\r\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
