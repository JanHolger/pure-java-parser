package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class TypedParameterDefinition {

    List<Annotation> annotations;
    Type type;
    Variable variable;

    public String toString() {
        return (annotations.size() > 0 ? (annotations.stream().map(Objects::toString).collect(Collectors.joining(" ")) + " ") : "") + type.toString() + " " + variable.toString();
    }

}
