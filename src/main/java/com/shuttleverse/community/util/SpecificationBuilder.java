package com.shuttleverse.community.util;

import com.shuttleverse.community.constants.BadmintonEntityType;
import com.shuttleverse.community.constants.BadmintonInfoType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class SpecificationBuilder {

  /**
   * Creates a specification from filter parameters.
   *
   * @param filters Map of field name to filter value
   * @return Specification with all filters applied
   */
  public static <T> Specification<T> buildSpecification(Map<String, String> filters) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      filters.forEach((field, value) -> {
        if (value != null && !value.isEmpty()) {
          if (isUuidField(field)) {
            try {
              UUID uuidValue = UUID.fromString(value);
              predicates.add(criteriaBuilder.equal(root.get(field), uuidValue));
            } catch (IllegalArgumentException ignored) {
              // ignored
            }
          } else if (isEnumField(field)) {
            int type = Integer.parseInt(value);
            predicates.add(criteriaBuilder.equal(root.get(field),
                field.equals("entityType")
                    ? BadmintonEntityType.values()[type].name().toUpperCase()
                    : BadmintonInfoType.values()[type].name().toUpperCase()));
          } else if (value.contains("*") || value.contains("%")) {
            String likeValue = value.replace('*', '%');
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get(field)),
                likeValue.toLowerCase()));
          } else {
            predicates.add(criteriaBuilder.equal(
                criteriaBuilder.lower(root.get(field)),
                value.toLowerCase()));
          }
        }
      });

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  private static boolean isUuidField(String fieldName) {
    return "userId".equals(fieldName) || "entityId".equals(fieldName);
  }

  private static boolean isEnumField(String fieldName) {
    return "entityType".equals(fieldName) || "infoType".equals(fieldName);
  }
}