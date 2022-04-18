package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class ArgumentList {

    List<Expression> arguments;

    public String toString() {
        return "(" + arguments.stream().map(Objects::toString).collect(Collectors.joining(", ")) + ")";
    }

}
