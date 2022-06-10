package eu.bebendorf.purejavaparser.token;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class TokenType {

    public static final TokenType SINGLE_LINE_COMMENT = new TokenType("SINGLE_LINE_COMMENT", "//[^\n]*", false);
    public static final TokenType MULTI_LINE_COMMENT = new TokenType("MULTI_LINE_COMMENT", "/\\*[\\s\\S]*?\\*/", false);
    public static final TokenType STRING_LITERAL = new TokenType("STRING_LITERAL", "\"((\\\\\\\\)|(\\\\\")|[^\\n\"])*\"", false);
    public static final TokenType CHAR_LITERAL = new TokenType("CHAR_LITERAL", "'((\\\\['nrtbf0])|[^\\n'])'", false);
    public static final TokenType HEX_INT_LITERAL = new TokenType("HEX_INT_LITERAL", "0x[0-9a-fA-F]+(_[0-9a-fA-F]+)*[Ll]?", false);
    public static final TokenType BINARY_INT_LITERAL = new TokenType("BINARY_INT_LITERAL", "0b[01]+(_[01]+)*[Ll]?", false);
    public static final TokenType DECIMAL_INT_LITERAL = new TokenType("DECIMAL_INT_LITERAL", "[0-9]+(_[0-9]+)*[LlFfDd]?", false);
    public static final TokenType DECIMAL_FLOAT_LITERAL = new TokenType("DECIMAL_FLOAT_LITERAL", "(([0-9]+(_[0-9]+)*\\.([0-9]+(_[0-9]+)*)?)|(\\.[0-9]+(_[0-9]+)*))[fFdD]?", false);
    public static final TokenType BOOLEAN_LITERAL = new TokenType("BOOLEAN_LITERAL", "(true)|(false)", true);
    public static final TokenType NULL_LITERAL = new TokenType("NULL_LITERAL", "null", true);
    public static final TokenType CLASS = new TokenType("CLASS", "class", true);
    public static final TokenType RETURN = new TokenType("RETURN", "return", true);
    public static final TokenType IMPORT = new TokenType("IMPORT", "import", true);
    public static final TokenType PACKAGE = new TokenType("PACKAGE", "package", true);
    public static final TokenType STATIC = new TokenType("STATIC", "static", true);
    public static final TokenType INCREMENTAL_OP = new TokenType("INCREMENTAL_OP", "(\\+\\+)|(\\-\\-)", false);
    public static final TokenType ARITHMETIC_OP = new TokenType("ARITHMETIC_OP", "\\+|\\-|\\*|/|%", false);
    public static final TokenType RELATIONAL_OP = new TokenType("RELATIONAL_OP", "\\<|\\>|(<=)|(>=)", false);
    public static final TokenType EQUALITY_OP = new TokenType("EQUALITY_OP", "(==)|(!=)", false);
    public static final TokenType TERNARY_OP = new TokenType("TERNARY_OP", "\\?", false);
    public static final TokenType COLON = new TokenType("COLON", ":", false);
    public static final TokenType ASSIGN_OP = new TokenType("ASSIGN_OP", "=", false);
    public static final TokenType UNARY_OP = new TokenType("UNARY_OP", "~", false);
    public static final TokenType NOT_OP = new TokenType("NOT_OP", "!", false);
    public static final TokenType PROPERTY_ACCESS = new TokenType("PROPERTY_ACCESS", "\\.", false);
    public static final TokenType STATEMENT_END = new TokenType("STATEMENT_END", ";", false);
    public static final TokenType OPEN_CURLY_BRACKET = new TokenType("OPEN_CURLY_BRACKET", "\\{", false);
    public static final TokenType CLOSE_CURLY_BRACKET = new TokenType("CLOSE_CURLY_BRACKET", "\\}", false);
    public static final TokenType GROUP_START = new TokenType("GROUP_START", "\\(", false);
    public static final TokenType GROUP_END = new TokenType("GROUP_END", "\\)", false);
    public static final TokenType ARRAY_START = new TokenType("ARRAY_START", "\\[", false);
    public static final TokenType ARRAY_END = new TokenType("ARRAY_END", "\\]", false);
    public static final TokenType SEPERATOR = new TokenType("SEPERATOR", ",", false);
    public static final TokenType NAME = new TokenType("NAME", "[_A-Za-z$][_A-Za-z0-9$]*", false);
    public static final TokenType WHITESPACE = new TokenType("WHITESPACE", "[\\n\\r\\t ]+", false);
    public static final TokenType EOF = new TokenType("EOF", "", false);
    public static final TokenType LAMBDA_ARROW = new TokenType("LAMBDA_ARROW", "\\->", false);
    public static final TokenType VARARGS = new TokenType("VARARGS", "\\.\\.\\.", false);
    public static final TokenType PUBLIC = new TokenType("PUBLIC", "public", true);
    public static final TokenType PRIVATE = new TokenType("PRIVATE", "private", true);
    public static final TokenType PROTECTED = new TokenType("PROTECTED", "protected", true);
    public static final TokenType NATIVE = new TokenType("NATIVE", "native", true);
    public static final TokenType SYNCHRONIZED = new TokenType("SYNCHRONIZED", "synchronized", true);
    public static final TokenType VOLATILE = new TokenType("VOLATILE", "volatile", true);
    public static final TokenType STRICTFP = new TokenType("STRICTFP", "strictfp", true);
    public static final TokenType FINAL = new TokenType("FINAL", "final", true);
    public static final TokenType ENUM = new TokenType("ENUM", "enum", true);
    public static final TokenType INTERFACE = new TokenType("INTERFACE", "interface", true);
    public static final TokenType ABSTRACT = new TokenType("ABSTRACT", "abstract", true);
    public static final TokenType TRANSIENT = new TokenType("TRANSIENT", "transient", true);
    public static final TokenType IF = new TokenType("IF", "if", true);
    public static final TokenType ELSE = new TokenType("ELSE", "else", true);
    public static final TokenType WHILE = new TokenType("WHILE", "while", true);
    public static final TokenType DO = new TokenType("DO", "do", true);
    public static final TokenType FOR = new TokenType("FOR", "for", true);
    public static final TokenType SWITCH = new TokenType("SWITCH", "switch", true);
    public static final TokenType CASE = new TokenType("CASE", "case", true);
    public static final TokenType DEFAULT = new TokenType("DEFAULT", "default", true);
    public static final TokenType TRY = new TokenType("TRY", "try", true);
    public static final TokenType CATCH = new TokenType("CATCH", "catch", true);
    public static final TokenType FINALLY = new TokenType("FINALLY", "finally", true);
    public static final TokenType BREAK = new TokenType("BREAK", "break", true);
    public static final TokenType CONTINUE = new TokenType("CONTINUE", "continue", true);
    public static final TokenType ASSERT = new TokenType("ASSERT", "assert", true);
    public static final TokenType ANNOTATION = new TokenType("ANNOTATION", "@", false);
    public static final TokenType NEW = new TokenType("NEW", "new", true);
    public static final TokenType EXTENDS = new TokenType("EXTENDS", "extends", true);
    public static final TokenType IMPLEMENTS = new TokenType("IMPLEMENTS", "implements", true);
    public static final TokenType INSTANCEOF = new TokenType("INSTANCEOF", "instanceof", true);
    public static final TokenType THROW = new TokenType("THROW", "throw", true);
    public static final TokenType THROWS = new TokenType("THROWS", "throws", true);
    public static final TokenType GOTO = new TokenType("GOTO", "goto", true);
    public static final TokenType CONST = new TokenType("CONST", "const", true);
    public static final TokenType LOGICAL_OP = new TokenType("LOGICAL_OP", "(\\|\\|)|(&&)|\\^", false);
    public static final TokenType SHIFT_OP = new TokenType("SHIFT_OP", "(\\>\\>\\>)|(\\<\\<)|(\\>\\>)", false);
    public static final TokenType BITWISE_OP = new TokenType("BITWISE_OP", "\\||&|\\^", false);

    private static final TokenType[] DEFAULT_JAVA_TOKENS = new TokenType[] {
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

    public static TokenType[] defaultJava() {
        return DEFAULT_JAVA_TOKENS;
    }

    final String name;
    final Pattern pattern;

    public TokenType(String name, String pattern, boolean keyword) {
        this.name = name;
        this.pattern = Pattern.compile("(?<token>" + pattern + ")" + (keyword ? "([^A-Za-z0-9_$]|$)" : "") + "[\\S\\s]*");
    }

}
