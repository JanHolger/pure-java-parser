package eu.bebendorf.purejavaparser.ast;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.purejavaparser.util.PrintUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class Annotation {

    Type type;
    Map<String, Expression> arguments;

    public String toString() {
        StringBuilder sb = new StringBuilder("@").append(type.toString());
        if(arguments.size() > 0) {
            sb.append("(");
            if(arguments.size() == 1) {
                String key = arguments.keySet().stream().findFirst().get();
                if(key.equals("value"))
                    sb.append(arguments.get("value"));
                else
                    sb.append(key).append(" = ").append(arguments.get(key));
            } else {
                sb
                        .append("\n")
                        .append(PrintUtil.prefixLines(arguments.entrySet().stream().map(e -> e.getKey() + " = " + e.getValue()).collect(Collectors.joining(",\n")), "    "))
                        .append("\n");
            }
            sb.append(")");
        }
        return sb.toString();
    }

}
