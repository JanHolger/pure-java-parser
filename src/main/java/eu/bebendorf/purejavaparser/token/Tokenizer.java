package eu.bebendorf.purejavaparser.token;

import eu.bebendorf.purejavaparser.exception.UnexpectedCharacterException;

import java.util.regex.Matcher;

public class Tokenizer {

    public static TokenStack tokenize(String source) throws UnexpectedCharacterException {
        return tokenize("<anonymous>", source);
    }

    public static TokenStack tokenize(String fileName, String source) throws UnexpectedCharacterException {
        int[] lineBreaks = lineBreaks(source);
        TokenStack tokens = new TokenStack();
        int pos = 0;
        Matcher matcher;
        outer:
        while (source.length() > 0) {
            int line = line(lineBreaks, pos);
            int linePos = linePos(lineBreaks, line, pos);
            for(TokenType type : TokenType.ordered()) {
                matcher = type.getPattern().matcher(source);
                if(matcher.matches()) {
                    String value = matcher.group("token");
                    tokens.push(new Token(fileName, pos, line, linePos, type, value));
                    pos += value.length();
                    source = value.length() < source.length() ? source.substring(value.length()) : "";
                    continue outer;
                }
            }
            throw new UnexpectedCharacterException(fileName, source.charAt(0), pos, line, linePos);
        }
        int line = line(lineBreaks, pos);
        tokens.push(new Token(fileName, pos, line, linePos(lineBreaks, line, pos), TokenType.EOF, "<EOF>"));
        return tokens.reversed();
    }

    private static int line(int[] lineBreaks, int pos) {
        if(lineBreaks.length == 0)
            return 0;
        for(int i=0; i<lineBreaks.length; i++) {
            if(pos <= lineBreaks[i])
                return i;
        }
        return lineBreaks.length;
    }

    private static int linePos(int[] lineBreaks, int line, int pos) {
        if(line == 0)
            return pos;
        return pos - lineBreaks[line - 1] - 1;
    }

    private static int[] lineBreaks(String source) {
        char[] chars = source.toCharArray();
        int count = 0;
        for(int i=0; i < chars.length; i++) {
            if(chars[i] == '\n')
                count++;
        }
        int[] positions = new int[count];
        count = 0;
        for(int i=0; i < chars.length; i++) {
            if(chars[i] == '\n')
                positions[count++] = i;
        }
        return positions;
    }


}
