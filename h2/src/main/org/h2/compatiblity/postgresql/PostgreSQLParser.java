package org.h2.compatiblity.postgresql;

import org.h2.command.Parser;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.function.StringFunction1;
import org.h2.value.Value;

public class PostgreSQLParser implements CompatibilityParser {

    private SessionLocal session;
    private Parser parser;

    public PostgreSQLParser(SessionLocal session, Parser parser) {
        this.session = session;
        this.parser = parser;
    }

    public Expression readFunction(String name) {
        Expression arg;
        switch(name) {
            case "INT4RANGE":
                return new RangeFunction2(parser.readExpression(), parser.readNextArgument(), parser.readIfArgument(), RangeFunction2.INT4RANGE);
            case "DATERANGE":
                return new RangeFunction2(parser.readExpression(), parser.readNextArgument(), parser.readIfArgument(), RangeFunction2.DATERANGE);
            case "LOWER":
                arg = parser.readSingleArgument();
                if (isRangeValue(arg)) {
                    return new RangeFunction1(arg, RangeFunction1.LOWER);
                } else {
                    return new StringFunction1(arg, StringFunction1.LOWER);
                }
            case "UPPER":
                arg = parser.readSingleArgument();
                if (isRangeValue(arg)) {
                    return new RangeFunction1(arg, RangeFunction1.UPPER);
                } else {
                    return new StringFunction1(arg, StringFunction1.UPPER);
                }
            default:
                return null;
        }
    }

    public boolean isRangeValue(Expression expression) {
        return expression instanceof RangeExpression;
    }

    public boolean isRangeValue(Value value) {
        return value instanceof RangeValue;
    }

    public boolean shouldHandleBinaryFactor(Expression expression) {
        return isRangeValue(expression);
    }

    public boolean shouldHandleBinarySum(Expression expression) {
        return isRangeValue(expression);
    }

    public Expression handleBinaryOperation(String token, Expression left, Expression right) {
        return new RangeOperation2(token, left, right);
    }

}
