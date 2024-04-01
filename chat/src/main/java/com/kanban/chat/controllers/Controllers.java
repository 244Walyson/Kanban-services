package com.kanban.chat.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ok")
public class Controllers {

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  public String ok() {
    return "ok";
  }
}
