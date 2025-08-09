package com.shuttleverse.community;

import static org.mockito.Mockito.mockStatic;

import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.util.SVAuthenticationUtils;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;

public abstract class SVBaseTest {

  private MockedStatic<SVAuthenticationUtils> mockedAuthUtils;

  protected SVUser user;

  @BeforeEach
  void setUpAuthMock() {
    user = new SVUser();
    user.setId(UUID.randomUUID());
    user.setUsername("testuser");
    user.setId(UUID.randomUUID());

    mockedAuthUtils = mockStatic(SVAuthenticationUtils.class);
    mockedAuthUtils.when(SVAuthenticationUtils::getCurrentUser).thenReturn(user);
  }

  @AfterEach
  void tearDownAuthMock() {
    mockedAuthUtils.close();
  }
}
