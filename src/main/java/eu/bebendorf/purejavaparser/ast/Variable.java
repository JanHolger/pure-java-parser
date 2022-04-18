package eu.bebendorf.purejavaparser.ast;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.purejavaparser.ast.statement.Assignable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Variable implements Expression, Assignable {

    String name;

    public String toString() {
        return name;
    }

}
