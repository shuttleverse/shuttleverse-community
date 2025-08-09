package com.shuttleverse.community.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shuttleverse.community.model.SVBaseModel;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SVQueryFactory<T extends SVBaseModel> {

  private final JPAQueryFactory queryFactory;

  public SVQueryFactory(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  public JPAQuery<T> getQuery(
      EntityPathBase<T> model, BooleanExpression predicate) {
    return queryFactory.selectFrom(model).where(predicate);
  }

  public Long getQueryCount(
      EntityPathBase<T> model, BooleanExpression predicate) {
    return queryFactory
        .select(model.count())
        .from(model)
        .where(predicate)
        .fetchOne();
  }

}
