package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class FloatLiteral implements Expression {

    public static FloatLiteral fromString(String s) {
        BigDecimal d = new BigDecimal(s);
        try {
            return new FloatLiteral(d.floatValue());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    float value;

    public String toString() {
        return value + "f";
    }

}
