package de.propra.wuffer2.service;

import de.propra.wuffer2.db.ProfileRepo;
import de.propra.wuffer2.db.WuffRepo;
import org.springframework.stereotype.Service;

@Service
public class WufferService {

  private final ProfileRepo profiles;
  private final WuffRepo wuffs;

  public WufferService(ProfileRepo profiles, WuffRepo wuffs) {
    this.profiles = profiles;
    this.wuffs = wuffs;
  }


}