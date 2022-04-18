package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class FloatLiteral implements Expression {

    BigDecimal value;

    public String toString() {
        return value.toString();
    }

}
