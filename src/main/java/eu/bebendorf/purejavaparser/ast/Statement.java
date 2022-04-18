package eu.bebendorf.purejavaparser.ast;

public interface Statement {

    default boolean hasStatementEnd() {
        return true;
    }

    default String toFullString() {
        return toString() + (hasStatementEnd() ? ";" : "");
    }

}
