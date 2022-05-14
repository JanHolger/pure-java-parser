package eu.bebendorf.purejavaparser.ast.type.method;

import eu.bebendorf.purejavaparser.ast.Annotation;
import eu.bebendorf.purejavaparser.ast.Type;
import eu.bebendorf.purejavaparser.ast.statement.StatementBlock;
import eu.bebendorf.purejavaparser.ast.type.GenericDefinition;
import eu.bebendorf.purejavaparser.ast.type.GenericDefinitionList;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class ConstructorDefinition {

    List<Annotation> annotations;
    ConstructorModifiers modifiers;
    GenericDefinitionList genericDefinitions;
    TypedParameterList parameters;
    List<Type> throwables;
    StatementBlock body;

    public String toString() {
        return toString(null);
    }

    public String toString(String className) {
        String modifiers = this.modifiers.toString();
        if(modifiers.length() > 0)
            modifiers += " ";
        StringBuilder sb = new StringBuilder();
        for(Annotation a : annotations)
            sb.append(a).append("\n");
        sb.append(modifiers);
        if(genericDefinitions != null)
            sb.append(genericDefinitions).append(" ");
        sb.append(className == null ? "" : className).append(parameters).append(" ");
        if(throwables.size() > 0)
            sb.append("throws ").append(throwables.stream().map(Object::toString).collect(Collectors.joining(", "))).append(" ");
        return sb.append(body).toString();
    }

}
