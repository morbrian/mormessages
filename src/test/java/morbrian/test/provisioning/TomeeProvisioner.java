package morbrian.test.provisioning;

public class TomeeProvisioner implements VendorSpecificProvisioner {

  private ContainerConfigurationProvider configProvider;

  public TomeeProvisioner(ContainerConfigurationProvider configProvider) {
    this.configProvider = configProvider;
  }

  @Override public void setup() {
    // TODO: add user when using tomee embedded
  }
}
