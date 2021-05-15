package org.h2.compatiblity.postgresql;

import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public class RangeValue<E extends Value> extends ValueVarchar {

    private E lowerBound;
    private E upperBound;
    private boolean lowerInclusive;
    private boolean upperInclusive;
    private boolean empty;

    public RangeValue() {
        super("empty");
        this.empty = true;
        this.lowerBound = (E) ValueNull.INSTANCE;
        this.upperBound = (E) ValueNull.INSTANCE;
        this.lowerInclusive = true;
        this.upperInclusive = false;
    }

    public RangeValue(E lowerBound, E upperBound) {
        this(lowerBound, upperBound, true, false);
    }

    public RangeValue(E lowerBound, E upperBound, boolean lowerInclusive, boolean upperInclusive) {
        super((lowerInclusive? '[' : '(') + lowerBound.getString() + ',' + upperBound.getString() + (upperInclusive? ']' : ')'));
        empty = false;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.lowerInclusive = lowerInclusive;
        this.upperInclusive = upperInclusive;

    }

    public E getLowerBound() {
        return lowerBound;
    }

    public E getUpperBound() {
        return upperBound;
    }

    public boolean isLowerInclusive() {
        return lowerInclusive;
    }

    public boolean isUpperInclusive() {
        return upperInclusive;
    }

    public boolean isEmpty() {
        return empty;
    }
}
