package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Equals implements Expression {

    Expression first;
    Expression second;

    public String toString() {
        return first.toString() + " == " + second.toString();
    }

}
