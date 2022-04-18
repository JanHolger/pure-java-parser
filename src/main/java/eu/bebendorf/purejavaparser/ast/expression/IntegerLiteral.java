package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@AllArgsConstructor
@Getter
public class IntegerLiteral implements Expression {

    public static IntegerLiteral fromString(String s) {
        BigInteger i;
        if(s.startsWith("0x"))
            i = new BigInteger(s.substring(2), 16);
        else if(s.startsWith("0b"))
            i = new BigInteger(s.substring(2), 2);
        else
            i = new BigInteger(s);
        try {
            return new IntegerLiteral(i.intValueExact());
        } catch (NumberFormatException | ArithmeticException ex) {
            return null;
        }
    }

    int value;

    public String toString() {
        return String.valueOf(value);
    }

}
