package org.h2.compatiblity.postgresql;

import org.h2.command.Parser;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.value.Value;
import org.h2.value.ValueVarchar;

public class RangeTypeUtil {

    public static boolean isRangeType(String input) {
        if (input == "empty") {
            return true;
        }
        String split[] = input.split(",");
        if (split.length != 2) {
            return false;
        }
        String opening = input.substring(0, 1);
        String closing = input.substring(input.length() - 1);
        String lowerBound = split[0].substring(1);
        String upperBound = split[1].substring(0, split[1].length() - 1);
        Parser p = new Parser();
        Expression lowerExp = p.parseExpression(lowerBound);
        Expression upperExp = p.parseExpression(upperBound);
        return lowerExp.getType() == upperExp.getType() &&
                (opening == "[" || opening == "(") &&
                (closing == "]" || closing == ")");
    }

    public static RangeValue varcharToRangeValue(ValueVarchar value, SessionLocal session) {
        String raw = value.getString();
        String split[] = raw.split(",");
        String opening = raw.substring(0, 1);
        String closing = raw.substring(raw.length() - 1);
        String lowerBound = split[0].substring(1);
        String upperBound = split[1].substring(0, split[1].length() - 1);

        Parser p = new Parser();
        Value lowerValue = p.parseExpression(lowerBound).getValue(session);
        Value upperValue = p.parseExpression(upperBound).getValue(session);
        System.out.println("testing lower: " + lowerBound + " " + lowerValue.getString() + " " + lowerValue.getType());
        System.out.println("testing upper: " + upperBound + " " + upperValue.getString() + " " + upperValue.getType());

        return new RangeValue(lowerValue, upperValue, opening == "[", closing == "]");
    }
}
