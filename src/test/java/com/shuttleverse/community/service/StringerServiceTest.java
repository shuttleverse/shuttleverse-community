package com.shuttleverse.community.service;

import com.shuttleverse.community.model.Stringer;
import com.shuttleverse.community.model.StringerPrice;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.repository.StringerRepository;
import com.shuttleverse.community.repository.StringerPriceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        stringer = new Stringer();
        stringer.setId(1L);
        stringer.setName("Test Stringer");
        stringer.setOwner(user);

        price = new StringerPrice();
        price.setId(1L);
        price.setStringName("Yonex BG65");
        price.setPrice(20.0);
        price.setStringerId(1L);
        price.setUpvotes(0);
        price.setIsVerified(false);
        price.setSubmittedBy(user);
    }

    @Test
    void createStringer_Success() {
        when(stringerRepository.save(any(Stringer.class))).thenReturn(stringer);

        Stringer result = stringerService.createStringer(stringer);

        assertNotNull(result);
        assertEquals(stringer.getId(), result.getId());
        assertEquals(stringer.getName(), result.getName());
        verify(stringerRepository).save(any(Stringer.class));
    }

    @Test
    void getStringer_Success() {
        when(stringerRepository.findById(anyLong())).thenReturn(Optional.of(stringer));

        Stringer result = stringerService.getStringer(1L);

        assertNotNull(result);
        assertEquals(stringer.getId(), result.getId());
        assertEquals(stringer.getName(), result.getName());
        verify(stringerRepository).findById(1L);
    }

    @Test
    void getStringer_NotFound() {
        when(stringerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stringerService.getStringer(1L));
        verify(stringerRepository).findById(1L);
    }

    @Test
    void addPrice_Success() {
        when(stringerRepository.findById(anyLong())).thenReturn(Optional.of(stringer));
        when(priceRepository.save(any(StringerPrice.class))).thenReturn(price);

        StringerPrice result = stringerService.addPrice(1L, price);

        assertNotNull(result);
        assertEquals(price.getId(), result.getId());
        assertEquals(price.getStringName(), result.getStringName());
        assertEquals(price.getPrice(), result.getPrice());
        verify(stringerRepository).findById(1L);
        verify(priceRepository).save(any(StringerPrice.class));
    }

    @Test
    void upvotePrice_Success() {
        when(priceRepository.findById(anyLong())).thenReturn(Optional.of(price));
        when(priceRepository.save(any(StringerPrice.class))).thenReturn(price);

        StringerPrice result = stringerService.upvotePrice(1L);

        assertNotNull(result);
        assertEquals(1, result.getUpvotes());
        verify(priceRepository).findById(1L);
        verify(priceRepository).save(any(StringerPrice.class));
    }

    @Test
    void upvotePrice_NotFound() {
        when(priceRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stringerService.upvotePrice(1L));
        verify(priceRepository).findById(1L);
    }

    @Test
    void updatePrice_Success() {
        when(stringerRepository.findById(anyLong())).thenReturn(Optional.of(stringer));
        when(priceRepository.save(any(StringerPrice.class))).thenReturn(price);

        StringerPrice result = stringerService.updatePrice(1L, 1L, price);

        assertNotNull(result);
        assertEquals(price.getId(), result.getId());
        assertEquals(price.getStringName(), result.getStringName());
        assertEquals(price.getPrice(), result.getPrice());
        verify(stringerRepository, times(2)).findById(1L);
        verify(priceRepository).save(any(StringerPrice.class));
    }

    @Test
    void isOwner_Success() {
        when(stringerRepository.findById(anyLong())).thenReturn(Optional.of(stringer));

        boolean result = stringerService.isOwner(1L, 1L);

        assertTrue(result);
        verify(stringerRepository).findById(1L);
    }

    @Test
    void isOwner_Failure() {
        when(stringerRepository.findById(anyLong())).thenReturn(Optional.of(stringer));

        boolean result = stringerService.isOwner(1L, 2L);

        assertFalse(result);
        verify(stringerRepository).findById(1L);
    }
}