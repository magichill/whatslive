package com.letv.whatslive.web.controller.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Title:
 * Desc:
 * User: crespo
 * Company: www.letv.com
 * Date: 14-4-9 上午9:59
 */
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value = "")
    public String index() {
        return "index";
    }

}
