package eu.bebendorf.purejavaparser.parser;

import eu.bebendorf.purejavaparser.token.Token;
import lombok.Getter;

@Getter
public class UnexpectedTokenException extends Exception {

    final Token token;

    public UnexpectedTokenException(Token token) {
        super("Unexpected token '" + token.getValue() + "' at " + token.getFile() + " [" + (token.getLine() + 1) + ", " + (token.getLinePos() + 1) + "]");
        this.token = token;
    }

}
