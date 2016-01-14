package morbrian.websockets.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import morbrian.test.provisioning.ContainerConfigurationProvider;
import morbrian.test.provisioning.VendorSpecificProvisioner;
import morbrian.websockets.model.ForumEntity;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.*;

@RunWith(Arquillian.class) public class PersistenceTest {

  private static final ContainerConfigurationProvider configProvider =
      new ContainerConfigurationProvider();

  @Inject Repository repository;
  @Inject Persistence persistence;

  @Deployment public static Archive<?> createDeployment() {
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

  @Test public void shouldCreateForum() throws JsonProcessingException {
    ForumEntity expectedForum = createRandomNewForum();
    ForumEntity createdForum = persistence.createForum(expectedForum);

    assertTrue("forum id greater than 0", createdForum.getId() > 0);
    assertNotNull("forum createdTime not null", createdForum.getCreatedTime());
    assertNotNull("forum createdByUid not null", createdForum.getCreatedByUid());
    assertTrue("forum createdByUid not empty", !createdForum.getCreatedByUid().isEmpty());
  }

  @Test public void shouldUpdateForum() throws JsonProcessingException {
    ForumEntity forum = persistence.createForum(createRandomNewForum());
    assertTrue("forum id greater than 0", forum.getId() > 0);

    ForumEntity expectedForum = createRandomNewForum();
    forum.setTitle(expectedForum.getTitle());
    forum.setDescription(expectedForum.getDescription());
    forum.setImageUrl(expectedForum.getImageUrl());

    ForumEntity modifiedForum = persistence.updateForum(forum);

    assertEquals("forum id", forum.getId(), modifiedForum.getId());
    assertEquals("forum title", expectedForum.getTitle(), modifiedForum.getTitle());
    assertEquals("forum description", expectedForum.getDescription(),
        modifiedForum.getDescription());
    assertEquals("forum imageurl", expectedForum.getImageUrl(), modifiedForum.getImageUrl());
    assertNotNull("forum modifiedTime not null", modifiedForum.getCreatedTime());
    assertNotNull("forum modifiedByUid not null", modifiedForum.getCreatedByUid());
    assertTrue("forum modifiedByUid not empty", !modifiedForum.getCreatedByUid().isEmpty());
  }

  private ForumEntity createRandomNewForum() {
    return new ForumEntity(ContainerConfigurationProvider.randomAlphaNumericString(),
        ContainerConfigurationProvider.randomAlphaNumericString(),
        ContainerConfigurationProvider.randomAlphaNumericString());
  }

}
