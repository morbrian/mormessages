package morbrian.test.provisioning;

import org.jboss.as.domain.management.security.adduser.AddUser;

public class WildflyProvisioner implements VendorSpecificProvisioner {

  private final ContainerConfigurationProvider configProvider;

  public WildflyProvisioner(ContainerConfigurationProvider configProvider) {
    this.configProvider = configProvider;
  }

  @Override public void setup() {
    String home = System.getenv("JBOSS_HOME");
    String standaloneConf = home + "/standalone/configuration";
    String userProps = standaloneConf + "/application-users.properties";
    String groupProps = standaloneConf + "/application-roles.properties";
    String[] args =
        {"-sc", standaloneConf, "-up", userProps, "-gp", groupProps, "-a", "-g", "user", "-u",
            configProvider.getUsername(), "-p", configProvider.getPassword(), "-s"
            // -s means silent, remove to see AddUser console output
        };
    try {
      AddUser.main(args);
    } catch (Throwable th) {
      th.printStackTrace();
      throw th;
    }
  }
}
