package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FieldDefinition {

    FieldModifiers modifiers;
    VariableDefinition variableDefinition;

    public String toString() {
        String modifiers = this.modifiers.toString();
        if(modifiers.length() > 0)
            modifiers += " ";
        return modifiers + variableDefinition.toString();
    }

}
