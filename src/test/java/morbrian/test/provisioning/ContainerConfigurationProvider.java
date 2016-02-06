package morbrian.test.provisioning;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.PasswordAuthentication;
import java.util.UUID;

public class ContainerConfigurationProvider {

  public static final String PROP_VENDOR = "arq.rest.vendor";
  public static final String PROP_USERNAME = "arq.rest.username";
  public static final String PROP_PASSWORD = "arq.rest.password";
  private final String username;
  private final String password;
  private final Logger logger = LoggerFactory.getLogger(ContainerConfigurationProvider.class);
  private VendorSpecificProvisioner vendorSpecificProvisioner;

  public ContainerConfigurationProvider() {
    Vendor vendor = Vendor
        .valueOf(getStringPropertyWithFallback(PROP_VENDOR, Vendor.WILDFLY.name()).toUpperCase());
    username = getStringPropertyWithFallback(PROP_USERNAME, "sampleperson");
    password = getStringPropertyWithFallback(PROP_PASSWORD, "changeme");

    switch (vendor) {
      case WILDFLY:
        vendorSpecificProvisioner = new WildflyProvisioner(this);
        break;
      case TOMEE:
        vendorSpecificProvisioner = new TomeeProvisioner(this);
        break;
      default:
        throw new IllegalArgumentException("vendor " + vendor + " not supported.");
    }
    logger.info("VendorSpecificProvisioner Implementation: " + vendorSpecificProvisioner.getClass()
        .getSimpleName());
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

  public Archive<?> createDeployment() {
    return ShrinkWrap.create(JavaArchive.class).addPackages(true, "morbrian.mormessages")
        .addPackages(true, "morbrian.test")
        .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
  }

  public VendorSpecificProvisioner getVendorSpecificProvisioner() {
    return vendorSpecificProvisioner;
  }

  public enum Vendor {
    WILDFLY("wildfly"), TOMEE("tomee");
    private final String value;

    Vendor(String value) {
      this.value = value;
    }
  }

  public PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(this.getUsername(),
        this.getPassword().toCharArray());
  }


}
