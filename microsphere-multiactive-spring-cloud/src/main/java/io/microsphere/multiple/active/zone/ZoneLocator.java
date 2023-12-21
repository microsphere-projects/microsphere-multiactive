package io.microsphere.multiple.active.zone;

import org.springframework.core.env.Environment;

/**
 * The locator for zone
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface ZoneLocator {

    /**
     * Current {@link ZoneLocator} supports or not ?
     * 
     * @param environment {@link Environment}
     * @return if <code>true</code>, {@link #locate(Environment)} will be invoked, or invocation will be
     *         ignored.
     */
    boolean supports(Environment environment);

    /**
     * Locate the zone
     * 
     * @return zone info
     */
    String locate(Environment environment);


}
