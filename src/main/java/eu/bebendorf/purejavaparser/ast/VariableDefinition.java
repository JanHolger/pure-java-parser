package eu.bebendorf.purejavaparser.ast;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.purejavaparser.ast.statement.Statement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class VariableDefinition implements Statement {

    List<Annotation> annotations;
    @Getter(AccessLevel.NONE)
    boolean finalModifier;
    Type type;
    Variable variable;
    Expression initializer;

    public boolean isFinal() {
        return finalModifier;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(annotations.size() == 1) {
            sb.append(annotations.get(0)).append(" ");
        } else {
            for(Annotation a : annotations)
                sb.append(a).append("\n");
        }
        if(finalModifier)
            sb.append("final ");
        sb.append(type).append(" ").append(variable);
        if(initializer != null)
            sb.append(" = ").append(initializer);
        return sb.toString();
    }

}
