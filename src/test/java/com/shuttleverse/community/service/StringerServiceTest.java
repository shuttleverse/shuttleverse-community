package com.shuttleverse.community.service;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        price.setStringer(stringer);
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
        List<StringerPrice> prices = List.of(price);
        when(stringerRepository.findById(any(UUID.class))).thenReturn(Optional.of(stringer));
        when(priceRepository.saveAll(anyList())).thenReturn(prices);

        List<StringerPrice> results = stringerService.addPrice(stringerId, prices);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(price.getId(), results.get(0).getId());
        assertEquals(price.getStringName(), results.get(0).getStringName());
        assertEquals(price.getPrice(), results.get(0).getPrice());
        verify(stringerRepository).findById(stringerId);
        verify(priceRepository).saveAll(anyList());
    }

    @Test
    void updatePrice_Success() {
        when(stringerRepository.findById(any(UUID.class))).thenReturn(Optional.of(stringer));
        when(priceRepository.save(any(StringerPrice.class))).thenReturn(price);

        StringerPrice result = stringerService.updatePrice(stringerId, priceId, price);

        assertNotNull(result);
        assertEquals(price.getId(), result.getId());
        assertEquals(price.getStringName(), result.getStringName());
        assertEquals(price.getPrice(), result.getPrice());
        verify(stringerRepository, times(2)).findById(stringerId);
        verify(priceRepository).save(any(StringerPrice.class));
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

    @Test
    void isOwner_Success() {
        when(stringerRepository.findById(any(UUID.class))).thenReturn(Optional.of(stringer));

        boolean result = stringerService.isOwner(stringerId, userId);

        assertTrue(result);
        verify(stringerRepository).findById(stringerId);
    }

    @Test
    void isOwner_Failure() {
        when(stringerRepository.findById(any(UUID.class))).thenReturn(Optional.of(stringer));

        boolean result = stringerService.isOwner(stringerId, UUID.randomUUID());

        assertFalse(result);
        verify(stringerRepository).findById(stringerId);
    }
}