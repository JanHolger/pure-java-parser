package eu.bebendorf.purejavaparser.ast.statement;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReturnStatement implements Statement {

    Expression value;

    public String toString() {
        return "return" + (value != null ? (" " + value) : "");
    }

}
