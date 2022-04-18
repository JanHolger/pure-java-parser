package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PackageStatement {

    List<Annotation> annotations;
    List<String> name;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Annotation a : annotations)
            sb.append(a).append("\n");
        return sb.append("package ").append(String.join(".", name)).append(";").toString();
    }

}
