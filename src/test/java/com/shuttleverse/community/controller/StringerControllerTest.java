package com.shuttleverse.community.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Stringer;
import com.shuttleverse.community.model.StringerPrice;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.StringerService;
import com.shuttleverse.community.service.UserService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class StringerControllerTest {

  @Mock
  private StringerService stringerService;

  @Mock
  private UserService userService;

  @InjectMocks
  private StringerController stringerController;

  private Stringer stringer;
  private StringerPrice price;
  private User user;
  private UUID stringerId;
  private UUID priceId;
  private Jwt jwt;

  @BeforeEach
  void setUp() {
    stringerId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
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
    price.setStringName("Polyester");
    price.setPrice(20.0);
    price.setStringerId(stringerId);
    price.setUpvotes(0);
    price.setIsVerified(false);
    price.setSubmittedBy(user);

    // Mock JWT token
    jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("sub", "user-123")
        .build();
  }

  @Test
  void createStringer_Success() {
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(stringerService.createStringer(any(Stringer.class), any(User.class))).thenReturn(stringer);

    ResponseEntity<ApiResponse<Stringer>> response = stringerController.createStringer(stringer,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(stringer, response.getBody().getData());
    verify(stringerService).createStringer(any(Stringer.class), any(User.class));
  }

  @Test
  void getStringer_Success() {
    when(stringerService.getStringer(any(UUID.class))).thenReturn(stringer);

    ResponseEntity<ApiResponse<Stringer>> response = stringerController.getStringer(
        stringerId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(stringer, response.getBody().getData());
    verify(stringerService).getStringer(stringerId);
  }

  @Test
  void updateStringer_Success() {
    when(stringerService.updateStringer(any(UUID.class), any(Stringer.class))).thenReturn(stringer);

    ResponseEntity<ApiResponse<Stringer>> response = stringerController.updateStringer(
        stringerId.toString(), stringer);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(stringer, response.getBody().getData());
    verify(stringerService).updateStringer(stringerId, stringer);
  }

  @Test
  void deleteStringer_Success() {
    doNothing().when(stringerService).deleteStringer(any(UUID.class));

    ResponseEntity<ApiResponse<Void>> response = stringerController.deleteStringer(
        stringerId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    verify(stringerService).deleteStringer(stringerId);
  }

  @Test
  void addPrice_Success() {
    List<StringerPrice> prices = List.of(price);
    doReturn(prices).when(stringerService).addPrice(any(UUID.class), any(List.class));

    ResponseEntity<ApiResponse<List<StringerPrice>>> response = stringerController.addPrice(
        stringerId.toString(),
        prices);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(price, response.getBody().getData().get(0));
    verify(stringerService).addPrice(stringerId, prices);
  }

  @Test
  void updatePrice_Success() {
    when(stringerService.updatePrice(any(UUID.class), any(UUID.class),
        any(StringerPrice.class))).thenReturn(price);

    ResponseEntity<ApiResponse<StringerPrice>> response = stringerController.updatePrice(
        stringerId.toString(),
        priceId.toString(),
        price);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(price, response.getBody().getData());
    verify(stringerService).updatePrice(stringerId, priceId, price);
  }

  @Test
  void addMultiplePrices_Success() {
    // Create multiple string prices
    StringerPrice price1 = new StringerPrice();
    price1.setId(UUID.randomUUID());
    price1.setStringName("Yonex BG65");
    price1.setPrice(20.0);
    price1.setStringerId(stringerId);
    price1.setUpvotes(0);
    price1.setIsVerified(false);
    price1.setSubmittedBy(user);

    StringerPrice price2 = new StringerPrice();
    price2.setId(UUID.randomUUID());
    price2.setStringName("Yonex BG80");
    price2.setPrice(25.0);
    price2.setStringerId(stringerId);
    price2.setUpvotes(0);
    price2.setIsVerified(false);
    price2.setSubmittedBy(user);

    List<StringerPrice> allPrices = List.of(price1, price2);
    doReturn(allPrices).when(stringerService).addPrice(any(UUID.class), any(List.class));

    ResponseEntity<ApiResponse<List<StringerPrice>>> response = stringerController.addPrice(
        stringerId.toString(),
        allPrices);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(2, response.getBody().getData().size());
    assertEquals(price1, response.getBody().getData().get(0));
    assertEquals(price2, response.getBody().getData().get(1));
    verify(stringerService).addPrice(stringerId, allPrices);
  }

  @Test
  void upvotePrice_Success() {
    StringerPrice price = new StringerPrice();
    price.setId(priceId);
    price.setStringName("Yonex BG65");
    price.setPrice(20.0);
    price.setStringerId(stringerId);
    price.setUpvotes(1);
    price.setIsVerified(false);
    price.setSubmittedBy(user);

    when(stringerService.upvotePrice(any(UUID.class))).thenReturn(price);

    ResponseEntity<ApiResponse<StringerPrice>> response = stringerController.upvotePrice(
        priceId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(price, response.getBody().getData());
    assertEquals(1, response.getBody().getData().getUpvotes());
    verify(stringerService).upvotePrice(priceId);
  }

  @Test
  void addNewPriceForExistingStringer_Success() {
    StringerPrice newPrice = new StringerPrice();
    newPrice.setId(UUID.randomUUID());
    newPrice.setStringName("Yonex BG95");
    newPrice.setPrice(30.0);
    newPrice.setStringerId(stringerId);
    newPrice.setUpvotes(0);
    newPrice.setIsVerified(false);
    newPrice.setSubmittedBy(user);

    List<StringerPrice> prices = List.of(newPrice);
    doReturn(prices).when(stringerService).addPrice(any(UUID.class), any(List.class));

    ResponseEntity<ApiResponse<List<StringerPrice>>> response = stringerController.addPrice(
        stringerId.toString(),
        prices);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(newPrice, response.getBody().getData().get(0));
    verify(stringerService).addPrice(stringerId, prices);
  }
}