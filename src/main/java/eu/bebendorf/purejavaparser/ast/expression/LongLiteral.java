package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@AllArgsConstructor
@Getter
public class LongLiteral implements Expression {

    public static LongLiteral fromString(String s) {
        BigInteger i;
        if(s.startsWith("0x"))
            i = new BigInteger(s.substring(2), 16);
        else if(s.startsWith("0b"))
            i = new BigInteger(s.substring(2), 2);
        else
            i = new BigInteger(s);
        try {
            return new LongLiteral(i.longValueExact());
        } catch (NumberFormatException | ArithmeticException ex) {
            return null;
        }
    }

    long value;

    public String toString() {
        return value + "l";
    }

}
