package de.propra.wuffer2.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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

  @Test
  @DisplayName("p with Es sind noch keine Wuffs vorhanden is shown if no wuffs are present")
  void noWuffs() throws Exception {
    when(service.anyWuffsPresent()).thenReturn(false);
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("index"))
        .andExpect(
            content().string(containsString("Es sind noch keine Wuffs vorhanden")));
  }

  @Test
  @DisplayName("<a> with Alle Wuffs is shown if model attribute anyWuffsPresent is true")
  void anyWuffs() throws Exception {
    when(service.anyWuffsPresent()).thenReturn(true);
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("index"))
        .andExpect(content().string(containsString("Alle Wuffs")));
  }

  @Test
  @DisplayName("addWuff is called with text if valid form is submitted")
  void addWuff() throws Exception {
    String text = "Hallo";
    mvc.perform(post("/sendWuff").param("text", text))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
    verify(service).addWuff(any(), eq(text), any());
  }


  @Test
  @DisplayName("error message is shown in index and addWuff is not called if invalid form is " +
      "submitted")
  void addWuffInvalidErrorMessage() throws Exception {
    String text = "Hallo".repeat(61);
    mvc.perform(post("/sendWuff").param("text", text))
        .andExpect(view().name("index"))
        .andExpect(
            content().string(containsString("Wufftext muss zwischen 1 und 300 Zeichen lang sein")));
    verify(service, never()).addWuff(any(), any(), any());
  }


}