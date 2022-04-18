package eu.bebendorf.purejavaparser.exception;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true)
public class UnexpectedCharacterException extends Exception {

    String file;
    char character;
    int pos;
    int line;
    int linePos;

    public UnexpectedCharacterException(String file, char character, int pos, int line, int linePos) {
        super("Unexpected character '" + character + "' at " + file + " [" + (line + 1) + ", " + (linePos + 1) + "]");
        this.file = file;
        this.character = character;
        this.pos = pos;
        this.line = line;
        this.linePos = linePos;
    }

}
