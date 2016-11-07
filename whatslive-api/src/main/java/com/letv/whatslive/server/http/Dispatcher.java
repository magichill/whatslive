package com.letv.whatslive.server.http;

import com.google.common.collect.ArrayListMultimap;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.Reflections;
import com.letv.whatslive.server.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 14-10-24.
 */
@Service
public class Dispatcher {

    private final static Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    public static final String ROUTE_CONFIG = "/route.yml";

    private Map<String, Object> configMap;

    ArrayListMultimap map = ArrayListMultimap.create();

    @PostConstruct
    void init() {
        InputStream input = null;
        try {
            input = this.getClass().getResourceAsStream(ROUTE_CONFIG);
            Yaml yaml = new Yaml();
            if (null == input) {
                logger.error("Can't find route.yml ! Application exit....");
                System.exit(-1);
            } else {
                this.configMap = (Map<String, Object>) yaml.load(input);
                if (configMap != null && configMap.size() > 0) {
                    Iterator<Map.Entry<String, Object>> iter = configMap.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String, Object> config = (Map.Entry<String, Object>) iter.next();
                        String configStr = (String) config.getValue();
                        String configArr[] = configStr.split(",");
                        String controller = configArr[0];
                        String method = configArr[1];
                        map.put(config.getKey(), controller);
                        map.put(config.getKey(), method);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            System.exit(-1);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public ResponseBody execute(String cmd, Object[] params) throws Exception{
        if (map.containsKey(cmd)) {
            List<String> values = map.get(cmd);
            String controller = values.get(0);
            String method = values.get(1);
            try {
                Object targetClass = Main.ctx.getBean(controller);
                return (ResponseBody) Reflections.invokeMethodByName(targetClass, method, params);
            }catch(NoSuchBeanDefinitionException e){
                throw new IllegalArgumentException("No found Controller [" + controller + "] in Application Context !");
            }catch (Exception e) {
                throw e;
            }
        } else {
            throw new IllegalArgumentException("No found CMD [" + cmd + "] in Route Config !");
        }

    }

}
