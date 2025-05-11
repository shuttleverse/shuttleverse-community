package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Stringer;
import com.shuttleverse.community.model.StringerPrice;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.StringerService;
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
class StringerControllerTest {

    @Mock
    private StringerService stringerService;

    @InjectMocks
    private StringerController stringerController;

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
        price.setStringName("Polyester");
        price.setPrice(20.0);
        price.setStringerId(1L);
        price.setUpvotes(0);
        price.setIsVerified(false);
        price.setSubmittedBy(user);
    }

    @Test
    void createStringer_Success() {
        when(stringerService.createStringer(any(Stringer.class))).thenReturn(stringer);

        ResponseEntity<ApiResponse<Stringer>> response = stringerController.createStringer(stringer);

        assertTrue(response.getBody().isSuccess());
        assertEquals(stringer, response.getBody().getData());
        verify(stringerService).createStringer(any(Stringer.class));
    }

    @Test
    void getStringer_Success() {
        when(stringerService.getStringer(anyLong())).thenReturn(stringer);

        ResponseEntity<ApiResponse<Stringer>> response = stringerController.getStringer(1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals(stringer, response.getBody().getData());
        verify(stringerService).getStringer(1L);
    }

    @Test
    void updateStringer_Success() {
        when(stringerService.updateStringer(anyLong(), any(Stringer.class))).thenReturn(stringer);

        ResponseEntity<ApiResponse<Stringer>> response = stringerController.updateStringer(1L, stringer);

        assertTrue(response.getBody().isSuccess());
        assertEquals(stringer, response.getBody().getData());
        verify(stringerService).updateStringer(1L, stringer);
    }

    @Test
    void deleteStringer_Success() {
        doNothing().when(stringerService).deleteStringer(anyLong());

        ResponseEntity<ApiResponse<Void>> response = stringerController.deleteStringer(1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals("Stringer deleted successfully", response.getBody().getMessage());
        verify(stringerService).deleteStringer(1L);
    }

    @Test
    void addPrice_Success() {
        when(stringerService.addPrice(anyLong(), any(StringerPrice.class))).thenReturn(price);

        ResponseEntity<ApiResponse<StringerPrice>> response = stringerController.addPrice(1L, price);

        assertTrue(response.getBody().isSuccess());
        assertEquals(price, response.getBody().getData());
        verify(stringerService).addPrice(1L, price);
    }

    @Test
    void updatePrice_Success() {
        when(stringerService.updatePrice(anyLong(), anyLong(), any(StringerPrice.class))).thenReturn(price);

        ResponseEntity<ApiResponse<StringerPrice>> response = stringerController.updatePrice(1L, 1L, price);

        assertTrue(response.getBody().isSuccess());
        assertEquals(price, response.getBody().getData());
        verify(stringerService).updatePrice(1L, 1L, price);
    }

    @Test
    void addMultiplePrices_Success() {
        // Create multiple string prices
        StringerPrice price1 = new StringerPrice();
        price1.setStringName("Yonex BG65");
        price1.setPrice(20.0);
        price1.setStringerId(1L);
        price1.setUpvotes(0);
        price1.setIsVerified(false);
        price1.setSubmittedBy(user);

        StringerPrice price2 = new StringerPrice();
        price2.setStringName("Yonex BG80");
        price2.setPrice(25.0);
        price2.setStringerId(1L);
        price2.setUpvotes(0);
        price2.setIsVerified(false);
        price2.setSubmittedBy(user);

        when(stringerService.addPrice(anyLong(), any(StringerPrice.class)))
                .thenReturn(price1)
                .thenReturn(price2);

        // Add first price
        ResponseEntity<ApiResponse<StringerPrice>> response1 = stringerController.addPrice(1L, price1);
        assertTrue(response1.getBody().isSuccess());
        assertEquals(price1, response1.getBody().getData());

        // Add second price
        ResponseEntity<ApiResponse<StringerPrice>> response2 = stringerController.addPrice(1L, price2);
        assertTrue(response2.getBody().isSuccess());
        assertEquals(price2, response2.getBody().getData());

        verify(stringerService, times(2)).addPrice(anyLong(), any(StringerPrice.class));
    }

    @Test
    void upvotePrice_Success() {
        StringerPrice price = new StringerPrice();
        price.setId(1L);
        price.setStringName("Yonex BG65");
        price.setPrice(20.0);
        price.setStringerId(1L);
        price.setUpvotes(1); // After upvote
        price.setIsVerified(false);
        price.setSubmittedBy(user);

        when(stringerService.upvotePrice(anyLong())).thenReturn(price);

        ResponseEntity<ApiResponse<StringerPrice>> response = stringerController.upvotePrice(1L, 1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals(price, response.getBody().getData());
        assertEquals(1, response.getBody().getData().getUpvotes());
        verify(stringerService).upvotePrice(1L);
    }

    @Test
    void addNewPriceForExistingStringer_Success() {
        StringerPrice newPrice = new StringerPrice();
        newPrice.setStringName("Yonex BG95");
        newPrice.setPrice(30.0);
        newPrice.setStringerId(1L);
        newPrice.setUpvotes(0);
        newPrice.setIsVerified(false);
        newPrice.setSubmittedBy(user);

        when(stringerService.addPrice(anyLong(), any(StringerPrice.class))).thenReturn(newPrice);

        ResponseEntity<ApiResponse<StringerPrice>> response = stringerController.addPrice(1L, newPrice);

        assertTrue(response.getBody().isSuccess());
        assertEquals(newPrice, response.getBody().getData());
        verify(stringerService).addPrice(1L, newPrice);
    }
}