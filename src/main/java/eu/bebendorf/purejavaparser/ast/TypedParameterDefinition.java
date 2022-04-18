package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TypedParameterDefinition {

    Type type;
    Variable variable;

    public String toString() {
        return type.toString() + " " + variable.toString();
    }

}
