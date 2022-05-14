package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Ternary implements Expression {

    Expression condition;
    Expression value;
    Expression elseValue;

    public String toString() {
        return condition.toString() + " ? " + value.toString() + " : " + elseValue.toString();
    }

}
