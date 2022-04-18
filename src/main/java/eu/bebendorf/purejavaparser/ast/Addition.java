package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Addition implements Expression {

    Expression augend;
    Expression addend;

    public String toString() {
        return augend.toString() + " + " + addend.toString();
    }

}
