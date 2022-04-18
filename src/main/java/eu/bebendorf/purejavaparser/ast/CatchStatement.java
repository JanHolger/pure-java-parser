package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class CatchStatement {

    List<Type> types;
    Variable variable;
    StatementBlock statement;

    public String toString() {
        return "catch (" + types.stream().map(Objects::toString).collect(Collectors.joining(" | ")) + " " + variable + ") " + statement;
    }

}
