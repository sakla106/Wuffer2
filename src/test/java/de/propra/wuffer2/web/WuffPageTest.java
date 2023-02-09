package de.propra.wuffer2.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import de.propra.wuffer2.config.WebSecurityConfiguration;
import de.propra.wuffer2.domain.WuffDetail;
import de.propra.wuffer2.helper.WithMockOAuth2User;
import de.propra.wuffer2.service.WufferService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class WuffPageTest {

  @Autowired
  MockMvc mvc;

  @MockBean
  WufferService service;

  @Test
  @DisplayName("/wuffs/1 shows view wuffs if wuffs are present")
  void page1() throws Exception {
    when(service.toDetailList(any())).thenReturn(createListWithWuffDetails(2));

    when(service.getAmountOfPages(anyInt())).thenReturn(1);

    mvc.perform(get("/wuffs/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("wuffs"));
  }

  private List<WuffDetail> createListWithWuffDetails(int amount) {
    List<WuffDetail> wuffs = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      wuffs.add(
          new WuffDetail(
              "text text " + i,
              "Wuffi",
              "avatar.url",
              LocalDateTime.of(2023, 1, 1, 1, 1, i)
          )
      );

    }
    return wuffs;
  }

  @Test
  @DisplayName("/wuffs/0 redirects to /wuffs/1")
  void page0() throws Exception {
    when(service.toDetailList(any())).thenReturn(createListWithWuffDetails(2));

    when(service.getAmountOfPages(anyInt())).thenReturn(1);

    when(service.anyWuffsPresent()).thenReturn(true);

    mvc.perform(get("/wuffs/0"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/wuffs/1"));
  }

  @Test
  @DisplayName("/wuffs/2 redirects to /wuffs/1 if getAmountOfPages returns 1")
  void page2() throws Exception {
    when(service.toDetailList(any())).thenReturn(createListWithWuffDetails(2));

    when(service.getAmountOfPages(anyInt())).thenReturn(1);

    when(service.anyWuffsPresent()).thenReturn(true);


    mvc.perform(get("/wuffs/2"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/wuffs/1"));
  }

  @Test
  @DisplayName("/wuffs/1 model contains the wuff details")
  void page1ContainsWuffDetails() throws Exception {
    WuffDetail wuff1 = new WuffDetail("Hallo", "Wuffi", "url", getTime(1));
    WuffDetail wuff2 = new WuffDetail("Wuff", "Thomas", "url", getTime(2));

    when(service.toDetailList(any())).thenReturn(List.of(wuff1, wuff2));

    when(service.getAmountOfPages(anyInt())).thenReturn(1);

    when(service.anyWuffsPresent()).thenReturn(true);

    mvc.perform(get("/wuffs/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("wuffs"))
        .andExpect(model().attribute("wuffs", List.of(wuff1, wuff2)));
  }

  private LocalDateTime getTime(int i) {
    return LocalDateTime.of(2023, 1, 1, 1, 1, i);
  }

  @Test
  @DisplayName("/wuffs/any contains no wuffs text if anyWuffsPresent returns false")
  void page1ContainsBackButton() throws Exception {
    when(service.toDetailList(any())).thenReturn(List.of());

    when(service.getAmountOfPages(anyInt())).thenReturn(1);

    when(service.anyWuffsPresent()).thenReturn(false);

    mvc.perform(get("/wuffs/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("wuffs"))
        .andExpect(content().string(containsString("Es gibt noch keine Wuffs")));
  }
}