package eu.bebendorf.purejavaparser.parser;

import eu.bebendorf.purejavaparser.PureJavaParser;
import eu.bebendorf.purejavaparser.ast.Expression;
import eu.bebendorf.purejavaparser.ast.Type;
import eu.bebendorf.purejavaparser.ast.Variable;
import eu.bebendorf.purejavaparser.ast.VariableDefinition;
import eu.bebendorf.purejavaparser.token.Token;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.TokenType;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class GeneralParser {

    PureJavaParser parser;

    public Type parseType(TokenStack stack, boolean allowGeneric, boolean allowArray, boolean allowVarArgs) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        if(stackCopy.peek().getType() != TokenType.NAME)
            throw new UnexpectedTokenException(stackCopy.pop());
        List<String> name = new ArrayList<>();
        name.add(stackCopy.pop().getValue());
        while (stackCopy.trim().peek().getType() == TokenType.PROPERTY_ACCESS) {
            stackCopy.pop();
            if(stackCopy.trim().peek().getType() != TokenType.NAME)
                throw new UnexpectedTokenException(stackCopy.pop());
            name.add(stackCopy.pop().getValue());
        }
        List<Type> genericTypes = new ArrayList<>();
        int arrayDepth = 0;
        boolean varArgs = false;
        if(allowGeneric) {
            if(stackCopy.trim().peek().is(TokenType.COMPARISON_OP, "<")) {
                stackCopy.pop();
                while (!stackCopy.trim().peek().is(TokenType.COMPARISON_OP, ">")) {
                    if(genericTypes.size() > 0) {
                        if(stackCopy.peek().getType() != TokenType.SEPERATOR)
                            throw new UnexpectedTokenException(stackCopy.pop());
                        stackCopy.pop();
                    }
                    genericTypes.add(parseType(stackCopy, true, true, false));
                }
                stackCopy.pop();
            }
        }
        if(allowArray) {
            while (stackCopy.trim().peek().getType() == TokenType.ARRAY_START) {
                stackCopy.pop();
                if(stackCopy.trim().peek().getType() != TokenType.ARRAY_END)
                    throw new UnexpectedTokenException(stackCopy.pop());
                stackCopy.pop();
                arrayDepth++;
            }
            if(stackCopy.trim().peek().getType() == TokenType.VARARGS) {
                if(!allowVarArgs)
                    throw new UnexpectedTokenException(stackCopy.pop());
                stackCopy.pop();
                varArgs = true;
                arrayDepth++;
            }
        }
        stack.copyFrom(stackCopy);
        return new Type(name, genericTypes, arrayDepth, varArgs);
    }

    public Variable parseVariable(TokenStack stack) throws UnexpectedTokenException {
        Token t = stack.trim().pop();
        if(t.getType() != TokenType.NAME)
            throw new UnexpectedTokenException(t);
        return new Variable(t.getValue());
    }

    public VariableDefinition parseVariableDefinition(TokenStack stack, boolean requireInitializer, boolean allowFinal) throws UnexpectedTokenException {
        boolean finalModifier = false;
        if(allowFinal && stack.trim().peek().getType() == TokenType.FINAL) {
            stack.pop();
            finalModifier = true;
        }
        Type type = parseType(stack, true, true, false);
        Variable variable = parseVariable(stack);
        Expression initializer = null;
        if(stack.trim().peek().getType() == TokenType.ASSIGN_OP) {
            stack.pop();
            initializer = parser.getExpressionParser().parseExpression(stack);
        } else if(requireInitializer) {
            throw new UnexpectedTokenException(stack.pop());
        }
        return new VariableDefinition(finalModifier, type, variable, initializer);
    }

}
