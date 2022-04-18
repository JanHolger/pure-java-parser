package eu.bebendorf.purejavaparser.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Token {

    String file;
    int pos;
    int line;
    int linePos;
    TokenType type;
    String value;

    public boolean is(TokenType type, String value) {
        return this.type == type && this.value.equals(value);
    }

}
