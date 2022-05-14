package eu.bebendorf.purejavaparser.ast.type;

import eu.bebendorf.purejavaparser.ast.Annotation;
import eu.bebendorf.purejavaparser.ast.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class InterfaceDefinition implements TypeDefinition {

    List<Annotation> annotations;
    ClassModifiers modifiers;
    String name;
    GenericDefinitionList genericDefinitions;
    List<Type> interfaces;
    InterfaceBody body;

    public String toString() {
        String modifiers = this.modifiers.toString();
        if(modifiers.length() > 0)
            modifiers += " ";
        StringBuilder sb = new StringBuilder();
        for(Annotation a : annotations)
            sb.append(a).append("\n");
        sb.append(modifiers).append("interface ").append(name).append(" ");
        if(genericDefinitions != null)
            sb.append(genericDefinitions).append(" ");
        if(interfaces.size() > 0)
            sb.append("extends ").append(interfaces.stream().map(Object::toString).collect(Collectors.joining(", "))).append(" ");
        return sb.append(body).toString();
    }

}
