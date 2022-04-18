package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class ParameterList {

    List<ParameterDefinition> parameters;

    public String toString() {
        return "(" + parameters.stream().map(Objects::toString).collect(Collectors.joining(", ")) + ")";
    }

}
