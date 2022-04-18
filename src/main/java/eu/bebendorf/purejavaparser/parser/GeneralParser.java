package eu.bebendorf.purejavaparser.parser;

import eu.bebendorf.purejavaparser.PureJavaParser;
import eu.bebendorf.purejavaparser.ast.Annotation;
import eu.bebendorf.purejavaparser.ast.expression.ArrayInitializer;
import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.purejavaparser.ast.Type;
import eu.bebendorf.purejavaparser.ast.Variable;
import eu.bebendorf.purejavaparser.ast.VariableDefinition;
import eu.bebendorf.purejavaparser.ast.expression.NullLiteral;
import eu.bebendorf.purejavaparser.token.Token;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.TokenType;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public VariableDefinition parseVariableDefinition(TokenStack stack, boolean allowAnnotations, boolean allowFinal, boolean requireInitializer) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.clone();
        List<Annotation> annotations = allowAnnotations ? parser.getGeneralParser().parseAnnotations(stackCopy) : new ArrayList<>();
        boolean finalModifier = false;
        if(allowFinal && stackCopy.trim().peek().getType() == TokenType.FINAL) {
            stackCopy.pop();
            finalModifier = true;
        }
        Type type = parseType(stackCopy, true, true, false);
        Variable variable = parseVariable(stackCopy);
        if(!type.isVarArgs() && stackCopy.trim().peek().getType() == TokenType.ARRAY_START) {
            int arrayDepth = 0;
            while (stackCopy.trim().peek().getType() == TokenType.ARRAY_START) {
                stackCopy.pop();
                if(stackCopy.trim().peek().getType() != TokenType.ARRAY_END)
                    throw new UnexpectedTokenException(stackCopy.pop());
                arrayDepth++;
            }
            type = new Type(type.getName(), type.getGenericTypes(), type.getArrayDepth() + arrayDepth, false);
        }
        Expression initializer = null;
        if(stackCopy.trim().peek().getType() == TokenType.ASSIGN_OP) {
            stackCopy.pop();
            initializer = parser.getExpressionParser().parseExpression(stackCopy);
        } else if(requireInitializer) {
            throw new UnexpectedTokenException(stackCopy.pop());
        }
        stack.copyFrom(stackCopy);
        return new VariableDefinition(annotations, finalModifier, type, variable, initializer);
    }

    public List<Annotation> parseAnnotations(TokenStack stack) throws UnexpectedTokenException {
        List<Annotation> annotations = new ArrayList<>();
        while (stack.trim().peek().getType() == TokenType.ANNOTATION) {
            stack.pop();
            Type type = parseType(stack, false, false, false);
            Map<String, Expression> arguments = new HashMap<>();
            if(stack.trim().peek().getType() == TokenType.GROUP_START) {
                stack.pop();
                while (stack.trim().peek().getType() != TokenType.GROUP_END) {
                    if(arguments.size() > 0) {
                        if(stack.trim().peek().getType() != TokenType.SEPERATOR)
                            throw new UnexpectedTokenException(stack.pop());
                        stack.pop();
                    }
                    TokenStack stackCopy = stack.clone();
                    if(stackCopy.trim().peek().getType() == TokenType.NAME) {
                        String name = stackCopy.pop().getValue();
                        if(stackCopy.trim().peek().getType() == TokenType.ASSIGN_OP) {
                            stackCopy.pop();
                            Expression value = parseAnnotationValue(stackCopy);
                            arguments.put(name, value);
                            stack.copyFrom(stackCopy);
                            continue;
                        }
                    }
                    Expression value = parseAnnotationValue(stack);
                    arguments.put("value", value);
                    if(stack.trim().peek().getType() != TokenType.GROUP_END)
                        throw new UnexpectedTokenException(stack.pop());
                    break;
                }
                stack.pop();
            }
            annotations.add(new Annotation(type, arguments));
        }
        return annotations;
    }

    private Expression parseAnnotationValue(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() == TokenType.OPEN_CURLY_BRACKET) {
            stack.pop();
            List<Expression> values = new ArrayList<>();
            while (stack.trim().peek().getType() != TokenType.CLOSE_CURLY_BRACKET) {
                if(values.size() > 0) {
                    if(stack.trim().peek().getType() != TokenType.SEPERATOR)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                }
                Expression value = parseAnnotationValue(stack);
                // TODO Check compatibility with other values
                values.add(value);
            }
            stack.pop();
            return new ArrayInitializer(values);
        }
        TokenStack stackCopy = stack.clone();
        Expression literal = parser.getExpressionParser().parseLiteral(stackCopy);
        if(literal instanceof NullLiteral)
            throw new UnexpectedTokenException(stack.pop());
        return literal;
    }

}
