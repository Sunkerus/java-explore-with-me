package ru.practicum.main.helper;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QPredicates {

    private final List<Predicate> predicates =  new ArrayList<>();

    public <T> QPredicates add(T object, Function<T, Predicate> function) {
        if (object != null) {
            predicates.add(function.apply(object));
        }

        return this;
    }

    public QPredicates add(BooleanExpression booleanExpression) {
        if (booleanExpression != null) {
            predicates.add(booleanExpression);
        }

        return this;
    }

    public Predicate buildAnd() {
        return ExpressionUtils.allOf(predicates);
    }

    public Predicate buildOr() {
        return ExpressionUtils.anyOf(predicates);
    }

    public static QPredicates build() {
        return new QPredicates();
    }

}
