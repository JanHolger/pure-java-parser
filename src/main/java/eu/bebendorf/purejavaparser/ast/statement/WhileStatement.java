package eu.bebendorf.purejavaparser.ast.statement;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.purejavaparser.ast.statement.Statement;
import eu.bebendorf.purejavaparser.ast.statement.StatementBlock;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WhileStatement implements Statement {

    Expression condition;
    StatementBlock statement;

    public boolean hasStatementEnd() {
        return false;
    }

    public String toString() {
        return new StringBuilder("while (").append(condition.toString()).append(") ").append(statement).toString();
    }

}
