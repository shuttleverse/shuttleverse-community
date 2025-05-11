package com.shuttleverse.community.service;

import com.shuttleverse.community.model.User;
import com.shuttleverse.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional
  public User findOrCreateUser(String sub, String email, String username) {
    User user = userRepository.findBySub(sub);
    if (user == null) {
      user = new User();
      user.setSub(sub);
      user.setEmail(email);
      user.setUsername(username);
      user.setAdmin(false);
      return userRepository.save(user);
    }

    // Update email if it has changed
    if (!email.equals(user.getEmail())) {
      user.setEmail(email);
      return userRepository.save(user);
    }

    return user;
  }

  @Transactional(readOnly = true)
  public User getBySub(String sub) {
    return userRepository.findBySub(sub);
  }

  @Transactional
  public User save(User user) {
    return userRepository.save(user);
  }
}