package eu.bebendorf.purejavaparser.ast;

import eu.bebendorf.purejavaparser.util.PrintUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ClassBody {

    List<FieldDefinition> fields;
    List<ConstructorDefinition> constructors;
    List<MethodDefinition> methods;
    List<TypeDefinition> innerClasses;

    public String toString() {
        return toString(null);
    }

    public String toString(String className) {
        StringBuilder sb = new StringBuilder("{\n\n");
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
