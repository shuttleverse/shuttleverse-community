package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Club;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.ClubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ClubControllerTest {

    @Mock
    private ClubService clubService;

    @InjectMocks
    private ClubController clubController;

    private Club club;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        club = new Club();
        club.setId(1L);
        club.setName("Test Club");
        club.setOwnerId(user.getId());
    }

    @Test
    void createClub_Success() {
        when(clubService.createClub(any(Club.class))).thenReturn(club);

        ResponseEntity<ApiResponse<Club>> response = clubController.createClub(club);

        assertTrue(response.getBody().isSuccess());
        assertEquals(club, response.getBody().getData());
        verify(clubService).createClub(any(Club.class));
    }

    @Test
    void getClub_Success() {
        when(clubService.getClub(anyLong())).thenReturn(club);

        ResponseEntity<ApiResponse<Club>> response = clubController.getClub(1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals(club, response.getBody().getData());
        verify(clubService).getClub(1L);
    }

    @Test
    void updateClub_Success() {
        when(clubService.updateClub(anyLong(), any(Club.class))).thenReturn(club);

        ResponseEntity<ApiResponse<Club>> response = clubController.updateClub(1L, club);

        assertTrue(response.getBody().isSuccess());
        assertEquals(club, response.getBody().getData());
        verify(clubService).updateClub(1L, club);
    }

    @Test
    void deleteClub_Success() {
        doNothing().when(clubService).deleteClub(anyLong());

        ResponseEntity<ApiResponse<Void>> response = clubController.deleteClub(1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals("Club deleted successfully", response.getBody().getMessage());
        verify(clubService).deleteClub(1L);
    }
}