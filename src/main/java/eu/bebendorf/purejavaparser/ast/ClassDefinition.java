package eu.bebendorf.purejavaparser.ast;

import eu.bebendorf.purejavaparser.util.PrintUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class ClassDefinition implements TypeDefinition {

    List<Annotation> annotations;
    ClassModifiers modifiers;
    String name;
    Type superClass;
    List<Type> interfaces;
    ClassBody body;

    public String toString() {
        String modifiers = this.modifiers.toString();
        if(modifiers.length() > 0)
            modifiers += " ";
        StringBuilder sb = new StringBuilder();
        for(Annotation a : annotations)
            sb.append(a).append("\n");
        sb.append(modifiers).append("class ").append(name).append(" ");
        if(superClass != null)
            sb.append("extends ").append(superClass).append(" ");
        if(interfaces.size() > 0)
            sb.append("implements ").append(interfaces.stream().map(Object::toString).collect(Collectors.joining(", "))).append(" ");
        return sb.append(body).toString();
    }

}
