package eu.bebendorf.purejavaparser.ast.type;

import eu.bebendorf.purejavaparser.ast.type.field.FieldDefinition;
import eu.bebendorf.purejavaparser.ast.type.method.ConstructorDefinition;
import eu.bebendorf.purejavaparser.ast.type.method.MethodDefinition;
import eu.bebendorf.purejavaparser.util.PrintUtil;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class EnumBody extends ClassBody {

    final List<EnumValue> values;

    public EnumBody(List<EnumValue> values, List<FieldDefinition> fields, List<ConstructorDefinition> constructors, List<MethodDefinition> methods, List<TypeDefinition> innerClasses) {
        super(fields, constructors, methods, innerClasses);
        this.values = values;
    }

    public String toString(String className) {
        StringBuilder sb = new StringBuilder("{\n\n");
        sb.append(PrintUtil.prefixLines(values.stream().map(Objects::toString).collect(Collectors.joining(",\n")) + ";", "    ")).append("\n\n");
        if(fields.size() > 0) {
            for(FieldDefinition d : fields)
                sb.append(PrintUtil.prefixLines(d.toString() + ";", "    ")).append("\n");
            sb.append("\n");
        }
        for(ConstructorDefinition d : constructors) {
            sb.append(PrintUtil.prefixLines(d.toString(className), "    ")).append("\n\n");
        }
        for(MethodDefinition d : methods) {
            sb.append(PrintUtil.prefixLines(d.toString(), "    ")).append("\n\n");
        }
        for(TypeDefinition d : innerClasses) {
            sb.append(PrintUtil.prefixLines(d.toString(), "    ")).append("\n\n");
        }
        return sb.append("}").toString();
    }

}
