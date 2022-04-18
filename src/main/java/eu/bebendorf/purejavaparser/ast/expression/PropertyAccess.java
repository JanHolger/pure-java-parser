package eu.bebendorf.purejavaparser.ast.expression;

import eu.bebendorf.purejavaparser.ast.statement.Assignable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PropertyAccess implements Expression, Assignable {

    Expression expression;
    String property;

    public String toString() {
        return expression.toString() + "." + property;
    }

}
