package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Comparison implements Expression {

    String comparator;
    Expression first;
    Expression second;

    public String toString() {
        return first.toString() + " " + comparator + " " + second.toString();
    }

}
