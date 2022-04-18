package eu.bebendorf.purejavaparser.ast.statement;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AssertStatement implements Statement {

    Expression assertion;

    public String toString() {
        return "assert " + assertion;
    }

}
