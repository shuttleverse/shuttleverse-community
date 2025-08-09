package com.shuttleverse.community.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.api.SVApiResponse;
import com.shuttleverse.community.model.SVClub;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.service.SVClubService;
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
class SVClubControllerTest {

  @Mock
  private SVClubService clubService;

  @InjectMocks
  private SVClubController clubController;

  private SVClub club;
  private UUID clubId;

  @BeforeEach
  void setUp() {
    clubId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    SVUser user = new SVUser();
    user.setId(userId);
    user.setUsername("testuser");

    club = new SVClub();
    club.setId(clubId);
    club.setName("Test Club");
    club.setOwnerId(user.getId());
  }

  @Test
  void createClub_Success() {
    when(clubService.createClub(any(SVClub.class))).thenReturn(club);

    ResponseEntity<SVApiResponse<SVClub>> response = clubController.createClub(club);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(club, response.getBody().getData());
    verify(clubService).createClub(any(SVClub.class));
  }

  @Test
  void getClub_Success() {
    when(clubService.getClub(any(UUID.class))).thenReturn(club);

    ResponseEntity<SVApiResponse<SVClub>> response = clubController.getClub(clubId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(club, response.getBody().getData());
    verify(clubService).getClub(clubId);
  }

  @Test
  void updateClub_Success() {
    when(clubService.updateClub(any(UUID.class), any(SVClub.class))).thenReturn(club);

    ResponseEntity<SVApiResponse<SVClub>> response = clubController.updateClub(clubId.toString(),
        club);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(club, response.getBody().getData());
    verify(clubService).updateClub(clubId, club);
  }

  @Test
  void deleteClub_Success() {
    doNothing().when(clubService).deleteClub(any(UUID.class));

    ResponseEntity<SVApiResponse<Void>> response = clubController.deleteClub(clubId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    verify(clubService).deleteClub(clubId);
  }
}