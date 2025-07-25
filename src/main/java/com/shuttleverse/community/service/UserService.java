package com.shuttleverse.community.service;

import com.shuttleverse.community.model.User;
import com.shuttleverse.community.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional
  public User createOrUpdateUser(String sub, String email, User userDetails) {
    UUID userId = UUID.nameUUIDFromBytes(sub.getBytes());
    Optional<User> optionalUser = userRepository.findById(userId);
    User user = optionalUser.orElse(new User());

    user.setId(userId);
    user.setEmail(email);
    user.setUsername(userDetails.getUsername());
    return userRepository.save(user);
  }

  @Transactional
  public User save(User user) {
    return userRepository.save(user);
  }

  public Optional<User> findByEmail(String email) {
    User user = userRepository.findByEmail(email);
    return Optional.ofNullable(user);
  }

  @Transactional(readOnly = true)
  public Optional<User> findBySub(String sub) {
    UUID userId = UUID.nameUUIDFromBytes(sub.getBytes());
    return userRepository.findById(userId);
  }
}