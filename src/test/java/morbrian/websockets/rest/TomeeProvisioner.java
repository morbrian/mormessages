package morbrian.websockets.rest;

import org.jboss.as.domain.management.security.adduser.AddUser;

public class TomeeProvisioner implements VendorSpecificProvisioner {

    private RestConfigurationProvider configProvider;

    public TomeeProvisioner(RestConfigurationProvider configProvider) {
        this.configProvider = configProvider;
    }

    @Override
    public void setup() {
        // TODO: add user when using tomee embedded
    }
}
