package de.propra.wuffer2.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import de.propra.wuffer2.config.WebSecurityConfiguration;
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
class WuffControllerTest {

  @Autowired
  MockMvc mvc;
  @MockBean
  WufferService service;

  @Test
  @DisplayName("Controller returns index")
  void index() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("index"));
  }

}