package eu.bebendorf.purejavaparser.ast;

import eu.bebendorf.purejavaparser.ast.statement.StatementBlock;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class MethodDefinition {

    MethodModifiers modifiers;
    Type type;
    Variable variable;
    TypedParameterList parameters;
    List<Type> throwables;
    StatementBlock body;

    public String toString() {
        String modifiers = this.modifiers.toString();
        if(modifiers.length() > 0)
            modifiers += " ";
        return modifiers +
                type.toString() + " " +
                variable.toString() +
                parameters.toString() + " " +
                (throwables.size() > 0 ? ("throws " + throwables.stream().map(Object::toString).collect(Collectors.joining(", ")) + " ") : " ") +
                body.toString();
    }

}
