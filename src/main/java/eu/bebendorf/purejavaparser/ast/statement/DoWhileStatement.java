package eu.bebendorf.purejavaparser.ast.statement;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DoWhileStatement implements Statement {

    StatementBlock statement;
    Expression condition;

    public boolean hasStatementEnd() {
        return true;
    }

    public String toString() {
        return new StringBuilder("do").append(" ").append(statement).append(" while (").append(condition.toString()).append(")").toString();
    }

}
