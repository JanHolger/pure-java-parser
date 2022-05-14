package eu.bebendorf.purejavaparser.ast.expression;

import eu.bebendorf.purejavaparser.ast.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InstanceOf implements Expression {

    Expression value;
    Type type;

    public String toString() {
        return value + " instanceof " + type;
    }

}
