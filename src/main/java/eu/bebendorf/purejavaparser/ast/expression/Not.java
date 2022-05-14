package eu.bebendorf.purejavaparser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Not implements Expression {

    Expression inner;

    public String toString() {
        return "!" + inner.toString();
    }

}
