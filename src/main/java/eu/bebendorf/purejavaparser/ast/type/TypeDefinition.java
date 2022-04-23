package eu.bebendorf.purejavaparser.ast.type;

import eu.bebendorf.purejavaparser.ast.statement.Statement;

public interface TypeDefinition extends Statement {

    default boolean hasStatementEnd() {
        return false;
    }

}
