package eu.bebendorf.purejavaparser.ast.statement;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SwitchStatement implements Statement {

    Expression value;
    SwitchBlock body;

    public boolean hasStatementEnd() {
        return false;
    }

    public String toString() {
        return "switch (" + value + ") " + body;
    }

}
