package de.propra.wuffer2.service;

import de.propra.wuffer2.db.ProfileRepo;
import de.propra.wuffer2.db.WuffRepo;
import de.propra.wuffer2.domain.Profile;
import de.propra.wuffer2.domain.ProfileDetail;
import de.propra.wuffer2.domain.Wuff;
import de.propra.wuffer2.domain.WuffDetail;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class WufferService {

  private final ProfileRepo profiles;
  private final WuffRepo wuffs;

  public WufferService(ProfileRepo profiles, WuffRepo wuffs) {
    this.profiles = profiles;
    this.wuffs = wuffs;
  }

  public void addWuff(OAuth2User user, String text, LocalDateTime timestamp) {
    Integer id = (Integer) user.getAttribute("id");
    String name = (String) user.getAttribute("login");
    String avatar = (String) user.getAttribute("avatar_url");

    Profile profile = profiles.findByProfileId(id).orElseGet(() -> {
      return profiles.save(new Profile(id, name, avatar));
    });

    wuffs.save(new Wuff(text, profile, timestamp));
  }

  public List<Wuff> getWuffs(int offset, int limit) {
    if (limit < 0 || offset > wuffs.count()) {
      return List.of();
    }
    if (offset < 0) {
      offset = 0;
    }
    int toSkip = offset * limit;
    return wuffs.findAllByOrderByCreatedAtDesc().stream()
        .skip(toSkip)
        .limit(limit)
        .toList();
  }


  public Optional<Profile> getProfileById(int id) {
    return profiles.findByProfileId(id);
  }


  public boolean anyWuffsPresent() {
    return wuffs.count() > 0;
  }

  public int getAmountOfPages(int max) {
    return (int) Math.ceil((double) wuffs.count() / max);
  }

  public WuffDetail toDetail(Wuff wuff) {
    Profile profile = profiles.findById(wuff.getProfile().getId()).orElseThrow();
    return new WuffDetail(
        wuff.getText(), profile.getName(),
        profile.getAvatarUrl(), wuff.getCreatedAt()
    );
  }

  public List<WuffDetail> toDetailList(List<Wuff> wuffs) {
    return wuffs.stream().map(this::toDetail).toList();
  }

  public ProfileDetail toDetail(Optional<Profile> maybeProfile) {
    if (maybeProfile.isEmpty()) {
      return null;
    }

    Profile profile = maybeProfile.get();
    String githubUrl = "https://github.com/" + profile.getName();
    return new ProfileDetail(profile.getName(), profile.getAvatarUrl(), profile.getProfileId(),
        githubUrl);
  }

  public ProfileDetail getProfileDetailByName(String name) {
    return toDetail(profiles.findByName(name));
  }


}