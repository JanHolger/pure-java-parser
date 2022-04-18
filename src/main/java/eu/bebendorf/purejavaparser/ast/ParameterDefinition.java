package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ParameterDefinition {

    Variable variable;

    public String toString() {
        return variable.toString();
    }

}
