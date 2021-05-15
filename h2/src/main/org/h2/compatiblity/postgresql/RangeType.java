package org.h2.compatiblity.postgresql;

import java.sql.SQLType;

public final class RangeType implements SQLType {

    private String name;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getVendor() {
        return null;
    }

    @Override
    public Integer getVendorTypeNumber() {
        return null;
    }

    public RangeType(String name) {

    }
}
