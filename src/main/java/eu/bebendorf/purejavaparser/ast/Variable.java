package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Variable implements Expression, Assignable {

    String name;

    public String toString() {
        return name;
    }

}
