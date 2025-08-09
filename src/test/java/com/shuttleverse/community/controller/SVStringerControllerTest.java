package com.shuttleverse.community.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.SVBaseTest;
import com.shuttleverse.community.api.SVApiResponse;
import com.shuttleverse.community.dto.SVStringerCreationData;
import com.shuttleverse.community.dto.SVStringerPriceResponse;
import com.shuttleverse.community.dto.SVStringerResponse;
import com.shuttleverse.community.mapper.SVMapStructMapper;
import com.shuttleverse.community.model.SVStringer;
import com.shuttleverse.community.model.SVStringerPrice;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.service.SVStringerService;
import com.shuttleverse.community.service.SVUserService;
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
class SVStringerControllerTest extends SVBaseTest {

  @Mock
  private SVStringerService stringerService;

  @Mock
  private SVUserService userService;

  @Mock
  private SVMapStructMapper mapper;

  @InjectMocks
  private SVStringerController stringerController;

  private SVStringer stringer;
  private SVStringerPrice price;
  private UUID stringerId;
  private UUID priceId;
  private Jwt jwt;
  private SVStringerResponse stringerResponse;
  private SVStringerPriceResponse priceResponse;
  private SVStringerCreationData stringerCreationData;

  @BeforeEach
  void setUp() {
    stringerId = UUID.randomUUID();
    priceId = UUID.randomUUID();

    stringer = new SVStringer();
    stringer.setId(stringerId);
    stringer.setName("Test Stringer");
    stringer.setOwner(user);

    stringerCreationData = new SVStringerCreationData();
    stringerCreationData.setName("Test Stringer");
    stringerCreationData.setLocation("Test Location");
    stringerCreationData.setDescription("Test Description");
    stringerCreationData.setOtherContacts("Test Contacts");
    stringerCreationData.setPhoneNumber("1234567890");

    price = new SVStringerPrice();
    price.setId(priceId);
    price.setStringName("Polyester");
    price.setPrice(20.0);
    price.setStringerId(stringerId);
    price.setUpvotes(0);
    price.setIsVerified(false);
    price.setSubmittedBy(user);

    stringerResponse = new SVStringerResponse();
    stringerResponse.setId(stringerId);
    stringerResponse.setName("Test Stringer");

    priceResponse = new SVStringerPriceResponse();
    priceResponse.setId(priceId);
    priceResponse.setStringName("Polyester");
    priceResponse.setPrice(20.0);

    jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("sub", "user-123")
        .build();
  }

  @Test
  void createStringer_Success() {
    when(mapper.toStringer(any(SVStringerCreationData.class))).thenReturn(stringer);
    when(stringerService.createStringer(any(SVStringer.class), any(SVUser.class))).thenReturn(
        stringer);
    when(mapper.toStringerResponse(any(SVStringer.class))).thenReturn(stringerResponse);

    ResponseEntity<SVApiResponse<SVStringerResponse>> response = stringerController.createStringer(
        stringerCreationData);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(stringerResponse, response.getBody().getData());
    verify(mapper).toStringer(any(SVStringerCreationData.class));
    verify(stringerService).createStringer(any(SVStringer.class), any(SVUser.class));
  }

  @Test
  void getStringer_Success() {
    when(stringerService.getStringer(any(UUID.class))).thenReturn(stringer);
    when(mapper.toStringerResponse(any(SVStringer.class))).thenReturn(stringerResponse);

    ResponseEntity<SVApiResponse<SVStringerResponse>> response = stringerController.getStringer(
        stringerId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(stringerResponse, response.getBody().getData());
    verify(stringerService).getStringer(stringerId);
  }

  @Test
  void updateStringer_Success() {
    when(stringerService.updateStringer(any(UUID.class), any(SVStringer.class))).thenReturn(
        stringer);
    when(mapper.toStringerResponse(any(SVStringer.class))).thenReturn(stringerResponse);

    ResponseEntity<SVApiResponse<SVStringerResponse>> response = stringerController.updateStringer(
        stringerId.toString(), stringer);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(stringerResponse, response.getBody().getData());
    verify(stringerService).updateStringer(stringerId, stringer);
  }

  @Test
  void deleteStringer_Success() {
    doNothing().when(stringerService).deleteStringer(any(UUID.class));

    ResponseEntity<SVApiResponse<Void>> response = stringerController.deleteStringer(
        stringerId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    verify(stringerService).deleteStringer(stringerId);
  }

  @Test
  void addPrice_Success() {
    List<SVStringerPrice> prices = List.of(price);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(prices).when(stringerService)
        .addPrice(any(SVUser.class), any(UUID.class), any(List.class));
    when(mapper.toStringerPriceResponse(any(SVStringerPrice.class))).thenReturn(priceResponse);

    ResponseEntity<SVApiResponse<List<SVStringerPriceResponse>>> response = stringerController.addPrice(
        stringerId.toString(),
        prices,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(priceResponse, response.getBody().getData().get(0));
    verify(stringerService).addPrice(any(SVUser.class), any(UUID.class), any(List.class));
  }

  @Test
  void updatePrice_Success() {
    when(stringerService.updatePrice(any(UUID.class), any(UUID.class),
        any(SVStringerPrice.class))).thenReturn(price);
    when(mapper.toStringerPriceResponse(any(SVStringerPrice.class))).thenReturn(priceResponse);

    ResponseEntity<SVApiResponse<SVStringerPriceResponse>> response = stringerController.updatePrice(
        stringerId.toString(),
        priceId.toString(),
        price);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(priceResponse, response.getBody().getData());
    verify(stringerService).updatePrice(stringerId, priceId, price);
  }

  @Test
  void addMultiplePrices_Success() {
    SVStringerPrice price1 = new SVStringerPrice();
    price1.setId(UUID.randomUUID());
    price1.setStringName("Yonex BG65");
    price1.setPrice(20.0);
    price1.setStringerId(stringerId);
    price1.setUpvotes(0);
    price1.setIsVerified(false);
    price1.setSubmittedBy(user);

    SVStringerPrice price2 = new SVStringerPrice();
    price2.setId(UUID.randomUUID());
    price2.setStringName("Yonex BG80");
    price2.setPrice(25.0);
    price2.setStringerId(stringerId);
    price2.setUpvotes(0);
    price2.setIsVerified(false);
    price2.setSubmittedBy(user);

    List<SVStringerPrice> allPrices = List.of(price1, price2);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(allPrices).when(stringerService)
        .addPrice(any(SVUser.class), any(UUID.class), any(List.class));

    SVStringerPriceResponse response1 = new SVStringerPriceResponse();
    response1.setId(price1.getId());
    response1.setStringName(price1.getStringName());
    response1.setPrice(price1.getPrice());

    SVStringerPriceResponse response2 = new SVStringerPriceResponse();
    response2.setId(price2.getId());
    response2.setStringName(price2.getStringName());
    response2.setPrice(price2.getPrice());

    when(mapper.toStringerPriceResponse(price1)).thenReturn(response1);
    when(mapper.toStringerPriceResponse(price2)).thenReturn(response2);

    ResponseEntity<SVApiResponse<List<SVStringerPriceResponse>>> response = stringerController.addPrice(
        stringerId.toString(),
        allPrices,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(2, response.getBody().getData().size());
    assertEquals(response1, response.getBody().getData().get(0));
    assertEquals(response2, response.getBody().getData().get(1));
    verify(stringerService).addPrice(any(SVUser.class), any(UUID.class), any(List.class));
  }

  @Test
  void upvotePrice_Success() {
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(stringerService.upvotePrice(any(UUID.class), any(SVUser.class))).thenReturn(price);
    when(mapper.toStringerPriceResponse(any(SVStringerPrice.class))).thenReturn(priceResponse);

    ResponseEntity<SVApiResponse<SVStringerPriceResponse>> response = stringerController.upvotePrice(
        priceId.toString(), jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(priceResponse, response.getBody().getData());
    verify(stringerService).upvotePrice(priceId, user);
  }

  @Test
  void addNewPriceForExistingStringer_Success() {
    SVStringerPrice newPrice = new SVStringerPrice();
    newPrice.setId(UUID.randomUUID());
    newPrice.setStringName("Yonex BG95");
    newPrice.setPrice(30.0);
    newPrice.setStringerId(stringerId);
    newPrice.setUpvotes(0);
    newPrice.setIsVerified(false);
    newPrice.setSubmittedBy(user);

    List<SVStringerPrice> prices = List.of(newPrice);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(prices).when(stringerService)
        .addPrice(any(SVUser.class), any(UUID.class), any(List.class));

    SVStringerPriceResponse newPriceResponse = new SVStringerPriceResponse();
    newPriceResponse.setId(newPrice.getId());
    newPriceResponse.setStringName(newPrice.getStringName());
    newPriceResponse.setPrice(newPrice.getPrice());

    when(mapper.toStringerPriceResponse(newPrice)).thenReturn(newPriceResponse);

    ResponseEntity<SVApiResponse<List<SVStringerPriceResponse>>> response = stringerController.addPrice(
        stringerId.toString(),
        prices,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(newPriceResponse, response.getBody().getData().get(0));
    verify(stringerService).addPrice(any(SVUser.class), any(UUID.class), any(List.class));
  }
}