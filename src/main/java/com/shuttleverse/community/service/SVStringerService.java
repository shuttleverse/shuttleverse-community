package com.shuttleverse.community.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.constants.SVSortType;
import com.shuttleverse.community.dto.SVLocationDto;
import com.shuttleverse.community.mapper.SVMapStructMapper;
import com.shuttleverse.community.model.SVStringer;
import com.shuttleverse.community.model.SVStringerPrice;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.params.SVBoundingBoxParams;
import com.shuttleverse.community.params.SVEntityFilterParams;
import com.shuttleverse.community.params.SVSortParams;
import com.shuttleverse.community.params.SVStringerCreationData;
import com.shuttleverse.community.params.SVWithinDistanceParams;
import com.shuttleverse.community.query.SVQueryFactory;
import com.shuttleverse.community.query.SVQueryModel;
import com.shuttleverse.community.repository.SVStringerPriceRepository;
import com.shuttleverse.community.repository.SVStringerRepository;
import com.shuttleverse.community.util.SVAuthenticationUtils;
import com.shuttleverse.community.util.SVQueryUtils;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SVStringerService {

  private final SVMapStructMapper mapper;
  private final SVStringerRepository stringerRepository;
  private final SVStringerPriceRepository priceRepository;
  private final SVUpvoteService upvoteService;
  private final SVQueryFactory<SVStringer> queryFactory;

  @Transactional
  public SVStringer createStringer(SVStringer stringer, SVUser creator) {
    stringer.setCreator(creator);
    return stringerRepository.save(stringer);
  }

  public SVStringer getStringer(UUID id) {
    return stringerRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Stringer not found with id: " + id));
  }

  @Transactional(readOnly = true)
  public Page<SVStringer> getAllStringers(SVEntityFilterParams params, SVSortParams sortParams,
      Pageable pageable) {
    List<UUID> ids = findStringersByPrice(params);

    BooleanExpression predicate = SVQueryModel.stringer.id.in(ids);

    if (params.getIsVerified() != null) {
      predicate = predicate.and(SVQueryModel.stringer.owner.isNotNull());
    }

    if (sortParams.getSortType() != SVSortType.LOCATION) {
      Pageable sortedPageable = PageRequest.of(
          pageable.getPageNumber(),
          pageable.getPageSize(),
          sortParams.getSortDirection().toPagableDirection(),
          sortParams.getSortType().toPageableProperty()
      );
      return stringerRepository.findAll(predicate, sortedPageable);
    }

    JPAQuery<SVStringer> query = queryFactory.getQuery(SVQueryModel.stringer, predicate)
        .orderBy(SVQueryUtils.orderByDistance(SVQueryModel.stringer.locationPoint,
            mapper.locationDtoToPoint(new SVLocationDto(sortParams.getLongitude(),
                sortParams.getLatitude())), sortParams.getSortDirection()));

    Long queryCount = queryFactory.getQueryCount(SVQueryModel.stringer, predicate);

    return PageableExecutionUtils.getPage(
        query.offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch(),
        pageable,
        () -> queryCount
    );
  }

  private List<UUID> findStringersByPrice(SVEntityFilterParams params) {
    Set<UUID> result = new HashSet<>();
    BooleanExpression pricePredicate = SVQueryModel.stringerPrice.price.between(
        params.getMinPrice(),
        params.getMaxPrice());
    priceRepository.findAll(pricePredicate).forEach(price -> {
      result.add(price.getStringerId());
    });

    return new ArrayList<>(result);
  }

  @Transactional(readOnly = true)
  public Page<SVStringer> getCourtsByBoundingBox(SVBoundingBoxParams params, Pageable pageable) {
    return stringerRepository.findWithinBounds(params.getMinLon(), params.getMinLat(),
        params.getMaxLon(), params.getMaxLat(), pageable);
  }

  @Transactional(readOnly = true)
  public Page<SVStringer> getStringersWithinDistance(SVWithinDistanceParams params,
      Pageable pageable) {
    return stringerRepository.findWithinDistance(params.getLocation(), params.getDistance(),
        pageable);
  }

  @Transactional
  public SVStringer updateStringer(UUID id, SVStringerCreationData data) {
    SVStringer stringer = stringerRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Stringer not found"));

    mapper.updateStringerFromDto(data, stringer);

    return stringerRepository.save(stringer);
  }

  @Transactional
  public void deleteStringer(UUID id) {
    SVStringer stringer = getStringer(id);
    if (!isOwner(id, stringer.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can delete the stringer");
    }
    stringerRepository.delete(stringer);
  }

  @Transactional
  public List<SVStringerPrice> addPrice(SVUser creator, UUID stringerId,
      List<SVStringerPrice> prices) {
    getStringer(stringerId);

    for (SVStringerPrice price : prices) {
      price.setSubmittedBy(creator);
      price.setStringerId(stringerId);
    }
    return priceRepository.saveAll(prices);
  }

  @Transactional
  public SVStringerPrice updatePrice(UUID stringerId, UUID priceId, SVStringerPrice price) {
    if (!isOwner(stringerId, price.getSubmittedBy().getId())) {
      throw new AccessDeniedException("Only the owner can update price");
    }
    price.setId(priceId);
    price.setStringerId(stringerId);
    return priceRepository.save(price);
  }

  @Transactional
  public SVStringerPrice upvotePrice(UUID priceId, SVUser creator) {
    SVStringerPrice price = this.upvotePrice(priceId);

    this.upvoteService.addUpvote(SVEntityType.STRINGER, SVInfoType.PRICE, priceId,
        creator);

    return price;
  }

  @Transactional
  public SVStringerPrice upvotePrice(UUID priceId) {
    SVStringerPrice price = priceRepository.findById(priceId)
        .orElseThrow(() -> new EntityNotFoundException("Price not found"));
    price.setUpvotes(price.getUpvotes() + 1);

    return priceRepository.save(price);
  }

  public boolean isSessionUserOwner(String stringerId) {
    UUID stringerUuid = UUID.fromString(stringerId);
    SVStringer stringer = getStringer(stringerUuid);
    UUID userId = SVAuthenticationUtils.getCurrentUser().getId();
    return stringer.getOwner().getId().equals(userId);
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean isOwner(UUID stringerId, UUID userId) {
    SVStringer stringer = getStringer(stringerId);
    return stringer.getOwner().getId().equals(userId);
  }

}