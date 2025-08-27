package com.shuttleverse.community.resolver;

import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.model.SVBaseUpvotable;
import com.shuttleverse.community.repository.SVUpvotableRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SVUpvotableEntityResolver {

  private final Map<SVEntityType, Map<SVInfoType, SVUpvotableRepository<? extends SVBaseUpvotable>>>
      repositories;

  public SVUpvotableEntityResolver(
      List<SVUpvotableRepository<? extends SVBaseUpvotable>> repositoryList) {
    this.repositories = repositoryList.stream()
        .filter(repo -> repo.getEntityType() != null)
        .collect(Collectors.groupingBy(
            SVUpvotableRepository::getEntityType,
            Collectors.toMap(
                SVUpvotableRepository::getInfoType,
                Function.identity()
            )
        ));
  }

  public SVBaseUpvotable findById(UUID id, SVEntityType entityType, SVInfoType infoType) {
    return this.repositories.get(entityType).get(infoType).findById(id).orElse(null);
  }

}
