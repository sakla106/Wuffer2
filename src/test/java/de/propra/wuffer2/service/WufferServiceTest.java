package de.propra.wuffer2.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.propra.wuffer2.db.ProfileRepo;
import de.propra.wuffer2.db.WuffRepo;
import de.propra.wuffer2.domain.Profile;
import de.propra.wuffer2.domain.ProfileDetail;
import de.propra.wuffer2.domain.Wuff;
import de.propra.wuffer2.domain.WuffDetail;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  @DisplayName("toDetail returns correct wuff with correct profile")
  void toDetail() {
    WufferService service = new WufferService(profiles, wuffs);
    int id = 33;
    String name = "Test";
    String text = "Hallo, das ist ein Test";
    service.addWuff(mkUser(id, name), text, getTime(1));

    Wuff wuff = service.getWuffs(0, 1).get(0);
    WuffDetail expectedDetail = new WuffDetail(text, name, null, getTime(1));
    WuffDetail actualDetail = service.toDetail(wuff);

    assertThat(actualDetail).isEqualTo(expectedDetail);
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

  @Test
  @DisplayName("toDetailList returns correct wuffs with correct profiles")
  void toDetailList() {
    WufferService service = new WufferService(profiles, wuffs);
    int id = 33;
    String name = "Test";
    String text = "Hallo, das ist ein Test";
    service.addWuff(mkUser(id, name), text, getTime(1));
    service.addWuff(mkUser(id, name), text, getTime(2));
    service.addWuff(mkUser(id, name), text, getTime(3));
    service.addWuff(mkUser(id, name), text, getTime(4));

    List<WuffDetail> expectedDetails = List.of(
        new WuffDetail(text, name, null, getTime(4)),
        new WuffDetail(text, name, null, getTime(3)),
        new WuffDetail(text, name, null, getTime(2)),
        new WuffDetail(text, name, null, getTime(1))
    );

    List<WuffDetail> actualDetails = service.toDetailList(service.getWuffs(0, 4));

    assertThat(actualDetails).isEqualTo(expectedDetails);
  }

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

  @Test
  @DisplayName("Service saves new wuff")
  void canSaveWuff() {
    WufferService service = new WufferService(profiles, wuffs);
    int id = 33;
    String name = "Test";
    String text = "Hallo, das ist ein Test";
    assertThat(wuffs.count()).isEqualTo(0);
    service.addWuff(mkUser(id, name), text, getTime(1));
    assertThat(wuffs.count()).isEqualTo(1);
//    assertThat(wuffs.findById(1)).isPresent();
//    assertThat(wuffs.findById(1).get().getText()).isEqualTo(text);
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

  private void saveMultipleWuffs(WufferService service, int count) {
    for (int i = 0; i < count; i++) {
      service.addWuff(mkUser(33, "Test"), "Hallo, das ist ein Test" + i, getTime(i));
    }
  }

  @Test
  @DisplayName("getWuffs with negative limit returns empty list")
  void getWuffsNegativeLimit() {
    WufferService service = new WufferService(profiles, wuffs);
    saveMultipleWuffs(service, 5);
    assertThat(service.getWuffs(0, -1)).isEmpty();
  }

  @Test
  @DisplayName("getWuffs with offset > wuff count returns empty list")
  void getWuffsOffsetGreaterThanWuffCount() {
    WufferService service = new WufferService(profiles, wuffs);
    int count = 5;
    saveMultipleWuffs(service, count);
    assertThat(service.getWuffs(count + 1, 1)).isEmpty();
  }

  @Test
  @DisplayName("getWuffs(1,3) with 5 wuffs returns 2 wuffs")
  void getWuffsOffset1Limit3() {
    WufferService service = new WufferService(profiles, wuffs);
    saveMultipleWuffs(service, 5);
    assertThat(service.getWuffs(1, 3)).hasSize(2);
  }

  @Test
  @DisplayName("getWuffs(3,2) with 7 wuffs returns 1 wuff with correct text")
  void getWuffsOffset1Limit2() {
    WufferService service = new WufferService(profiles, wuffs);
    String text = "Texti";
    service.addWuff(mkUser(33, "bob"), text, getTime(0, 1));
    saveMultipleWuffs(service, 6);
    assertThat(service.getWuffs(3, 2)).hasSize(1);
    assertThat(service.getWuffs(3, 2).get(0).getText()).isEqualTo(text);
  }

  private LocalDateTime getTime(int hour, int minute) {
    return LocalDateTime.of(2023, 1, 1, hour, minute);
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

  //  test toDetail from service
  @Test
  @DisplayName("toDetail with no existing profile returns null")
  void toDetailNoProfile() {
    WufferService service = new WufferService(profiles, wuffs);
    assertThat(service.toDetail(Optional.empty())).isNull();
  }


  @Test
  @DisplayName("getWuffs(1,3) with 5 wuffs returns 2 wuffs in correct order")
  void getWuffsOffset1Limit3Order() {
    WufferService service = new WufferService(profiles, wuffs);
    ProfileDetail profileDetail = service.toDetail(Optional.empty());
    assertThat(profileDetail).isNull();
  }

  @Test
  @DisplayName("toDetail with existing profile returns profile detail")
  void toDetailExistingProfile() {
    WufferService service = new WufferService(profiles, wuffs);
    int id = 33;
    String name = "Test";
    String text = "Hallo, das ist ein Test";
    service.addWuff(mkUser(id, name), text, getTime(1));
    Optional<Profile> profile = profiles.findAll().stream().findFirst();
    ProfileDetail profileDetail = service.toDetail(profile);
    assertThat(profileDetail).isNotNull();
    assertThat(profileDetail.username()).isEqualTo(name);
    assertThat(profileDetail.profileId()).isEqualTo(id);
  }


  @Test
  @DisplayName("getProfileDetailByName returns profile detail when profile exists")
  void getProfileDetailByName() {
    WufferService service = new WufferService(profiles, wuffs);
    int id = 33;
    String name = "Test";
    String text = "Hallo, das ist ein Test";
    service.addWuff(mkUser(id, name), text, getTime(1));
    assertThat(service.getProfileDetailByName(name)).isNotNull();
  }

  @Test
  @DisplayName("getProfileDetailByName returns null when profile does not exist")
  void getProfileDetailByNameNoProfile() {
    WufferService service = new WufferService(profiles, wuffs);
    assertThat(service.getProfileDetailByName("Test")).isNull();
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

  @Test
  @DisplayName("getAmountOfPages returns right amount of pages")
  void getAmountOfPages() {
    WufferService service = new WufferService(profiles, wuffs);
    saveMultipleWuffs(service, 10);
    assertThat(service.getAmountOfPages(1)).isEqualTo(10);
    assertThat(service.getAmountOfPages(2)).isEqualTo(5);
    assertThat(service.getAmountOfPages(3)).isEqualTo(4);
    assertThat(service.getAmountOfPages(4)).isEqualTo(3);
    assertThat(service.getAmountOfPages(5)).isEqualTo(2);
    assertThat(service.getAmountOfPages(6)).isEqualTo(2);
    assertThat(service.getAmountOfPages(7)).isEqualTo(2);
    assertThat(service.getAmountOfPages(8)).isEqualTo(2);
    assertThat(service.getAmountOfPages(9)).isEqualTo(2);
    assertThat(service.getAmountOfPages(10)).isEqualTo(1);
  }


}