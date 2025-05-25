package com.shuttleverse.community.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Club;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.ClubService;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ClubControllerTest {

  @Mock
  private ClubService clubService;

  @InjectMocks
  private ClubController clubController;

  private Club club;
  private UUID clubId;

  @BeforeEach
  void setUp() {
    clubId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    User user = new User();
    user.setId(userId);
    user.setUsername("testuser");

    club = new Club();
    club.setId(clubId);
    club.setName("Test Club");
    club.setOwnerId(user.getId());
  }

  @Test
  void createClub_Success() {
    when(clubService.createClub(any(Club.class))).thenReturn(club);

    ResponseEntity<ApiResponse<Club>> response = clubController.createClub(club);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(club, response.getBody().getData());
    verify(clubService).createClub(any(Club.class));
  }

  @Test
  void getClub_Success() {
    when(clubService.getClub(any(UUID.class))).thenReturn(club);

    ResponseEntity<ApiResponse<Club>> response = clubController.getClub(clubId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(club, response.getBody().getData());
    verify(clubService).getClub(clubId);
  }

  @Test
  void updateClub_Success() {
    when(clubService.updateClub(any(UUID.class), any(Club.class))).thenReturn(club);

    ResponseEntity<ApiResponse<Club>> response = clubController.updateClub(clubId.toString(), club);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(club, response.getBody().getData());
    verify(clubService).updateClub(clubId, club);
  }

  @Test
  void deleteClub_Success() {
    doNothing().when(clubService).deleteClub(any(UUID.class));

    ResponseEntity<ApiResponse<Void>> response = clubController.deleteClub(clubId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    verify(clubService).deleteClub(clubId);
  }
}