package morbrian.websockets.persistence;

import morbrian.test.provisioning.ContainerConfigurationProvider;
import morbrian.test.provisioning.VendorSpecificProvisioner;
import morbrian.websockets.model.ForumEntity;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class) public class PersistenceTest {

  private static final ContainerConfigurationProvider
      configProvider = new ContainerConfigurationProvider();

  @Inject Repository repository;
  @Inject Persistence persistence;

  @Deployment public static JavaArchive createDeployment() {
    return configProvider.createDeployment();
  }

  @BeforeClass public static void setupClass() throws Throwable {
    VendorSpecificProvisioner provisioner = configProvider.getVendorSpecificProvisioner();
    provisioner.setup();
  }

  @Before public void setup() {
    assertNotNull(repository);
    assertNotNull(persistence);
  }

  @Test public void shouldCreateForum() {
    ForumEntity expectedForum = createRandomNewForum();

    ForumEntity createdForum = persistence.createForum(expectedForum);
    assertTrue("forum id", createdForum.getId() > 0);
  }

  private ForumEntity createRandomNewForum() {
    return new ForumEntity(
        ContainerConfigurationProvider.randomAlphaNumericString(),
        ContainerConfigurationProvider.randomAlphaNumericString(),
        ContainerConfigurationProvider.randomAlphaNumericString()
    );
  }

}
