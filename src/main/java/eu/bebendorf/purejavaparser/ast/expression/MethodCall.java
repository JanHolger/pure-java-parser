package eu.bebendorf.purejavaparser.ast.expression;

import eu.bebendorf.purejavaparser.ast.statement.Statement;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MethodCall implements Expression, Statement {

    Expression function;
    ArgumentList arguments;

    public String toString() {
        return function.toString() + arguments.toString();
    }

}
