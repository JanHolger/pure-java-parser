package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LogicalAnd implements Expression {

    Expression left;
    Expression right;

    public String toString() {
        return left.toString() + " && " + right.toString();
    }

}
