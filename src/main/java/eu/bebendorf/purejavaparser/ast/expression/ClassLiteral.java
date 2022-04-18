package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ClassLiteral implements Expression {

    List<String> name;

    public String toString() {
        return String.join(".", name) + ".class";
    }

}
