package com.shuttleverse.community.service;

import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.repository.SVUserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SVUserService {

  private final SVUserRepository userRepository;

  @Transactional
  public SVUser createOrUpdateUser(String sub, String email, SVUser userDetails) {
    UUID userId = UUID.nameUUIDFromBytes(sub.getBytes());
    Optional<SVUser> optionalUser = userRepository.findById(userId);
    SVUser user = optionalUser.orElse(new SVUser());

    user.setId(userId);
    user.setEmail(email);
    user.setUsername(userDetails.getUsername());
    return userRepository.save(user);
  }

  @Transactional
  public SVUser save(SVUser user) {
    return userRepository.save(user);
  }

  public Optional<SVUser> findByEmail(String email) {
    SVUser user = userRepository.findByEmail(email);
    return Optional.ofNullable(user);
  }

  @Transactional(readOnly = true)
  public Optional<SVUser> findBySub(String sub) {
    UUID userId = UUID.nameUUIDFromBytes(sub.getBytes());
    return userRepository.findById(userId);
  }
}