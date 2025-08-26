package com.shuttleverse.community.resolver;

import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.model.SVBaseModel;
import com.shuttleverse.community.repository.SVBaseRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SVBaseEntityResolver {

  private final Map<SVEntityType, SVBaseRepository<? extends SVBaseModel>>
      repositories;

  public SVBaseEntityResolver(
      List<SVBaseRepository<? extends SVBaseModel>> repositoryList) {
    this.repositories = repositoryList.stream()
        .filter(repo -> repo.getEntityType() != null)
        .collect(Collectors.toMap(
            SVBaseRepository::getEntityType,
            Function.identity()
        ));
  }

  public SVBaseModel findById(UUID id, SVEntityType entityType) {
    return this.repositories.get(entityType).findById(id).orElse(null);
  }

}