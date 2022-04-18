package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExpressionGroup implements Expression {

    Expression expression;

    public String toString() {
        return "(" + expression.toString() + ")";
    }

}
