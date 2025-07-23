package com.shuttleverse.community.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.constants.BadmintonEntityType;
import com.shuttleverse.community.constants.BadmintonInfoType;
import com.shuttleverse.community.dto.BadmintonEntityDto;
import com.shuttleverse.community.dto.UpvoteResponse;
import com.shuttleverse.community.dto.UserResponse;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.UpvoteService;
import com.shuttleverse.community.util.AuthenticationUtils;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UpvoteControllerTest {

  @Mock
  private UpvoteService upvoteService;

  @InjectMocks
  private UpvoteController upvoteController;

  private UpvoteResponse upvoteResponse;
  private Page<UpvoteResponse> upvotePage;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("testuser");

    UUID userId = UUID.randomUUID();
    UUID upvoteId = UUID.randomUUID();
    UUID entityId = UUID.randomUUID();

    UserResponse userResponse = new UserResponse();
    userResponse.setId(userId);
    userResponse.setUsername("testuser");

    BadmintonEntityDto entityDto = new BadmintonEntityDto() {
    };
    entityDto.setId(entityId);
    entityDto.setName("Test Entity");

    upvoteResponse = UpvoteResponse.builder()
        .upvoteId(upvoteId)
        .upvoteCreator(userResponse)
        .entityType(BadmintonEntityType.COURT)
        .infoType(BadmintonInfoType.SCHEDULE)
        .entity(entityDto)
        .createdAt(ZonedDateTime.now())
        .build();

    upvotePage = new PageImpl<>(List.of(upvoteResponse));
  }

  @Test
  void getAllUpvotes_Success() {
    Map<String, String> params = new HashMap<>();
    params.put("entityType", "COURT");
    params.put("infoType", "SCHEDULE");

    try (MockedStatic<AuthenticationUtils> mockedAuth = org.mockito.Mockito.mockStatic(
        AuthenticationUtils.class)) {
      mockedAuth.when(AuthenticationUtils::getCurrentUser).thenReturn(user);

      when(upvoteService.getAllUpvotes(anyMap(), any(Pageable.class)))
          .thenReturn(upvotePage);

      ResponseEntity<ApiResponse<Page<UpvoteResponse>>> response = upvoteController.getAllUpvotes(0,
          50, params);

      assertNotNull(response);
      assertTrue(response.getStatusCode().is2xxSuccessful());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().isSuccess());
      assertEquals(upvotePage, response.getBody().getData());
      assertEquals(1, response.getBody().getData().getContent().size());
      assertEquals(upvoteResponse, response.getBody().getData().getContent().get(0));

      Map<String, String> expectedFilters = new HashMap<>(params);
      expectedFilters.put("userId", user.getId().toString());
      verify(upvoteService).getAllUpvotes(eq(expectedFilters), any(Pageable.class));
    }
  }

  @Test
  void getAllUpvotes_WithCustomPagination() {
    Map<String, String> params = new HashMap<>();
    params.put("entityType", "STRINGER");
    params.put("infoType", "PRICE");

    try (MockedStatic<AuthenticationUtils> mockedAuth = org.mockito.Mockito.mockStatic(
        AuthenticationUtils.class)) {
      mockedAuth.when(AuthenticationUtils::getCurrentUser).thenReturn(user);

      when(upvoteService.getAllUpvotes(anyMap(), any(Pageable.class)))
          .thenReturn(upvotePage);

      ResponseEntity<ApiResponse<Page<UpvoteResponse>>> response = upvoteController.getAllUpvotes(2,
          10, params);

      assertNotNull(response);
      assertTrue(response.getStatusCode().is2xxSuccessful());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().isSuccess());

      Pageable expectedPageable = PageRequest.of(2, 10);
      Map<String, String> expectedFilters = new HashMap<>(params);
      expectedFilters.put("userId", user.getId().toString());
      verify(upvoteService).getAllUpvotes(eq(expectedFilters), eq(expectedPageable));
    }
  }

  @Test
  void getAllUpvotes_WithAdditionalFilters() {
    Map<String, String> params = new HashMap<>();
    params.put("entityType", "COACH");
    params.put("infoType", "SCHEDULE");
    params.put("additional_filter", "some_value");

    try (MockedStatic<AuthenticationUtils> mockedAuth = org.mockito.Mockito.mockStatic(
        AuthenticationUtils.class)) {
      mockedAuth.when(AuthenticationUtils::getCurrentUser).thenReturn(user);

      when(upvoteService.getAllUpvotes(anyMap(), any(Pageable.class)))
          .thenReturn(upvotePage);

      ResponseEntity<ApiResponse<Page<UpvoteResponse>>> response = upvoteController.getAllUpvotes(0,
          50, params);

      assertNotNull(response);
      assertTrue(response.getStatusCode().is2xxSuccessful());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().isSuccess());

      Map<String, String> expectedFilters = new HashMap<>(params);
      expectedFilters.put("userId", user.getId().toString());
      verify(upvoteService).getAllUpvotes(eq(expectedFilters), any(Pageable.class));
    }
  }

  @Test
  void getAllUpvotes_WithPageAndSizeInParams() {
    Map<String, String> params = new HashMap<>();
    params.put("entityType", "COURT");
    params.put("infoType", "SCHEDULE");
    params.put("page", "5");
    params.put("size", "25");

    try (MockedStatic<AuthenticationUtils> mockedAuth = org.mockito.Mockito.mockStatic(
        AuthenticationUtils.class)) {
      mockedAuth.when(AuthenticationUtils::getCurrentUser).thenReturn(user);

      when(upvoteService.getAllUpvotes(anyMap(), any(Pageable.class)))
          .thenReturn(upvotePage);

      ResponseEntity<ApiResponse<Page<UpvoteResponse>>> response = upvoteController.getAllUpvotes(3,
          15, params);

      assertNotNull(response);
      assertTrue(response.getStatusCode().is2xxSuccessful());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().isSuccess());

      Pageable expectedPageable = PageRequest.of(3, 15);
      Map<String, String> expectedFilters = new HashMap<>(params);
      expectedFilters.put("userId", user.getId().toString());
      expectedFilters.remove("page");
      expectedFilters.remove("size");
      verify(upvoteService).getAllUpvotes(eq(expectedFilters), eq(expectedPageable));
    }
  }

  @Test
  void getAllUpvotes_EmptyResult() {
    Map<String, String> params = new HashMap<>();
    params.put("entityType", "COURT");
    params.put("infoType", "SCHEDULE");

    Page<UpvoteResponse> emptyPage = new PageImpl<>(List.of());

    try (MockedStatic<AuthenticationUtils> mockedAuth = org.mockito.Mockito.mockStatic(
        AuthenticationUtils.class)) {
      mockedAuth.when(AuthenticationUtils::getCurrentUser).thenReturn(user);

      when(upvoteService.getAllUpvotes(anyMap(), any(Pageable.class)))
          .thenReturn(emptyPage);

      ResponseEntity<ApiResponse<Page<UpvoteResponse>>> response = upvoteController.getAllUpvotes(0,
          50, params);

      assertNotNull(response);
      assertTrue(response.getStatusCode().is2xxSuccessful());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().isSuccess());
      assertEquals(emptyPage, response.getBody().getData());
      assertEquals(0, response.getBody().getData().getContent().size());
    }
  }
}