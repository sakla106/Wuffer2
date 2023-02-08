package de.propra.wuffer2.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.propra.wuffer2.db.ProfileRepo;
import de.propra.wuffer2.db.WuffRepo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@DataJdbcTest
class WufferServiceTest {

  @Autowired
  WuffRepo wuffs;

  @Autowired
  ProfileRepo profiles;

  @Test
  @DisplayName("Service saves new profile")
  void savesProfile() {
    WufferService service = new WufferService(profiles, wuffs);
    int id = 33;
    String name = "Test";
    String text = "Hallo, das ist ein Test";
    assertThat(profiles.count()).isEqualTo(0);
    service.addWuff(mkUser(id, name), text, getTime(1));
    assertThat(profiles.count()).isEqualTo(1);
    assertThat(profiles.findByProfileId(id)).isPresent();
  }

  private DefaultOAuth2User mkUser(Integer id, String name) {
    return new DefaultOAuth2User(
        List.of(),
        Map.of("id", id,
            "login", name),
        "id");
  }

  private LocalDateTime getTime(int minute) {
    return LocalDateTime.of(2023, 1, 1, 1, minute);
  }

  private void saveMultipleWuffs(WufferService service, int count) {
    for (int i = 0; i < count; i++) {
      service.addWuff(mkUser(33, "Test"), "Hallo, das ist ein Test", getTime(i));
    }
  }

  @Test
  @DisplayName("Service saves new wuff")
  void canSaveWuff() {
    WufferService service = new WufferService(profiles, wuffs);
    int id = 33;
    String name = "Test";
    String text = "Hallo, das ist ein Test";
    service.addWuff(mkUser(id, name), text, getTime(1));
    System.out.println(wuffs.findAll());
    assertThat(wuffs.count()).isEqualTo(1);
  }

  @Test
  @DisplayName("Service doesnt save duplicate profile")
  void doesntSaveDuplicateProfile() {
    WufferService service = new WufferService(profiles, wuffs);
    int id = 33;
    String name = "Test";
    String text = "Hallo, das ist ein Test";
    service.addWuff(mkUser(id, name), text, getTime(1));
    service.addWuff(mkUser(id, name), text, getTime(2));
    assertThat(profiles.count()).isEqualTo(1);
  }

  @Test
  @DisplayName("Service saves duplicate wuff")
  void savesDuplicateWuff() {
    WufferService service = new WufferService(profiles, wuffs);
    int id = 33;
    String name = "Test";
    String text = "Hallo, das ist ein Test";
    service.addWuff(mkUser(id, name), text, getTime(1));
    service.addWuff(mkUser(id, name), text, getTime(1));
    assertThat(wuffs.count()).isEqualTo(2);
  }

  @Test
  @DisplayName("getWuffs with negative offset is corrected to 0")
  void getWuffsNegativeOffset() {
    WufferService service = new WufferService(profiles, wuffs);
    saveMultipleWuffs(service, 5);
    assertThat(service.getWuffs(-1, 2)).hasSize(2);

  }

  @Test
  @DisplayName("getWuffs with negative limit returns empty list")
  void getWuffsNegativeLimit() {
    WufferService service = new WufferService(profiles, wuffs);
    saveMultipleWuffs(service, 5);
    assertThat(service.getWuffs(0, -1)).isEmpty();
  }

  @Test
  @DisplayName("getWuffs with offset > limit returns empty list")
  void getWuffsOffsetGreaterThanLimit() {
    WufferService service = new WufferService(profiles, wuffs);
    saveMultipleWuffs(service, 5);
    assertThat(service.getWuffs(2, 1)).isEmpty();
  }

  @Test
  @DisplayName("getWuffs with offset > wuff count returns empty list")
  void getWuffsOffsetGreaterThanWuffCount() {
    WufferService service = new WufferService(profiles, wuffs);
    saveMultipleWuffs(service, 5);
    assertThat(service.getWuffs(6, 1)).isEmpty();
  }

  @Test
  @DisplayName("getWuffs(1,3) with 5 wuffs returns 2 wuffs")
  void getWuffsOffset1Limit3() {
    WufferService service = new WufferService(profiles, wuffs);
    saveMultipleWuffs(service, 5);
    assertThat(service.getWuffs(1, 3)).hasSize(2);
  }

  @Test
  @DisplayName("getWuffs(0,3) with 5 wuffs returns 3 wuffs")
  void getWuffsOffset0Limit3() {
    WufferService service = new WufferService(profiles, wuffs);
    saveMultipleWuffs(service, 5);
    assertThat(service.getWuffs(0, 3)).hasSize(3);
  }

  @Test
  @DisplayName("getWuffs returns wuffs in correct order")
  void getWuffsOrder() {
    WufferService service = new WufferService(profiles, wuffs);
    saveMultipleWuffs(service, 5);
    assertThat(service.getWuffs(0, 5)).isSortedAccordingTo(
        (w1, w2) -> w2.getCreatedAt().compareTo(w1.getCreatedAt()));
  }

  @Test
  @DisplayName("getWuffs(1,3) with 5 wuffs returns 2 wuffs in correct order")
  void getWuffsOffset1Limit3Order() {
    WufferService service = new WufferService(profiles, wuffs);
    saveMultipleWuffs(service, 5);
    System.out.println(service.getWuffs(1, 3));
    assertThat(service.getWuffs(1, 3)).isSortedAccordingTo(
        (w1, w2) -> w2.getCreatedAt().compareTo(w1.getCreatedAt()));
  }

  @Test
  @DisplayName("getProfileById(profileId) returns profile")
  void getProfileById() {
    WufferService service = new WufferService(profiles, wuffs);
    int id = 33;
    String name = "Test";
    String text = "Hallo, das ist ein Test";
    service.addWuff(mkUser(id, name), text, getTime(1));
    assertThat(service.getProfileById(id)).isPresent();
  }

}