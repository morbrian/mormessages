package morbrian.websockets.rest;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import java.util.UUID;

public class RestConfigurationProvider {

    public static final String PROP_VENDOR = "settings.rest.vendor";
    public static final String PROP_HOSTPROTOCOLPORT = "settings.rest.hostProtocolPort";
    public static final String PROP_USERNAME = "settings.rest.username";
    public static final String PROP_PASSWORD = "settings.rest.password";
    private Vendor vendor;
    private String username;
    private String password;
    private String restProtocolHostPort;
    private VendorSpecificProvisioner vendorSpecificProvisioner;

    public RestConfigurationProvider() {
        System.out.println(Thread.currentThread().getContextClassLoader());
        vendor = Vendor.valueOf(getStringPropertyWithFallback(PROP_VENDOR, Vendor.WILDFLY.name()));
        restProtocolHostPort = getStringPropertyWithFallback(PROP_HOSTPROTOCOLPORT, "http://127.0.0.1:8080");
        username = getStringPropertyWithFallback(PROP_USERNAME, "samplepreson");
        password = getStringPropertyWithFallback(PROP_PASSWORD, "changeme");

        switch(vendor) {
            case WILDFLY:
                vendorSpecificProvisioner = new WildflyProvisioner(this);
                break;
            default:
                throw new IllegalArgumentException("vendor " + vendor + " not supported.");
        }
    }

    private static String getStringPropertyWithFallback(String key, String fallback) {
        return (String) getAnyPropertyWithFallback(key, fallback);
    }

    private static Object getAnyPropertyWithFallback(String key, Object fallback) {
        String envValue = System.getProperty(key);
        if (envValue == null || envValue.isEmpty()) {
            return fallback;
        }
        return envValue;
    }

    public static String randomAlphaNumericString() {
        // random enough for test purposes
        return "a" + UUID.randomUUID().toString().replace('-', 'a');
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRestProtocolHostPort() {
        return restProtocolHostPort;
    }

    public JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, "morbrian.websockets")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    public VendorSpecificProvisioner getVendorSpecificProvisioner() {
        return vendorSpecificProvisioner;
    }

    public enum Vendor {
        WILDFLY("wildfly");
        private String value;
        Vendor(String value) { this.value = value; }
    }
}
