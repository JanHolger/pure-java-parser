package eu.bebendorf.purejavaparser.ast;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.purejavaparser.ast.statement.StatementBlock;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Lambda implements Expression {

    ParameterList parameters;
    StatementBlock body;

    public String toString() {
        return parameters.toString() + " -> " + body.toString();
    }

}
