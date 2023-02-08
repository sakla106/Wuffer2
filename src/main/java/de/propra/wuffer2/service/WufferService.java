package de.propra.wuffer2.service;

import de.propra.wuffer2.db.ProfileRepo;
import de.propra.wuffer2.db.WuffRepo;
import de.propra.wuffer2.domain.Profile;
import de.propra.wuffer2.domain.Wuff;
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
    if (limit < 0 || offset > limit) {
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
}