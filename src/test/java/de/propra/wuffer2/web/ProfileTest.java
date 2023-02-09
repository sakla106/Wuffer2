package de.propra.wuffer2.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import de.propra.wuffer2.config.WebSecurityConfiguration;
import de.propra.wuffer2.domain.ProfileDetail;
import de.propra.wuffer2.helper.WithMockOAuth2User;
import de.propra.wuffer2.service.WufferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import(WebSecurityConfiguration.class)
@WithMockOAuth2User(id = 333, login = "Wuffi")
public class ProfileTest {

  @Autowired
  MockMvc mvc;
  @MockBean
  WufferService service;

  @Test
  @DisplayName("/profile/name shows view profile if profile is present")
  void profileIsPresent() throws Exception {
    String username = "John";
    String avatarUrl = "avatar.url";
    Integer profileId = 333;
    String githubUrl = "github.url";
    ProfileDetail profile = new ProfileDetail(username, avatarUrl, profileId, githubUrl);
    when(service.getProfileDetailByName(any())).thenReturn(profile);

    mvc.perform(get("/profile/" + username))
        .andExpect(status().isOk())
        .andExpect(view().name("profile"))
        .andExpect(model().attribute("profile", profile));
  }

  @Test
  @DisplayName("/profile/name shows 'Es existiert noch kein Profil mit diesem Usernamen' if profile is not present")
  void profileIsNotPresent() throws Exception {
    String username = "John";
    when(service.getProfileDetailByName(any())).thenReturn(null);

    mvc.perform(get("/profile/" + username))
        .andExpect(status().isOk())
        .andExpect(view().name("profile"))
        .andExpect(
            content().string(containsString("Es existiert noch kein Profil mit diesem Usernamen")));
  }
}