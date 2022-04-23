package eu.bebendorf.purejavaparser.ast.type.field;

import eu.bebendorf.purejavaparser.ast.Annotation;
import eu.bebendorf.purejavaparser.ast.VariableDefinition;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class FieldDefinition {

    List<Annotation> annotations;
    FieldModifiers modifiers;
    VariableDefinition variableDefinition;

    public String toString() {
        String modifiers = this.modifiers.toString();
        if(modifiers.length() > 0)
            modifiers += " ";
        StringBuilder sb = new StringBuilder();
        for(Annotation a : annotations)
            sb.append(a).append("\n");
        return sb.append(modifiers).append(variableDefinition).toString();
    }

}
