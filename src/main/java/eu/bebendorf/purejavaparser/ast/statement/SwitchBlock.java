package eu.bebendorf.purejavaparser.ast.statement;

import eu.bebendorf.purejavaparser.util.PrintUtil;

import java.util.List;
import java.util.stream.Collectors;

public class SwitchBlock extends StatementBlock {

    public SwitchBlock(List<Statement> statements) {
        super(statements);
    }

    public String toString() {
        return "{\n" + statements.stream().map(s -> (s instanceof CaseStatement) ? s.toFullString() : PrintUtil.prefixLines(s.toFullString(), "    ")).map(s -> PrintUtil.prefixLines(s, "    ")).collect(Collectors.joining("\n")) + (statements.size() == 0 ? "    " : "") + "\n}";
    }

}
