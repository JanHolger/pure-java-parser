package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Subtraction implements Expression {

    Expression minuend;
    Expression subtrahend;

    public String toString() {
        return minuend.toString() + " - " + subtrahend.toString();
    }

}
