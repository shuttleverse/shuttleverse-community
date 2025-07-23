package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.dto.UpvoteResponse;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.UpvoteService;
import com.shuttleverse.community.util.AuthenticationUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/upvote")
@RequiredArgsConstructor
public class UpvoteController {

  private final UpvoteService upvoteService;

  @GetMapping
  public ResponseEntity<ApiResponse<Page<UpvoteResponse>>> getAllUpvotes(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size,
      @RequestParam Map<String, String> params) {
    try {
      User user = AuthenticationUtils.getCurrentUser();

      Map<String, String> filters = new HashMap<>(params);
      filters.put("userId", String.valueOf(user.getId()));
      filters.remove("page");
      filters.remove("size");

      Pageable pageable = PageRequest.of(
          page,
          size);

      Page<UpvoteResponse> upvotes = upvoteService.getAllUpvotes(filters, pageable);
      return ResponseEntity.ok(ApiResponse.success(upvotes));
    } catch (Exception e) {
      return ResponseEntity.ok(ApiResponse.success(new PageImpl<>(Collections.emptyList())));
    }
  }
}
