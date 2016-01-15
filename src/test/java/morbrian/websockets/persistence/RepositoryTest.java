package morbrian.websockets.persistence;

import morbrian.test.provisioning.ContainerConfigurationProvider;
import morbrian.test.provisioning.VendorSpecificProvisioner;
import morbrian.websockets.model.ForumEntity;
import morbrian.websockets.model.ForumEntityTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class) public class RepositoryTest {

  private static final ContainerConfigurationProvider configProvider =
      new ContainerConfigurationProvider();
  private static List<ForumEntity> sampleData;
  private static Comparator<ForumEntity> ForumCreatedTimeComparator =
      new Comparator<ForumEntity>() {
        @Override public int compare(ForumEntity o1, ForumEntity o2) {
          return o1.getCreatedTime().compareTo(o2.getCreatedTime());
        }
      };
  @Inject Repository repository;
  @Inject Persistence persistence;

  @Deployment public static Archive<?> createDeployment() {
    return configProvider.createDeployment();
  }

  @BeforeClass public static void setupClass() throws Throwable {
    VendorSpecificProvisioner provisioner = configProvider.getVendorSpecificProvisioner();
    provisioner.setup();
  }

  private static ForumEntity createRandomNewForum() {
    return new ForumEntity(ContainerConfigurationProvider.randomAlphaNumericString(),
        ContainerConfigurationProvider.randomAlphaNumericString(),
        ContainerConfigurationProvider.randomAlphaNumericString());
  }

  @Before public void setup() {
    assertNotNull(repository);
    assertNotNull(persistence);
    sampleData = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      sampleData.add(persistence.createForum(createRandomNewForum()));
    }
  }

  @After public void teardown() {
    for (ForumEntity forum : sampleData) {
      persistence.removeForum(forum);
    }
    sampleData = null;
  }

  @Test public void shouldFindForumById() {
    for (int i = 0; i < sampleData.size(); i++) {
      ForumEntity expectedForum = sampleData.get(i);
      ForumEntity foundForum = repository.findForumById(expectedForum.getId());
      ForumEntityTest.verifyEqualityOfAllAttributes("found", expectedForum, foundForum);
    }
  }

  @Test public void shouldFindAllForumsOrderedByCreatedTime() {
    List<ForumEntity> forumList = repository.findAllForumsOrderedByCreatedTime();
    sampleData.sort(ForumCreatedTimeComparator);
    assertEquals("equal list sizes", sampleData.size(), forumList.size());
    for (int i = 0; i < sampleData.size(); i++) {
      ForumEntityTest.verifyEqualityOfAllAttributes("item-" + i, sampleData.get(i), forumList.get(i));
    }
  }

}
