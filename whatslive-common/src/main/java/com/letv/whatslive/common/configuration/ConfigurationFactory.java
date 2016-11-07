package com.letv.whatslive.common.configuration;

import com.letv.whatslive.common.utils.ClassLoaderUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoshan on 15-8-28.
 */
public class ConfigurationFactory {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationFactory.class);

    private static final String DEFAULT_CFG_FILE = "config.xml";

    private static final XPathFactory xpathFactory = XPathFactory.newInstance();
    private static final XPath xpath = xpathFactory.newXPath();

    private static Configuration _configurationInstance = null;

    private static XPathExpression RESOURCE_XPATH_EXPRESSION;
    static {
        try {
            RESOURCE_XPATH_EXPRESSION = xpath.compile("//properties/@resource");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public synchronized static Configuration getConfiguration() {
        if (_configurationInstance != null) {
            return _configurationInstance;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputStream is = ClassLoaderUtils.getStream(DEFAULT_CFG_FILE);
            if (is == null) {
                logger.warn("没有默认资源配置文件：" + DEFAULT_CFG_FILE);
                return null;
            }
            Document xmlDoc = builder.parse(is);
            if (xmlDoc == null) {
                return null;
            }
            NodeList resourceNodes = (NodeList) RESOURCE_XPATH_EXPRESSION.evaluate(xmlDoc, XPathConstants.NODESET);
            List<String> resourceNames = new ArrayList<String>();
            for (int i = 0, size = resourceNodes.getLength(); i < size; i++) {
                resourceNames.add(StringUtils.trimToEmpty(resourceNodes.item(i).getTextContent()));
            }
            _configurationInstance = new MixedConfiguration(resourceNames.toArray(new String[] {}));
            return _configurationInstance;
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return null;
    }

    public static void main(String[] args) {
        Configuration cfg = ConfigurationFactory.getConfiguration();
        System.out.println(cfg.getString("test.test"));
    }
}
