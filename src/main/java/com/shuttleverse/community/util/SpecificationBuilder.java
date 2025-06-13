package com.shuttleverse.community.util;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;

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
          if (value.contains("*") || value.contains("%")) {
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
}