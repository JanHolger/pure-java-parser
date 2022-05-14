package eu.bebendorf.purejavaparser.ast.expression;

import eu.bebendorf.purejavaparser.ast.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Cast implements Expression {

    Type type;
    Expression value;

    public String toString() {
        return "(" + type.toString() + ") " + value.toString();
    }

}
