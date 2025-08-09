package com.shuttleverse.community.util;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.shuttleverse.community.constants.SVSortDirection;
import org.locationtech.jts.geom.Point;

public class SVQueryUtils {

  public static OrderSpecifier<?> orderByDistance(ComparablePath<Point> queryLocation,
      Point location, SVSortDirection sortDirection) {

    NumberTemplate<Double> expression = Expressions.numberTemplate(Double.class,
        "ST_Distance({0}, {1})",
        queryLocation,
        ConstantImpl.create(location)
    );

    return sortDirection == SVSortDirection.ASC ? expression.asc() : expression.desc();
  }
}
