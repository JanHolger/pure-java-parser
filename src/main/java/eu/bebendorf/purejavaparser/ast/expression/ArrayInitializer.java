package eu.bebendorf.purejavaparser.ast.expression;

import eu.bebendorf.purejavaparser.util.PrintUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class ArrayInitializer implements Expression {

    List<Expression> values;

    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        if(values.size() > 0)
            sb.append("\n").append(PrintUtil.prefixLines(values.stream().map(Objects::toString).collect(Collectors.joining(",\n")), "    ")).append("\n");
        return sb.append("}").toString();
    }

}
