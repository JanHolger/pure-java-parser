package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class DoubleLiteral implements Expression {

    public static DoubleLiteral fromString(String s) {
        BigDecimal d = new BigDecimal(s);
        try {
            return new DoubleLiteral(d.doubleValue());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    double value;

    public String toString() {
        return String.valueOf(value);
    }

}
