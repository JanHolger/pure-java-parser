package eu.bebendorf.purejavaparser.ast;

import eu.bebendorf.purejavaparser.util.PrintUtil;
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
