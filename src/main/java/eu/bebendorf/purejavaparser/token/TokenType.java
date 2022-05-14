package eu.bebendorf.purejavaparser.token;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public enum TokenType {

    SINGLE_LINE_COMMENT("//[^\n]*", false),
    MULTI_LINE_COMMENT("/\\*[\\s\\S]*?\\*/", false),
    STRING_LITERAL("\"((\\\\\\\\)|(\\\\\")|[^\\n\"])*\"", false),
    CHAR_LITERAL("'((\\\\['nrtbf0])|[^\\n'])'", false),
    HEX_INT_LITERAL("0x[0-9a-fA-F]+(_[0-9a-fA-F]+)*[Ll]?", false),
    BINARY_INT_LITERAL("0b[01]+(_[01]+)*[Ll]?", false),
    DECIMAL_INT_LITERAL("[0-9]+(_[0-9]+)*[LlFfDd]?", false),
    DECIMAL_FLOAT_LITERAL("(([0-9]+(_[0-9]+)*\\.([0-9]+(_[0-9]+)*)?)|(\\.[0-9]+(_[0-9]+)*))[fFdD]?", false),
    BOOLEAN_LITERAL("(true)|(false)", true),
    NULL_LITERAL("null", true),
    CLASS("class", true),
    RETURN("return", true),
    IMPORT("import", true),
    PACKAGE("package", true),
    STATIC("static", true),
    INCREMENTAL_OP("(\\+\\+)|(\\-\\-)", false),
    ARITHMETIC_OP("\\+|\\-|\\*|/|%", false),
    RELATIONAL_OP("\\<|\\>|(<=)|(>=)", false),
    EQUALITY_OP("(==)|(!=)", false),
    TERNARY_OP("\\?", false),
    COLON(":", false),
    ASSIGN_OP("=", false),
    UNARY_OP("~", false),
    NOT_OP("!", false),
    PROPERTY_ACCESS("\\.", false),
    STATEMENT_END(";", false),
    OPEN_CURLY_BRACKET("\\{", false),
    CLOSE_CURLY_BRACKET("\\}", false),
    GROUP_START("\\(", false),
    GROUP_END("\\)", false),
    ARRAY_START("\\[", false),
    ARRAY_END("\\]", false),
    SEPERATOR(",", false),
    NAME("[_A-Za-z$][_A-Za-z0-9$]*", false),
    WHITESPACE("[\\n\\r\\t ]+", false),
    EOF("", false),
    LAMBDA_ARROW("\\->", false),
    VARARGS("\\.\\.\\.", false),
    PUBLIC("public", true),
    PRIVATE("private", true),
    PROTECTED("protected", true),
    NATIVE("native", true),
    SYNCHRONIZED("synchronized", true),
    VOLATILE("volatile", true),
    STRICTFP("strictfp", true),
    FINAL("final", true),
    ENUM("enum", true),
    INTERFACE("interface", true),
    ABSTRACT("abstract", true),
    TRANSIENT("transient", true),
    IF("if", true),
    ELSE("else", true),
    WHILE("while", true),
    DO("do", true),
    FOR("for", true),
    SWITCH("switch", true),
    CASE("case", true),
    DEFAULT("default", true),
    TRY("try", true),
    CATCH("catch", true),
    FINALLY("finally", true),
    BREAK("break", true),
    CONTINUE("continue", true),
    ASSERT("assert", true),
    ANNOTATION("@", false),
    NEW("new", true),
    EXTENDS("extends", true),
    IMPLEMENTS("implements", true),
    INSTANCEOF("instanceof", true),
    THROW("throw", true),
    THROWS("throws", true),
    GOTO("goto", true),
    CONST("const", true),
    LOGICAL_OP("(\\|\\|)|(&&)|\\^", false),
    SHIFT_OP("(\\>\\>\\>)|(\\<\\<)|(\\>\\>)", false),
    BITWISE_OP("\\||&|\\^", false);

    private static final TokenType[] ordered = new TokenType[] {
            // Comments
            SINGLE_LINE_COMMENT,
            MULTI_LINE_COMMENT,
            // Literals
            BOOLEAN_LITERAL,
            NULL_LITERAL,
            HEX_INT_LITERAL,
            BINARY_INT_LITERAL,
            DECIMAL_FLOAT_LITERAL,
            DECIMAL_INT_LITERAL,
            STRING_LITERAL,
            // Package & Import
            PACKAGE,
            IMPORT,
            // Annotation
            ANNOTATION,
            // Modifiers
            PRIVATE,
            PUBLIC,
            PROTECTED,
            STATIC,
            ABSTRACT,
            FINAL,
            VOLATILE,
            TRANSIENT,
            STRICTFP,
            NATIVE,
            SYNCHRONIZED,
            // Class Types
            CLASS,
            ENUM,
            INTERFACE,
            // Class and Method Extensions
            EXTENDS,
            IMPLEMENTS,
            THROWS,
            // Operations
            NEW,
            INSTANCEOF,
            THROW,
            RETURN,
            ASSERT,
            BREAK,
            CONTINUE,
            BITWISE_OP,
            LOGICAL_OP,
            INCREMENTAL_OP,
            ARITHMETIC_OP,
            SHIFT_OP,
            RELATIONAL_OP,
            EQUALITY_OP,
            UNARY_OP,
            NOT_OP,
            ASSIGN_OP,
            TERNARY_OP,
            COLON,
            // Control Flow
            IF,
            ELSE,
            DO,
            WHILE,
            FOR,
            SWITCH,
            CASE,
            DEFAULT,
            // Try
            TRY,
            CATCH,
            FINALLY,
            // Functions
            VARARGS,
            LAMBDA_ARROW,
            SEPERATOR,
            // Groups
            OPEN_CURLY_BRACKET,
            CLOSE_CURLY_BRACKET,
            GROUP_START,
            GROUP_END,
            ARRAY_START,
            ARRAY_END,
            STATEMENT_END,
            PROPERTY_ACCESS,
            // Reserved
            GOTO,
            CONST,
            // Name Literal
            NAME,
            // Whitespace
            WHITESPACE
    };

    final Pattern pattern;

    public static TokenType[] ordered() {
        return ordered;
    }

    TokenType(String pattern, boolean keyword) {
        this.pattern = Pattern.compile("(?<token>" + pattern + ")" + (keyword ? "[^A-Za-z0-9_$]" : "") + "[\\S\\s]*");
    }

}
