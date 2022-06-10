package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LessThan implements Expression {

    Expression first;
    Expression second;

    public String toString() {
        return first.toString() + " < " + second.toString();
    }

}
