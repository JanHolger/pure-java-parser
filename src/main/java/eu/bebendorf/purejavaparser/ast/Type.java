package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public class Type implements GenericType {

    List<String> name;
    List<GenericType> genericTypes;
    Type extend;
    int arrayDepth;
    boolean varArgs;

    public String toString() {
        return toComponentString() +
                Stream.of(new String[varArgs ? arrayDepth - 1 : arrayDepth]).map(s -> "[]").collect(Collectors.joining("")) +
                (varArgs ? "..." : "");
    }

    public String toComponentString() {
        return String.join(".", name) +
                (genericTypes.size() > 0 ? ("<" + genericTypes.stream().map(Objects::toString).collect(Collectors.joining(", ")) + ">") : "");
    }

}
