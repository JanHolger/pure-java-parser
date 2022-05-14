package eu.bebendorf.purejavaparser.ast.type;

import eu.bebendorf.purejavaparser.ast.statement.StatementBlock;
import eu.bebendorf.purejavaparser.ast.type.field.FieldDefinition;
import eu.bebendorf.purejavaparser.ast.type.method.ConstructorDefinition;
import eu.bebendorf.purejavaparser.ast.type.method.MethodDefinition;
import eu.bebendorf.purejavaparser.util.PrintUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ClassBody implements TypeBody {

    List<StatementBlock> staticInitializers;
    List<StatementBlock> initializers;
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
        for(StatementBlock i : staticInitializers)
            sb.append(PrintUtil.prefixLines("static " + i.toString(), "    ")).append("\n\n");
        for(StatementBlock i : initializers)
            sb.append(PrintUtil.prefixLines(i.toString(), "    ")).append("\n\n");
        for(ConstructorDefinition d : constructors)
            sb.append(PrintUtil.prefixLines(d.toString(className), "    ")).append("\n\n");
        for(MethodDefinition d : methods)
            sb.append(PrintUtil.prefixLines(d.toString(), "    ")).append("\n\n");
        for(TypeDefinition d : innerClasses)
            sb.append(PrintUtil.prefixLines(d.toString(), "    ")).append("\n\n");
        return sb.append("}").toString();
    }

}
