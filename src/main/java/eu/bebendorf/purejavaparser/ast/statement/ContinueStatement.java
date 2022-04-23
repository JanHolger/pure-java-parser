package eu.bebendorf.purejavaparser.ast.statement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ContinueStatement implements Statement {

    String label;

    public String toString() {
        return "continue" + (label != null ? (" " + label) : "");
    }

}
