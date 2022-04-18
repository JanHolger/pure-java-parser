package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ClassFileDefinition {

    List<String> packageName;
    List<ImportStatement> imports;
    List<TypeDefinition> classes;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(packageName != null)
            sb.append("package ").append(String.join(".", packageName)).append(";\n\n");
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
