package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Multiplication implements Expression {

    Expression multiplier;
    Expression multiplicand;

    public String toString() {
        return multiplier.toString() + " * " + multiplicand.toString();
    }

}
