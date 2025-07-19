package com.shuttleverse.community.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.model.Stringer;
import com.shuttleverse.community.model.StringerPrice;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.repository.StringerPriceRepository;
import com.shuttleverse.community.repository.StringerRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class StringerServiceTest {

  @Mock
  private StringerRepository stringerRepository;

  @Mock
  private StringerPriceRepository priceRepository;

  @InjectMocks
  private StringerService stringerService;

  private Stringer stringer;
  private StringerPrice price;
  private User user;
  private UUID stringerId;
  private UUID userId;
  private UUID priceId;

  @BeforeEach
  void setUp() {
    stringerId = UUID.randomUUID();
    userId = UUID.randomUUID();
    priceId = UUID.randomUUID();

    user = new User();
    user.setId(userId);
    user.setUsername("testuser");

    stringer = new Stringer();
    stringer.setId(stringerId);
    stringer.setName("Test Stringer");
    stringer.setOwner(user);

    price = new StringerPrice();
    price.setId(priceId);
    price.setStringName("Test String");
    price.setPrice(30.0);
    price.setUpvotes(0);
    price.setIsVerified(false);
    price.setStringerId(stringerId);
    price.setSubmittedBy(user);
  }

  @Test
  void createStringer_Success() {
    when(stringerRepository.save(any(Stringer.class))).thenReturn(stringer);

    Stringer result = stringerService.createStringer(stringer, user);

    assertNotNull(result);
    assertEquals(stringer.getId(), result.getId());
    assertEquals(stringer.getName(), result.getName());
    assertEquals(user, result.getOwner());
    verify(stringerRepository).save(any(Stringer.class));
  }

  @Test
  void getStringer_Success() {
    when(stringerRepository.findById(any(UUID.class))).thenReturn(Optional.of(stringer));

    Stringer result = stringerService.getStringer(stringerId);

    assertNotNull(result);
    assertEquals(stringer.getId(), result.getId());
    assertEquals(stringer.getName(), result.getName());
    verify(stringerRepository).findById(stringerId);
  }

  @Test
  void getStringer_NotFound() {
    when(stringerRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> stringerService.getStringer(stringerId));
    verify(stringerRepository).findById(stringerId);
  }

  @Test
  void addPrice_Success() {
    UUID stringerId = UUID.fromString("8af52394-7d79-4069-9470-e71f03eb4218");
    Stringer stringer = new Stringer();
    stringer.setId(stringerId);
    when(stringerRepository.findById(stringerId)).thenReturn(Optional.of(stringer));
    when(priceRepository.saveAll(any())).thenReturn(List.of(price));

    List<StringerPrice> result = stringerService.addPrice(user, stringerId, List.of(price));

    verify(stringerRepository).findById(stringerId);
    verify(priceRepository).saveAll(any());
    assertEquals(1, result.size());
    assertEquals(price, result.get(0));
  }

  @Test
  void updatePrice_Success() {
    UUID stringerId = UUID.fromString("e5569ae6-ca1a-4668-8e77-58d6604b105d");
    Stringer stringer = new Stringer();
    stringer.setId(stringerId);
    stringer.setOwner(user);
    when(stringerRepository.findById(stringerId)).thenReturn(Optional.of(stringer));
    when(priceRepository.save(any())).thenReturn(price);

    StringerPrice result = stringerService.updatePrice(stringerId, priceId, price);

    verify(stringerRepository, times(2)).findById(stringerId);
    verify(priceRepository).save(any());
    assertEquals(price, result);
  }

  @Test
  void upvotePrice_Success() {
    when(priceRepository.findById(any(UUID.class))).thenReturn(Optional.of(price));
    when(priceRepository.save(any(StringerPrice.class))).thenReturn(price);

    StringerPrice result = stringerService.upvotePrice(priceId);

    assertNotNull(result);
    assertEquals(1, result.getUpvotes());
    verify(priceRepository).findById(priceId);
    verify(priceRepository).save(any(StringerPrice.class));
  }

  @Test
  void upvotePrice_NotFound() {
    when(priceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> stringerService.upvotePrice(priceId));
    verify(priceRepository).findById(priceId);
  }
}