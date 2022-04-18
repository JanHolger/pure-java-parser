package eu.bebendorf.purejavaparser.ast;

import eu.bebendorf.purejavaparser.util.PrintUtil;
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
