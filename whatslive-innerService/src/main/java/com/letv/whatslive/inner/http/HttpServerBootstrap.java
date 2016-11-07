package com.letv.whatslive.inner.http;

import com.letv.psp.swift.common.util.StringUtils;
import com.letv.psp.swift.core.component.UrlRewriter;
import com.letv.psp.swift.httpd.server.DefaultHttpServer;
import com.letv.whatslive.inner.utils.util.SpringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wangjian7 on 2015/7/17.
 */
public class HttpServerBootstrap extends DefaultHttpServer {
    private static final int DEFAULT_PORT = 8022;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            port = StringUtils.toInt(args[0], DEFAULT_PORT);
        }
        HttpServerBootstrap bootstrap = new HttpServerBootstrap();

        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/applicationContext.xml");
        SpringUtils.setApplicationContext(ctx);

        bootstrap.addComponent(new UrlRewriter(null));
        bootstrap.addChannelHandler(new CustomHttpServiceDispatcherServerHandler());
        bootstrap.listen(port);
    }
}
