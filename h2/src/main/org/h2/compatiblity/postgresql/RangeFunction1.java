package org.h2.compatiblity.postgresql;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.function.Function1;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class RangeFunction1 extends Function1 {

    public static final int LOWER = 0;

    public static final int UPPER = LOWER + 1;

    private static final String[] NAMES = { //
            "LOWER", "UPPER"
    };

    private final int function;

    public RangeFunction1(Expression arg1, int function) {
        super(arg1);
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session) {
        RangeValue v = (RangeValue) arg.getValue(session);
        switch (function) {
            case LOWER:
                return v.getLowerBound();
            case UPPER:
                return v.getUpperBound();
            default:
                throw DbException.getInternalError("function=" + function);
        }
    }

    @Override
    public Expression optimize(SessionLocal session) {
        arg = arg.optimize(session);
        type = TypeInfo.TYPE_UNKNOWN;
        return this;
    }

    @Override
    public String getName() {
        return NAMES[function];
    }
}
