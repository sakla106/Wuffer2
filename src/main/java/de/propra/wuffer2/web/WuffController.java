package de.propra.wuffer2.web;

import de.propra.wuffer2.domain.ProfileDetail;
import de.propra.wuffer2.domain.WuffDetail;
import de.propra.wuffer2.service.WufferService;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WuffController {


  private final WufferService service;

  public WuffController(WufferService service) {
    this.service = service;
  }

  @GetMapping
  public String index(Model model) {
    model.addAttribute("anyWuffsPresent", service.anyWuffsPresent());
    return "index";
  }

  @PostMapping("/sendWuff")
  public String sendWuff(@Valid WuffText wuffText, OAuth2AuthenticationToken authenticationToken,
                         RedirectAttributes attrs) {
    System.out.println("WuffController.sendWuff: " + wuffText.text());
    service.addWuff(authenticationToken.getPrincipal(), wuffText.text(), LocalDateTime.now());
    return "redirect:/";
  }

  @GetMapping("/wuffs/{page}")
  public String wuffs(Model model, @PathVariable int page, @ModelAttribute
  @RequestParam(name = "max", required = false, defaultValue = "5") int max) {
    boolean anyWuffsPresent = service.anyWuffsPresent();
    model.addAttribute("anyWuffsPresent", anyWuffsPresent);

    if (!anyWuffsPresent) {
      return "wuffs";
    }

    int totalPages = service.getAmountOfPages(max);
    model.addAttribute("max", max);

    if (page < 1) {
      return "redirect:/wuffs/1";
    }
    if (page > totalPages) {
      return "redirect:/wuffs/" + (totalPages);
    }

    List<WuffDetail> wuffs = service.toDetailList(service.getWuffs(page - 1, max));


    model.addAttribute("wuffs", wuffs);
    model.addAttribute("pages", totalPages);
    model.addAttribute("page", page);

    return "wuffs";
  }


  @GetMapping("/profile/{name}")
  public String profile(Model model, @PathVariable String name) {
    ProfileDetail profile = service.getProfileDetailByName(name);
    model.addAttribute("profile", profile);
    return "profile";
  }


  @ExceptionHandler(BindException.class)
  @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
  public String handleContraintViolation(BindException e, Model model) {
    model.addAttribute("anyWuffsPresent", service.anyWuffsPresent());
    model.addAttribute("wuffText", e.getBindingResult().getFieldValue("text"));
    model.addAttribute("error", e.getBindingResult().getFieldError().getDefaultMessage());
    return "index";
  }

}