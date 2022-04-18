package eu.bebendorf.purejavaparser.ast;

public interface TypeDefinition extends Statement {

    default boolean hasStatementEnd() {
        return false;
    }

}
