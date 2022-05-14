package eu.bebendorf.purejavaparser.ast.type;

import eu.bebendorf.purejavaparser.ast.GenericType;
import eu.bebendorf.purejavaparser.ast.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GenericDefinition implements GenericType {

    String name;
    Type extend;

    public String toString() {
        return name + (extend != null ? (" extends " + extend) : "");
    }

}
