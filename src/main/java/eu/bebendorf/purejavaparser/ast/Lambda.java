package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Lambda implements Expression {

    ParameterList parameters;
    StatementBlock body;

    public String toString() {
        return parameters.toString() + " -> " + body.toString();
    }

}
