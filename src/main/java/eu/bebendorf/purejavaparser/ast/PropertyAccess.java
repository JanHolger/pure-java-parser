package eu.bebendorf.purejavaparser.ast;

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
