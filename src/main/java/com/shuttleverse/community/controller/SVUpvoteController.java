package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.SVApiResponse;
import com.shuttleverse.community.dto.SVUpvoteResponse;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.params.UpvoteParams;
import com.shuttleverse.community.service.SVUpvoteService;
import com.shuttleverse.community.util.SVAuthenticationUtils;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/upvote")
@RequiredArgsConstructor
public class SVUpvoteController {

  private final SVUpvoteService upvoteService;

  @GetMapping
  public ResponseEntity<SVApiResponse<Page<SVUpvoteResponse>>> getAllUpvotes(
      @ModelAttribute UpvoteParams params,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    try {
      SVUser user = SVAuthenticationUtils.getCurrentUser();
      Pageable pageable = PageRequest.of(page, size);

      Page<SVUpvoteResponse> upvotes = upvoteService.getAllUpvotes(user, params, pageable);
      return ResponseEntity.ok(SVApiResponse.success(upvotes));
    } catch (Exception e) {
      return ResponseEntity.ok(SVApiResponse.success(new PageImpl<>(Collections.emptyList())));
    }
  }
}
