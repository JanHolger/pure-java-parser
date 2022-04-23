package eu.bebendorf.purejavaparser.ast.statement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BreakStatement implements Statement {

    String label;

    public String toString() {
        return "break" + (label != null ? (" " + label) : "");
    }

}
