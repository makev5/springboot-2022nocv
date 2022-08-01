package com.make.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLog {

    public static final Logger logger = LoggerFactory.getLogger(TestLog.class);

    public static void main(String[] args) {
        logger.trace("trace");
        logger.info("info");
        logger.warn("warn");
        logger.debug("debug");
        logger.error("error");
    }
}
