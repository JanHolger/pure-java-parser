package eu.bebendorf.purejavaparser.ast.type;

import eu.bebendorf.purejavaparser.ast.ImportStatement;
import eu.bebendorf.purejavaparser.ast.PackageStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ClassFileDefinition {

    PackageStatement packageStatement;
    List<ImportStatement> imports;
    List<TypeDefinition> classes;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(packageStatement != null)
            sb.append(packageStatement).append("\n\n");
        if(imports.size() > 0) {
            for(ImportStatement i : imports)
                sb.append(i.toString()).append("\n");
            sb.append("\n");
        }
        for(TypeDefinition c : classes)
            sb.append(c).append("\n\n");
        sb.append("\n");
        return sb.toString();
    }

}
