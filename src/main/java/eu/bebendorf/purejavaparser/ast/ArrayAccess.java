package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ArrayAccess implements Expression, Assignable {

    Expression array;
    Expression index;

    public String toString() {
        return array.toString() + "[" + index.toString() + "]";
    }

}
