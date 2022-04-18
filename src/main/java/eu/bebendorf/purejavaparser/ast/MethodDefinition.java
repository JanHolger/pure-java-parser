package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MethodDefinition {

    MethodModifiers modifiers;
    Type type;
    Variable variable;
    TypedParameterList parameters;
    StatementBlock body;

    public String toString() {
        String modifiers = this.modifiers.toString();
        if(modifiers.length() > 0)
            modifiers += " ";
        return modifiers + type.toString() + " " + variable.toString() + " " + parameters.toString() + " " + body.toString();
    }

}
