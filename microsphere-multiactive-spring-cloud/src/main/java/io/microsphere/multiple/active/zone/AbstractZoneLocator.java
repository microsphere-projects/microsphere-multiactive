package io.microsphere.multiple.active.zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_TIMEOUT;
import static io.microsphere.multiple.active.zone.ZoneConstants.LOCATOR_TIMEOUT_PROPERTY_NAME;

/**
 * The abstract implementation {@link ZoneLocator}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class AbstractZoneLocator implements ZoneLocator, BeanNameAware, Ordered {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String beanName;

    protected int order;

    public AbstractZoneLocator(int order) {
        setOrder(order);
    }

    @Override
    public final void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    public final void setOrder(int order) {
        this.order = order;
    }


    @Override
    public String toString() {
        return "ZoneLocator{" + "beanName='" + beanName + '\'' + ", order=" + order + '}';
    }

}
