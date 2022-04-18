package eu.bebendorf.purejavaparser.ast;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BooleanLiteral implements Expression {

    public static final BooleanLiteral TRUE = new BooleanLiteral(true);
    public static final BooleanLiteral FALSE = new BooleanLiteral(false);

    boolean value;

    public String toString() {
        return String.valueOf(value);
    }

}
