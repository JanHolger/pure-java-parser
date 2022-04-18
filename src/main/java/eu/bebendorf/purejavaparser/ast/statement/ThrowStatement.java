package eu.bebendorf.purejavaparser.ast.statement;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ThrowStatement implements Statement {

    Expression value;

    public String toString() {
        return "throw " + value;
    }

}
