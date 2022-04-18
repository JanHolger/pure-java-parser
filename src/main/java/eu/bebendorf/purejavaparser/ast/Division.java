package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Division implements Expression {

    Expression dividend;
    Expression divisor;

    public String toString() {
        return dividend.toString() + " % " + divisor.toString();
    }

}
