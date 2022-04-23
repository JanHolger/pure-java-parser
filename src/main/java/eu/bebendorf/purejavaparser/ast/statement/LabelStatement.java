package eu.bebendorf.purejavaparser.ast.statement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LabelStatement implements Statement {

    String name;

    public boolean hasStatementEnd() {
        return false;
    }

    public String toString() {
        return name + ":";
    }

}
