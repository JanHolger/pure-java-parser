package eu.bebendorf.purejavaparser.ast.statement;

import eu.bebendorf.purejavaparser.ast.VariableDefinition;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class TryStatement implements Statement {

    List<VariableDefinition> resources;
    StatementBlock tryStatement;
    List<CatchStatement> catchStatements;
    StatementBlock finallyStatement;

    public boolean hasStatementEnd() {
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("try ");
        if(resources.size() > 0)
            sb.append("(").append(resources.stream().map(Object::toString).collect(Collectors.joining("; "))).append(") ");
        sb.append(tryStatement);
        for(CatchStatement c : catchStatements)
            sb.append(" ").append(c);
        if(finallyStatement != null)
            sb.append(" finally ").append(finallyStatement);
        return sb.toString();
    }

}
