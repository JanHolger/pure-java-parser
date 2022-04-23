package eu.bebendorf.purejavaparser.ast.type;

import eu.bebendorf.purejavaparser.ast.type.field.FieldDefinition;
import eu.bebendorf.purejavaparser.ast.type.method.MethodDefinition;
import eu.bebendorf.purejavaparser.util.PrintUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class InterfaceBody implements TypeBody {

    List<FieldDefinition> fields;
    List<MethodDefinition> methods;
    List<TypeDefinition> innerClasses;

    public String toString() {
        StringBuilder sb = new StringBuilder("{\n\n");
        if(fields.size() > 0) {
            for(FieldDefinition d : fields)
                sb.append(PrintUtil.prefixLines(d.toString() + ";", "    ")).append("\n");
            sb.append("\n");
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
