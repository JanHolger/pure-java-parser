package eu.bebendorf.purejavaparser.ast.statement;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CaseStatement implements Statement {

    Expression value;

    public boolean hasStatementEnd() {
        return false;
    }

    public String toString() {
        if(value == null)
            return "default:";
        return "case " + value + ":";
    }

}
