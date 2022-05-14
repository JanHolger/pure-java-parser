package eu.bebendorf.purejavaparser.ast.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class GenericDefinitionList {

    List<GenericDefinition> genericDefinitions;

    public String toString() {
        return "<" + genericDefinitions.stream().map(Object::toString).collect(Collectors.joining(", ")) + ">";
    }

}
