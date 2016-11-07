package com.letv.whatslive.inner.locator;

import com.letv.psp.swift.core.service.ServiceLocator;
import com.letv.whatslive.inner.utils.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleServiceLocator implements ServiceLocator {

	private static final Logger logger = LoggerFactory.getLogger(SimpleServiceLocator.class);

	public Object getService(String serviceName) {
		try {
			return SpringUtils.getBean(serviceName);
		} catch (Exception ex) {
			logger.error("", ex);
		}
		return null;
	}

}
