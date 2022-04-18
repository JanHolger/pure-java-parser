package eu.bebendorf.purejavaparser.token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TokenStack {

    Token[] tokens;
    int pos = 0;
    boolean immutable;

    public TokenStack() {
        this(0);
    }

    public TokenStack(int capacity) {
        this(new Token[capacity], false);
    }

    public TokenStack(Token[] tokens) {
        this(tokens, true);
    }

    public TokenStack(Token[] tokens, boolean immutable) {
        this.tokens = tokens;
        this.immutable = immutable;
    }

    public TokenStack trim() {
        return trim(true);
    }

    public TokenStack trim(boolean trimComments) {
        while (size() > 0 && (peek().getType() == TokenType.WHITESPACE || (trimComments && (peek().getType() == TokenType.SINGLE_LINE_COMMENT || peek().getType() == TokenType.MULTI_LINE_COMMENT))))
            pop();
        return this;
    }

    public int size() {
        return Math.max(0, tokens.length - pos);
    }

    public synchronized void push(Token token) {
        ensureCapacity(size() + 1);
        ensureMutable();
        tokens[--pos] = token;
    }

    public Token peek() {
        if(pos >= tokens.length)
            return null;
        return tokens[pos];
    }

    public synchronized Token pop() {
        if(pos >= tokens.length)
            return null;
        return tokens[pos++];
    }

    public synchronized void ensureCapacity(int capacity) {
        if(capacity <= tokens.length)
            return;
        int size = size();
        int newCap = ((capacity / 10) + Math.min(1, capacity % 10)) * 10;
        Token[] newTokens = new Token[newCap];
        System.arraycopy(tokens, pos, newTokens, newCap - size, size);
        tokens = newTokens;
        pos = newCap - size;
        immutable = false;
    }

    public int capacity() {
        return tokens.length;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public synchronized void ensureMutable() {
        if(immutable) {
            int size = size();
            Token[] tokens = new Token[size + 1];
            System.arraycopy(this.tokens, pos, tokens, 1, size);
            this.tokens = tokens;
            this.pos = 1;
            this.immutable = false;
        }
    }

    public TokenStack reversed() {
        int size = size();
        Token[] tokens = new Token[size];
        for(int i = 0; i < size; i++)
            tokens[size - i - 1] = this.tokens[i + pos];
        return new TokenStack(tokens, false);
    }

    public TokenStack clone() {
        immutable = true;
        TokenStack ts = new TokenStack(tokens);
        ts.pos = pos;
        return ts;
    }

    public synchronized void copyFrom(TokenStack stack) {
        this.tokens = stack.tokens;
        this.pos = stack.pos;
        this.immutable = stack.immutable;
    }

    public void forEach(Consumer<Token> consumer) {
        for(int i = pos; i < tokens.length; i++)
            consumer.accept(tokens[i]);
    }

    public List<Token> toList() {
        List<Token> list = new ArrayList<>(size());
        for(int i = pos; i < tokens.length; i++)
            list.add(tokens[i]);
        return list;
    }

}
