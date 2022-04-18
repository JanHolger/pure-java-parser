package eu.bebendorf.purejavaparser.ast;

import eu.bebendorf.purejavaparser.util.PrintUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class StatementBlock implements Statement {

    List<Statement> statements;

    public String toString() {
        return "{\n" + statements.stream().map(s -> PrintUtil.prefixLines(s.toString(), "    ") + (s.hasStatementEnd() ? ";" : "")).collect(Collectors.joining("\n")) + (statements.size() == 0 ? "    " : "") + "\n}";
    }

}
