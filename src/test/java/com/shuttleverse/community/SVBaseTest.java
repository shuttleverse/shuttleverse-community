package com.shuttleverse.community;

import static org.mockito.Mockito.mockStatic;

import com.shuttleverse.community.model.User;
import com.shuttleverse.community.util.AuthenticationUtils;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;

public abstract class SVBaseTest {

  private MockedStatic<AuthenticationUtils> mockedAuthUtils;

  protected User user;

  @BeforeEach
  void setUpAuthMock() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("testuser");
    user.setId(UUID.randomUUID());

    mockedAuthUtils = mockStatic(AuthenticationUtils.class);
    mockedAuthUtils.when(AuthenticationUtils::getCurrentUser).thenReturn(user);
  }

  @AfterEach
  void tearDownAuthMock() {
    mockedAuthUtils.close();
  }
}
