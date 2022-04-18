package eu.bebendorf.purejavaparser.ast.statement;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Assignment implements Statement {

    Assignable destination;
    Expression value;

    public String toString() {
        return destination.toString() + " = " + value.toString();
    }

}
