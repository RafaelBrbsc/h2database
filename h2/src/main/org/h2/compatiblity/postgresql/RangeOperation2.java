package org.h2.compatiblity.postgresql;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Operation2;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class RangeOperation2 extends Operation2 implements RangeExpression {

    public enum RangeOpType {
        UNION,
        INTERSECTION,
        DIFFERENCE
    }

    private RangeOpType opType;

    public RangeOperation2(RangeOpType opType, Expression left, Expression right) {
        super(left, right);
        this.opType = opType;
    }

    public RangeOperation2(String opType, Expression left, Expression right) {
        super(left, right);
        this.opType = getOperationEnum(opType);
    }

    @Override
    public Value getValue(SessionLocal session) {
        try {
            RangeValue leftValue = (RangeValue) left.getValue(session);
            RangeValue rightValue = (RangeValue) right.getValue(session);
            switch(opType) {
                case UNION:
                    if (leftValue.isEmpty()) {
                        return right.getValue(session);
                    } else if (rightValue.isEmpty()) {
                        return left.getValue(session);
                    } else {
                        int comparison = session.compare(leftValue.getLowerBound(), rightValue.getLowerBound());
                        Value min = comparison < 0 ? leftValue.getLowerBound() : rightValue.getLowerBound();
                        comparison = session.compare(leftValue.getUpperBound(), rightValue.getUpperBound());
                        Value max = comparison > 0 ? leftValue.getUpperBound() : rightValue.getUpperBound();
                        return new RangeValue(min, max);
                    }
                case INTERSECTION:
                    if (leftValue.isEmpty() || rightValue.isEmpty() || !rangeOverlaps(session, leftValue, rightValue)) {
                        return new RangeValue();
                    } else {
                        int comparison = session.compare(leftValue.getLowerBound(), rightValue.getLowerBound());
                        Value min = comparison < 0 ? rightValue.getLowerBound() : leftValue.getLowerBound();
                        comparison = session.compare(leftValue.getUpperBound(), rightValue.getUpperBound());
                        Value max = comparison > 0 ? rightValue.getUpperBound() : leftValue.getUpperBound();
                        return new RangeValue(min, max);
                    }
                default:
                    return null;
            }
        } catch (ClassCastException e) {
            throw DbException.getInternalError("not a range value: " + left + " " + opType + " " + right);
        }
    }

    private boolean rangeOverlaps(SessionLocal session, RangeValue leftValue, RangeValue rightValue) {
        int leftLowToRightUpComparison = session.compare(leftValue.getLowerBound(), rightValue.getUpperBound());
        int rightLowToLeftUpComparison = session.compare(rightValue.getLowerBound(), leftValue.getUpperBound());
        if (leftLowToRightUpComparison < 0 && rightLowToLeftUpComparison < 0) {
            return true;
        } else if (leftLowToRightUpComparison == 0) {
            return leftValue.isLowerInclusive() && rightValue.isUpperInclusive();
        } else if (rightLowToLeftUpComparison == 0) {
            return rightValue.isLowerInclusive() && leftValue.isUpperInclusive();
        }
        return false;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        left = left.optimize(session);
        right = right.optimize(session);
        type = TypeInfo.TYPE_VARCHAR;
        return this;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        left.getSQL(builder, sqlFlags, AUTO_PARENTHESES).append(' ').append(getOperationToken()).append(' ');
        return right.getSQL(builder, sqlFlags, AUTO_PARENTHESES);
    }

    private String getOperationToken() {
        switch (opType) {
            case UNION:
                return "+";
            case INTERSECTION:
                return "*";
            case DIFFERENCE:
                return "-";
            default:
                throw DbException.getInternalError("opType=" + opType);
        }
    }

    private RangeOpType getOperationEnum(String operation) {
        switch (operation) {
            case "+":
                return RangeOpType.UNION;
            case "*":
                return RangeOpType.INTERSECTION;
            case "-":
                return RangeOpType.DIFFERENCE;
            default:
                throw DbException.getInternalError("opType=" + operation);
        }
    }
}
