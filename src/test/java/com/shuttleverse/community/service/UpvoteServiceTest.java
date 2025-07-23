package com.shuttleverse.community.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.SVBaseTest;
import com.shuttleverse.community.constants.BadmintonEntityType;
import com.shuttleverse.community.constants.BadmintonInfoType;
import com.shuttleverse.community.dto.CourtResponse;
import com.shuttleverse.community.dto.UpvoteResponse;
import com.shuttleverse.community.dto.UserResponse;
import com.shuttleverse.community.mapper.MapStructMapper;
import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.Upvote;
import com.shuttleverse.community.repository.CoachPriceRepository;
import com.shuttleverse.community.repository.CoachScheduleRepository;
import com.shuttleverse.community.repository.CourtPriceRepository;
import com.shuttleverse.community.repository.CourtScheduleRepository;
import com.shuttleverse.community.repository.StringerPriceRepository;
import com.shuttleverse.community.repository.UpvoteRepository;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UpvoteServiceTest extends SVBaseTest {

  @Mock
  private UpvoteRepository upvoteRepository;

  @Mock
  private CourtPriceRepository courtPriceRepository;

  @Mock
  private CourtScheduleRepository courtScheduleRepository;

  @Mock
  private CoachScheduleRepository coachScheduleRepository;

  @Mock
  private CoachPriceRepository coachPriceRepository;

  @Mock
  private StringerPriceRepository stringerPriceRepository;

  @Mock
  private MapStructMapper mapper;

  @InjectMocks
  private UpvoteService upvoteService;

  private Upvote testUpvote;
  private Court testCourt;
  private CourtResponse testCourtResponse;
  private UserResponse testUserResponse;
  private UUID upvoteId;
  private UUID entityId;

  @BeforeEach
  void setUp() {
    upvoteId = UUID.randomUUID();
    entityId = UUID.randomUUID();

    testUpvote = new Upvote();
    testUpvote.setUpvoteId(upvoteId);
    testUpvote.setEntityType(BadmintonEntityType.COURT);
    testUpvote.setInfoType(BadmintonInfoType.SCHEDULE);
    testUpvote.setEntityId(entityId);
    testUpvote.setUpvoteCreator(user);
    testUpvote.setCreatedAt(ZonedDateTime.now());

    testCourt = new Court();
    testCourt.setId(entityId);
    testCourt.setName("Test Court");

    testUserResponse = new UserResponse();
    testUserResponse.setId(user.getId());
    testUserResponse.setUsername(user.getUsername());

    testCourtResponse = new CourtResponse();
    testCourtResponse.setId(entityId);
    testCourtResponse.setName("Test Court");
  }

  @Test
  void addUpvote_Success() {
    when(upvoteRepository.findByUpvoteCreatorAndEntityId(user, entityId)).thenReturn(
        Optional.empty());
    when(upvoteRepository.save(any(Upvote.class))).thenReturn(testUpvote);

    upvoteService.addUpvote(BadmintonEntityType.COURT, BadmintonInfoType.SCHEDULE, entityId,
        user);

    ArgumentCaptor<Upvote> upvoteCaptor = ArgumentCaptor.forClass(Upvote.class);
    verify(upvoteRepository).save(upvoteCaptor.capture());

    Upvote savedUpvote = upvoteCaptor.getValue();
    assertEquals(BadmintonEntityType.COURT, savedUpvote.getEntityType());
    assertEquals(BadmintonInfoType.SCHEDULE, savedUpvote.getInfoType());
    assertEquals(entityId, savedUpvote.getEntityId());
    assertEquals(user, savedUpvote.getUpvoteCreator());
    verify(upvoteRepository).findByUpvoteCreatorAndEntityId(user, entityId);
  }

  @Test
  void addUpvote_AlreadyExists_ThrowsException() {
    when(upvoteRepository.findByUpvoteCreatorAndEntityId(user, entityId)).thenReturn(
        Optional.of(testUpvote));

    assertThrows(IllegalStateException.class,
        () -> upvoteService.addUpvote(BadmintonEntityType.COURT, BadmintonInfoType.SCHEDULE,
            entityId, user));
    verify(upvoteRepository).findByUpvoteCreatorAndEntityId(user, entityId);
    verify(upvoteRepository, times(0)).save(any(Upvote.class));
  }

  @Test
  void addUpvote_WithDifferentEntityTypes() {
    BadmintonEntityType[] entityTypes = {BadmintonEntityType.COURT, BadmintonEntityType.STRINGER,
        BadmintonEntityType.COACH};

    for (BadmintonEntityType entityType : entityTypes) {
      when(upvoteRepository.findByUpvoteCreatorAndEntityId(user, entityId)).thenReturn(
          Optional.empty());
      when(upvoteRepository.save(any(Upvote.class))).thenReturn(testUpvote);

      upvoteService.addUpvote(entityType, BadmintonInfoType.SCHEDULE, entityId, user);

      ArgumentCaptor<Upvote> upvoteCaptor = ArgumentCaptor.forClass(Upvote.class);
      verify(upvoteRepository).save(upvoteCaptor.capture());

      Upvote savedUpvote = upvoteCaptor.getValue();
      assertEquals(entityType, savedUpvote.getEntityType());
      assertEquals(BadmintonInfoType.SCHEDULE, savedUpvote.getInfoType());
      assertEquals(entityId, savedUpvote.getEntityId());
      assertEquals(user, savedUpvote.getUpvoteCreator());

      org.mockito.Mockito.clearInvocations(upvoteRepository);
    }
  }

  @Test
  void getAllUpvotes_Success() {
    Map<String, String> filters = new HashMap<>();
    filters.put("entityType", "COURT");
    filters.put("infoType", "SCHEDULE");
    filters.put("userId", user.getId().toString());

    Pageable pageable = PageRequest.of(0, 10);
    Page<Upvote> upvotePage = new PageImpl<>(List.of(testUpvote));

    when(upvoteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(upvotePage);
    when(mapper.userToUserDto(user)).thenReturn(testUserResponse);
    when(courtScheduleRepository.findById(entityId)).thenReturn(Optional.empty());

    Page<UpvoteResponse> result = upvoteService.getAllUpvotes(filters, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());

    verify(upvoteRepository).findAll(any(Specification.class), eq(pageable));
    verify(mapper).userToUserDto(user);
    verify(courtScheduleRepository).findById(entityId);
  }

  @Test
  void getAllUpvotes_WithStringerEntity() {
    Map<String, String> filters = new HashMap<>();
    filters.put("entityType", "STRINGER");
    filters.put("infoType", "PRICE");
    filters.put("userId", user.getId().toString());

    Upvote stringerUpvote = new Upvote();
    stringerUpvote.setUpvoteId(upvoteId);
    stringerUpvote.setEntityType(BadmintonEntityType.STRINGER);
    stringerUpvote.setInfoType(BadmintonInfoType.PRICE);
    stringerUpvote.setEntityId(entityId);
    stringerUpvote.setUpvoteCreator(user);
    stringerUpvote.setCreatedAt(ZonedDateTime.now());

    Pageable pageable = PageRequest.of(0, 10);
    Page<Upvote> upvotePage = new PageImpl<>(List.of(stringerUpvote));

    when(upvoteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(upvotePage);
    when(mapper.userToUserDto(user)).thenReturn(testUserResponse);
    when(stringerPriceRepository.findById(entityId)).thenReturn(Optional.empty());

    Page<UpvoteResponse> result = upvoteService.getAllUpvotes(filters, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());

    verify(stringerPriceRepository).findById(entityId);
  }

  @Test
  void getAllUpvotes_WithCoachEntity() {
    Map<String, String> filters = new HashMap<>();
    filters.put("entityType", "COACH");
    filters.put("infoType", "SCHEDULE");
    filters.put("userId", user.getId().toString());

    Upvote coachUpvote = new Upvote();
    coachUpvote.setUpvoteId(upvoteId);
    coachUpvote.setEntityType(BadmintonEntityType.COACH);
    coachUpvote.setInfoType(BadmintonInfoType.SCHEDULE);
    coachUpvote.setEntityId(entityId);
    coachUpvote.setUpvoteCreator(user);
    coachUpvote.setCreatedAt(ZonedDateTime.now());

    Pageable pageable = PageRequest.of(0, 10);
    Page<Upvote> upvotePage = new PageImpl<>(List.of(coachUpvote));

    when(upvoteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(upvotePage);
    when(mapper.userToUserDto(user)).thenReturn(testUserResponse);
    when(coachScheduleRepository.findById(entityId)).thenReturn(Optional.empty());

    Page<UpvoteResponse> result = upvoteService.getAllUpvotes(filters, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());

    verify(coachScheduleRepository).findById(entityId);
  }

  @Test
  void getAllUpvotes_EmptyResult() {
    Map<String, String> filters = new HashMap<>();
    filters.put("entityType", "COURT");
    filters.put("infoType", "SCHEDULE");
    filters.put("userId", user.getId().toString());

    Pageable pageable = PageRequest.of(0, 10);
    Page<Upvote> emptyPage = new PageImpl<>(List.of());

    when(upvoteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

    Page<UpvoteResponse> result = upvoteService.getAllUpvotes(filters, pageable);

    assertNotNull(result);
    assertEquals(0, result.getContent().size());
    assertEquals(0, result.getTotalElements());

    verify(upvoteRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void getAllUpvotes_WithPagination() {
    Map<String, String> filters = new HashMap<>();
    filters.put("entityType", "COURT");
    filters.put("infoType", "SCHEDULE");
    filters.put("userId", user.getId().toString());

    Pageable pageable = PageRequest.of(2, 5);
    Page<Upvote> upvotePage = new PageImpl<>(List.of(testUpvote), pageable, 1);

    when(upvoteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(upvotePage);
    when(mapper.userToUserDto(user)).thenReturn(testUserResponse);
    when(courtScheduleRepository.findById(entityId)).thenReturn(Optional.empty());

    Page<UpvoteResponse> result = upvoteService.getAllUpvotes(filters, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(2, result.getNumber());
    assertEquals(5, result.getSize());

    verify(upvoteRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void getAllUpvotes_VerifyFilterTransformation() {
    Map<String, String> filters = new HashMap<>();
    filters.put("entityType", "STRINGER");
    filters.put("infoType", "PRICE");
    filters.put("userId", user.getId().toString());

    Pageable pageable = PageRequest.of(0, 10);
    Page<Upvote> upvotePage = new PageImpl<>(List.of(testUpvote));

    when(upvoteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(upvotePage);
    when(mapper.userToUserDto(user)).thenReturn(testUserResponse);
    when(courtScheduleRepository.findById(entityId)).thenReturn(Optional.empty());

    upvoteService.getAllUpvotes(filters, pageable);

    assertEquals("STRINGER", filters.get("entityType"));
    assertEquals("PRICE", filters.get("infoType"));
    assertEquals(user.getId().toString(), filters.get("userId"));

    verify(upvoteRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void getAllUpvotes_WithMultipleUpvotes() {
    Map<String, String> filters = new HashMap<>();
    filters.put("entityType", "COURT");
    filters.put("infoType", "SCHEDULE");
    filters.put("userId", user.getId().toString());

    UUID upvoteId2 = UUID.randomUUID();
    Upvote upvote2 = new Upvote();
    upvote2.setUpvoteId(upvoteId2);
    upvote2.setEntityType(BadmintonEntityType.COURT);
    upvote2.setInfoType(BadmintonInfoType.SCHEDULE);
    upvote2.setEntityId(entityId);
    upvote2.setUpvoteCreator(user);
    upvote2.setCreatedAt(ZonedDateTime.now());

    Pageable pageable = PageRequest.of(0, 10);
    Page<Upvote> upvotePage = new PageImpl<>(List.of(testUpvote, upvote2));

    when(upvoteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(upvotePage);
    when(mapper.userToUserDto(user)).thenReturn(testUserResponse);
    when(courtScheduleRepository.findById(entityId)).thenReturn(Optional.empty());

    Page<UpvoteResponse> result = upvoteService.getAllUpvotes(filters, pageable);

    assertNotNull(result);
    assertEquals(2, result.getContent().size());

    verify(upvoteRepository).findAll(any(Specification.class), eq(pageable));
    verify(mapper, times(2)).userToUserDto(user);
    verify(courtScheduleRepository, times(2)).findById(entityId);
  }
}