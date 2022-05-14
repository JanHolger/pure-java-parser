package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Unary implements Expression {

    Expression value;

    public String toString() {
        return "~" + value.toString();
    }

}
