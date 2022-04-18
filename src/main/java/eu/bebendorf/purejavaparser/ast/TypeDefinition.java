package eu.bebendorf.purejavaparser.ast;

import eu.bebendorf.purejavaparser.ast.statement.Statement;

public interface TypeDefinition extends Statement {

    default boolean hasStatementEnd() {
        return false;
    }

}
