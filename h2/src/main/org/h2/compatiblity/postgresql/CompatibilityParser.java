package org.h2.compatiblity.postgresql;

import org.h2.expression.Expression;

public interface CompatibilityParser {

    Expression readFunction(String name);

    boolean shouldHandleBinarySum(Expression expression);

    boolean shouldHandleBinaryFactor(Expression expression);

    Expression handleBinaryOperation(String token, Expression left, Expression right);
}
