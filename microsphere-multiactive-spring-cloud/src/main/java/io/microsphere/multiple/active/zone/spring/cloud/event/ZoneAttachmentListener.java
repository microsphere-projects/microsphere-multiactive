package io.microsphere.multiple.active.zone.spring.cloud.event;

import io.microsphere.multiple.active.zone.ZoneAttachmentHandler;
import io.microsphere.multiple.active.zone.ZoneContext;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreRegisteredEvent;
import org.springframework.beans.BeansException;
import org.springframework.cloud.client.discovery.event.InstancePreRegisteredEvent;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * A listener for the {@link InstancePreRegisteredEvent instance pre-registered event} to attach to
 * the zone.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see ZoneContext
 * @since 1.0.0
 */
public class ZoneAttachmentListener implements ApplicationListener<RegistrationPreRegisteredEvent>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(RegistrationPreRegisteredEvent event) {
        Registration registration = event.getRegistration();
        Map<String, String> metadata = registration.getMetadata();
        ZoneAttachmentHandler zoneAttachmentHandler = applicationContext.getBean(ZoneAttachmentHandler.class);
        zoneAttachmentHandler.attachZone(metadata);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
