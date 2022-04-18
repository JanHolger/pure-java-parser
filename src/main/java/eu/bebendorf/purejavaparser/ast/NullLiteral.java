package eu.bebendorf.purejavaparser.ast;

public class NullLiteral implements Expression {

    public static final NullLiteral INSTANCE = new NullLiteral();

    private NullLiteral() {}

    public String toString() {
        return "null";
    }

}
