package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MethodCall implements Expression, Statement {

    Expression function;
    ArgumentList arguments;

    public String toString() {
        return function.toString() + arguments.toString();
    }

}
