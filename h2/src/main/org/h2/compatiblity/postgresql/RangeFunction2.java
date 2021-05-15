package org.h2.compatiblity.postgresql;

import org.h2.constraint.Constraint;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.expression.function.Function2;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.value.*;

import java.util.Arrays;

public class RangeFunction2 extends Function2 implements RangeExpression {

    public static final int INT4RANGE = 0;

    public static final int INT8RANGE = INT4RANGE + 1;

    public static final int DATERANGE = INT8RANGE + 1;

    private static final String[] NAMES = { //
            "INT4RANGE", "INT8RANGE", "DATERANGE"
    };

    public static RangeValue canonicize(RangeValue rangeValue) {
        Value v1 = rangeValue.getLowerBound();
        Value v2 = rangeValue.getUpperBound();
        if (!rangeValue.isLowerInclusive()) {
            v1 = addOneDiscreteTo(v1);
        }
        if (rangeValue.isUpperInclusive()) {
            v2 = addOneDiscreteTo(v2);
        }
        return new RangeValue(v1, v2);
    }

    private static Value addOneDiscreteTo(Value v) {
        switch (v.getValueType()) {
            case Value.INTEGER:
                return v.add(ValueInteger.get(1));
            case Value.DATE:
                long dateValue = ((ValueDate) v).getDateValue();
                long oneDay = DateTimeUtils.dateValue(0, 0, 1);
                return ValueDate.fromDateValue(dateValue + oneDay);
            default:
                return v;
        }
    }

    private final int function;
    private final Expression boundFlags;

    public RangeFunction2(Expression arg1, Expression arg2, Expression arg3, int function) {
        super(arg1, arg2);
        this.boundFlags = arg3;
        this.function = function;
    }

    public int getFunction() {
        return function;
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value v1 = left.getValue(session);
        Value v2 = right.getValue(session);
        boolean lowerInclusive = true;
        boolean upperInclusive = false;
        if (boundFlags != null) {
            String bounds = boundFlags.getValue(session).getString();
            switch (bounds) {
                case "[)":
                    lowerInclusive = true;
                    upperInclusive = false;
                    break;
                case "[]":
                    lowerInclusive = true;
                    upperInclusive = true;
                    break;
                case "()":
                    lowerInclusive = false;
                    upperInclusive = false;
                    break;
                case "(]":
                    lowerInclusive = false;
                    upperInclusive = true;
                    break;
                default:
                    throw new IllegalArgumentException("invalid range bound flags Hint: Valid values are \"[]\", \"[)\", \"(]\", and \"()\"");
            }
        }
        RangeValue rangeValue;
        switch (function) {
        case DATERANGE:
                String v1String = v1.getString();
                String v2String = v2.getString();
                v1 = ValueDate.fromDateValue(DateTimeUtils.parseDateValue(v1String, 0, v1String.length()));
                v2 = ValueDate.fromDateValue(DateTimeUtils.parseDateValue(v2String, 0, v2String.length()));
        case INT4RANGE:
        case INT8RANGE:
            rangeValue = canonicize(new RangeValue(v1, v2, lowerInclusive, upperInclusive));
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
        if (session.compare(v1, v2) > 0) {
            throw new IllegalArgumentException("range lower bound must be less than or equal to range upper bound");
        } else if (session.compare(rangeValue.getLowerBound(), rangeValue.getUpperBound()) == 0
                && rangeValue.isLowerInclusive() && !rangeValue.isUpperInclusive()) {
            return new RangeValue();
        }
        return rangeValue;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        left = left.optimize(session);
        right = right.optimize(session);
        type = TypeInfo.TYPE_VARCHAR;
        return this;
    }

    @Override
    public String getName() {
        return NAMES[function];
    }
}
