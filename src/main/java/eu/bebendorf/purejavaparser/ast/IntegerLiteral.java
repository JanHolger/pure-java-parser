package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@AllArgsConstructor
@Getter
public class IntegerLiteral implements Expression {

    BigInteger value;

    public String toString() {
        return value.toString();
    }

}
