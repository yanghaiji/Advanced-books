package com.javayh.advanced.java.patterns.factory;

import com.javayh.advanced.java.patterns.factory.bean.Tomcat;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-14
 */
public class TomcatWebServerFactory<T> implements ServerFactory<Tomcat> {

    Logger logger = LoggerFactory.getLogger(TomcatWebServerFactory.class);

    private Tomcat tomcat;

    @SneakyThrows
    @Override
    public Tomcat create(int port) {
        tomcat = new Tomcat(port);
        ServerFactory.isNull(tomcat);
        logger.info("------create------");
        return tomcat;
    }

    @Override
    public void start() {
        logger.info("------start------");
        logger.info("------" + tomcat.getPort() + "------");
        logger.info("------start------");
    }

    @Override
    public void stop() {
        logger.info("------stop------");
    }

    @Override
    public void destroy() {
        logger.info("------destroy------");
        tomcat = null;
    }

}
