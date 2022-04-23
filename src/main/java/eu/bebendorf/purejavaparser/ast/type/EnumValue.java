package eu.bebendorf.purejavaparser.ast.type;

import eu.bebendorf.purejavaparser.ast.Variable;
import eu.bebendorf.purejavaparser.ast.expression.ArgumentList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EnumValue {

    Variable variable;
    ArgumentList arguments;

    public String toString() {
        return variable.getName() + arguments;
    }

}
