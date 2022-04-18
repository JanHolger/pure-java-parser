package eu.bebendorf.purejavaparser.ast.statement;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class IfStatement implements Statement {

    Expression condition;
    StatementBlock statement;
    StatementBlock elseStatement;

    public boolean hasStatementEnd() {
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("if (").append(condition.toString()).append(")");
        sb.append(" ").append(statement);
        if (elseStatement != null)
            sb.append(" else ").append(elseStatement);
        return sb.toString();
    }

}
