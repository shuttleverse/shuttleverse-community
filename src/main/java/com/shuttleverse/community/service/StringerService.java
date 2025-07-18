package com.shuttleverse.community.service;

import com.shuttleverse.community.constants.BadmintonEntityType;
import com.shuttleverse.community.constants.BadmintonInfoType;
import com.shuttleverse.community.model.Stringer;
import com.shuttleverse.community.model.StringerPrice;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.params.BoundingBoxParams;
import com.shuttleverse.community.params.WithinDistanceParams;
import com.shuttleverse.community.repository.StringerPriceRepository;
import com.shuttleverse.community.repository.StringerRepository;
import com.shuttleverse.community.util.SpecificationBuilder;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StringerService {

  private final StringerRepository stringerRepository;
  private final StringerPriceRepository priceRepository;
  private final UpvoteService upvoteService;

  @Transactional
  public Stringer createStringer(Stringer stringer, User creator) {
    stringer.setCreator(creator);
    return stringerRepository.save(stringer);
  }

  public Stringer getStringer(UUID id) {
    return stringerRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Stringer not found with id: " + id));
  }

  public Page<Stringer> getAllStringers(Map<String, String> filters, Pageable pageable) {
    Specification<Stringer> spec = SpecificationBuilder.buildSpecification(filters);
    return stringerRepository.findAll(spec, pageable);
  }

  public Page<Stringer> getCourtsByBoundingBox(BoundingBoxParams params, Pageable pageable) {
    return stringerRepository.findWithinBounds(params.getMinLon(), params.getMinLat(),
        params.getMaxLon(), params.getMaxLat(), pageable);
  }

  public Page<Stringer> getStringersWithinDistance(WithinDistanceParams params, Pageable pageable) {
    return stringerRepository.findWithinDistance(params.getLocation(), params.getDistance(),
        pageable);
  }

  @Transactional
  public Stringer updateStringer(UUID id, Stringer stringer) {
    if (!isOwner(id, stringer.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update stringer information");
    }
    stringer.setId(id);
    return stringerRepository.save(stringer);
  }

  @Transactional
  public void deleteStringer(UUID id) {
    Stringer stringer = getStringer(id);
    if (!isOwner(id, stringer.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can delete the stringer");
    }
    stringerRepository.delete(stringer);
  }

  @Transactional
  public List<StringerPrice> addPrice(User creator, UUID stringerId, List<StringerPrice> prices) {
    getStringer(stringerId);

    for (StringerPrice price : prices) {
      price.setSubmittedBy(creator);
      price.setStringerId(stringerId);
    }
    return priceRepository.saveAll(prices);
  }

  @Transactional
  public StringerPrice updatePrice(UUID stringerId, UUID priceId, StringerPrice price) {
    getStringer(stringerId);

    if (!isOwner(stringerId, price.getSubmittedBy().getId())) {
      throw new AccessDeniedException("Only the owner can update price");
    }
    price.setId(priceId);
    price.setStringerId(stringerId);
    return priceRepository.save(price);
  }

  public StringerPrice upvotePrice(UUID priceId) {
    StringerPrice price = priceRepository.findById(priceId)
        .orElseThrow(() -> new EntityNotFoundException("Price not found"));
    price.setUpvotes(price.getUpvotes() + 1);

    return priceRepository.save(price);
  }

  @Transactional
  public StringerPrice upvotePrice(UUID priceId, User creator) {
    StringerPrice price = this.upvotePrice(priceId);

    this.upvoteService.addUpvote(BadmintonEntityType.STRINGER, BadmintonInfoType.PRICE, priceId,
        creator);

    return price;
  }

  public boolean isOwner(UUID stringerId, UUID userId) {
    Stringer stringer = getStringer(stringerId);
    return stringer.getOwner() != null && stringer.getOwner().getId().equals(userId);
  }
}