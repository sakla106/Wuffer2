package de.propra.wuffer2.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WuffController {

  @GetMapping
  public String index() {
    return "index";
  }

}