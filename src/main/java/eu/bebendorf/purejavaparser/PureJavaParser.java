package eu.bebendorf.purejavaparser;

import eu.bebendorf.purejavaparser.ast.ClassFileDefinition;
import eu.bebendorf.purejavaparser.exception.UnexpectedCharacterException;
import eu.bebendorf.purejavaparser.parser.*;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.Tokenizer;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true)
public class PureJavaParser {

    GeneralParser generalParser;
    ClassParser classParser;
    ExpressionParser expressionParser;
    StatementParser statementParser;

    public PureJavaParser() {
        generalParser = new GeneralParser(this);
        classParser = new ClassParser(this);
        expressionParser = new ExpressionParser(this);
        statementParser = new StatementParser(this);
    }

    public ClassFileDefinition parse(String file, String source) throws UnexpectedTokenException, UnexpectedCharacterException {
        TokenStack tokens = Tokenizer.tokenize(file, source);
        return classParser.parseClassFile(tokens);
    }

}
