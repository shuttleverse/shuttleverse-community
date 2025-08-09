package com.shuttleverse.community.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.SVBaseTest;
import com.shuttleverse.community.model.SVStringer;
import com.shuttleverse.community.model.SVStringerPrice;
import com.shuttleverse.community.repository.SVStringerPriceRepository;
import com.shuttleverse.community.repository.SVStringerRepository;
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
class SVStringerServiceTest extends SVBaseTest {

  @Mock
  private SVStringerRepository stringerRepository;

  @Mock
  private SVStringerPriceRepository priceRepository;

  @InjectMocks
  private SVStringerService stringerService;

  private SVStringer stringer;
  private SVStringerPrice price;
  private UUID stringerId;
  private UUID priceId;

  @BeforeEach
  void setUp() {
    stringerId = UUID.randomUUID();
    priceId = UUID.randomUUID();

    stringer = new SVStringer();
    stringer.setId(stringerId);
    stringer.setName("Test Stringer");
    stringer.setOwner(user);

    price = new SVStringerPrice();
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
    when(stringerRepository.save(any(SVStringer.class))).thenReturn(stringer);

    SVStringer result = stringerService.createStringer(stringer, user);

    assertNotNull(result);
    assertEquals(stringer.getId(), result.getId());
    assertEquals(stringer.getName(), result.getName());
    assertEquals(user, result.getOwner());
    verify(stringerRepository).save(any(SVStringer.class));
  }

  @Test
  void getStringer_Success() {
    when(stringerRepository.findById(any(UUID.class))).thenReturn(Optional.of(stringer));

    SVStringer result = stringerService.getStringer(stringerId);

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
    SVStringer stringer = new SVStringer();
    stringer.setId(stringerId);
    when(stringerRepository.findById(stringerId)).thenReturn(Optional.of(stringer));
    when(priceRepository.saveAll(any())).thenReturn(List.of(price));

    List<SVStringerPrice> result = stringerService.addPrice(user, stringerId, List.of(price));

    verify(stringerRepository).findById(stringerId);
    verify(priceRepository).saveAll(any());
    assertEquals(1, result.size());
    assertEquals(price, result.get(0));
  }

  @Test
  void updatePrice_Success() {
    stringer.setOwner(user);
    when(stringerRepository.findById(stringerId)).thenReturn(Optional.of(stringer));
    when(priceRepository.save(any())).thenReturn(price);

    SVStringerPrice result = stringerService.updatePrice(stringerId, priceId, price);

    verify(stringerRepository, times(1)).findById(stringerId);
    verify(priceRepository).save(any());
    assertEquals(price, result);
  }

  @Test
  void upvotePrice_Success() {
    when(priceRepository.findById(any(UUID.class))).thenReturn(Optional.of(price));
    when(priceRepository.save(any(SVStringerPrice.class))).thenReturn(price);

    SVStringerPrice result = stringerService.upvotePrice(priceId);

    assertNotNull(result);
    assertEquals(1, result.getUpvotes());
    verify(priceRepository).findById(priceId);
    verify(priceRepository).save(any(SVStringerPrice.class));
  }

  @Test
  void upvotePrice_NotFound() {
    when(priceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> stringerService.upvotePrice(priceId));
    verify(priceRepository).findById(priceId);
  }
}