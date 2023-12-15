package io.microsphere.multiple.active.zone.eureka;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
import com.netflix.discovery.PreRegistrationHandler;
import io.microsphere.multiple.active.zone.ZoneAttachmentHandler;
import io.microsphere.multiple.active.zone.ZoneAttachmentListener;

import java.util.Map;

/**
 * Eureka {@link PreRegistrationHandler} to attach zone.
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see AbstractDiscoveryClientOptionalArgs
 * @see ZoneAttachmentListener
 * @since 1.0.0
 */
public class ZoneAttachmentPreRegistrationHandler implements PreRegistrationHandler {

    private final ApplicationInfoManager applicationInfoManager;

    private final ZoneAttachmentHandler zoneAttachmentHandler;

    public ZoneAttachmentPreRegistrationHandler(ApplicationInfoManager applicationInfoManager, ZoneAttachmentHandler zoneAttachmentHandler) {
        this.applicationInfoManager = applicationInfoManager;
        this.zoneAttachmentHandler = zoneAttachmentHandler;
    }

    @Override
    public void beforeRegistration() {
        InstanceInfo instanceInfo = applicationInfoManager.getInfo();
        Map<String, String> metadata = instanceInfo.getMetadata();
        zoneAttachmentHandler.attachZone(metadata);
    }

}
