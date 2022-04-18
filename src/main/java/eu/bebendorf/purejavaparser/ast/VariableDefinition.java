package eu.bebendorf.purejavaparser.ast;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VariableDefinition implements Statement {

    @Getter(AccessLevel.NONE)
    boolean finalModifier;
    Type type;
    Variable variable;
    Expression initializer;

    public boolean isFinal() {
        return finalModifier;
    }

    public String toString() {
        return (finalModifier ? "final " : "") + type.toString() + " " + variable.toString() + (initializer != null ? (" = " + initializer) : "");
    }

}
