package eu.bebendorf.purejavaparser.parser;

import eu.bebendorf.purejavaparser.PureJavaParser;
import eu.bebendorf.purejavaparser.ast.*;
import eu.bebendorf.purejavaparser.ast.statement.StatementBlock;
import eu.bebendorf.purejavaparser.ast.type.GenericDefinitionList;
import eu.bebendorf.purejavaparser.ast.type.method.*;
import eu.bebendorf.purejavaparser.token.Token;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.TokenType;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MethodDefinitionParser {

    private static final TokenType[] NORMAL_METHOD_MODIFIERS = {
            TokenType.STATIC,
            TokenType.PRIVATE,
            TokenType.PUBLIC,
            TokenType.PROTECTED,
            TokenType.ABSTRACT,
            TokenType.FINAL,
            TokenType.STRICTFP,
            TokenType.SYNCHRONIZED,
            TokenType.NATIVE
    };

    private static final TokenType[] INTERFACE_METHOD_MODIFIERS = {
            TokenType.STATIC,
            TokenType.PUBLIC,
            TokenType.ABSTRACT,
            TokenType.DEFAULT
    };

    PureJavaParser parser;

    public ConstructorDefinition parseConstructorDefinition(TokenStack stack, String className) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.clone();
        List<Annotation> annotations = parser.getGeneralParser().parseAnnotations(stackCopy);
        ConstructorModifiers modifiers = parseConstructorModifiers(stackCopy);
        GenericDefinitionList genericDefinitions = null;
        if(stackCopy.trim().peek().is(TokenType.RELATIONAL_OP, "<"))
            genericDefinitions = parser.getGeneralParser().parseGenericDefinitions(stackCopy);
        TokenStack stackCopy2 = stackCopy.clone();
        Variable variable = parser.getGeneralParser().parseVariable(stackCopy2);
        if(!variable.getName().equals(className))
            throw new UnexpectedTokenException(stackCopy.trim().pop());
        stackCopy.copyFrom(stackCopy2);
        TypedParameterList parameters = parseTypedParameterList(stackCopy);
        List<Type> throwables = new ArrayList<>();
        if(stackCopy.trim().peek().getType() == TokenType.THROWS) {
            stackCopy.pop();
            throwables.add(parser.getGeneralParser().parseType(stackCopy, false, false, false));
            while (stackCopy.trim().peek().getType() == TokenType.SEPERATOR) {
                stackCopy.pop();
                throwables.add(parser.getGeneralParser().parseType(stackCopy, false, false, false));
            }
        }
        StatementBlock body = parser.getStatementParser().parseStatementBlock(stackCopy, false);
        stack.copyFrom(stackCopy);
        return new ConstructorDefinition(annotations, modifiers, genericDefinitions, parameters, throwables, body);
    }

    public MethodDefinition parseMethodDefinition(TokenStack stack, boolean interfaceMethod) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.clone();
        List<Annotation> annotations = parser.getGeneralParser().parseAnnotations(stackCopy);
        MethodModifiers modifiers = parseMethodModifiers(stackCopy, interfaceMethod ? INTERFACE_METHOD_MODIFIERS : NORMAL_METHOD_MODIFIERS);
        GenericDefinitionList genericDefinitions = null;
        if(stackCopy.trim().peek().is(TokenType.RELATIONAL_OP, "<"))
            genericDefinitions = parser.getGeneralParser().parseGenericDefinitions(stackCopy);
        Type type = parser.getGeneralParser().parseType(stackCopy, true, true, false);
        Variable variable = parser.getGeneralParser().parseVariable(stackCopy);
        TypedParameterList parameters = parseTypedParameterList(stackCopy);
        List<Type> throwables = new ArrayList<>();
        if(stackCopy.trim().peek().getType() == TokenType.THROWS) {
            stackCopy.pop();
            throwables.add(parser.getGeneralParser().parseType(stackCopy, false, false, false));
            while (stackCopy.trim().peek().getType() == TokenType.SEPERATOR) {
                stackCopy.pop();
                throwables.add(parser.getGeneralParser().parseType(stackCopy, false, false, false));
            }
        }
        StatementBlock body = null;
        if(modifiers.isNative() || modifiers.isAbstract() || (interfaceMethod && !modifiers.isDefault())) {
            if(stackCopy.trim().peek().getType() != TokenType.STATEMENT_END)
                throw new UnexpectedTokenException(stackCopy.pop());
            stackCopy.pop();
        } else {
            body = parser.getStatementParser().parseStatementBlock(stackCopy, false);
        }
        stack.copyFrom(stackCopy);
        return new MethodDefinition(annotations, modifiers, genericDefinitions, type, variable, parameters, throwables, body);
    }

    private MethodModifiers parseMethodModifiers(TokenStack stack, TokenType... allowed) throws UnexpectedTokenException {
        MethodModifiers modifiers = new MethodModifiers();
        loop:
        while (true) {
            Token t = stack.trim().peek();
            boolean isAllowed = false;
            for(TokenType a : allowed) {
                if(a == t.getType()) {
                    isAllowed = true;
                    break;
                }
            }
            switch (t.getType().getName()) {
                case "STATIC": {
                    if (modifiers.isStatic() || modifiers.isDefault() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setStatic(true);
                    break;
                }
                case "PRIVATE": {
                    if (modifiers.hasAccessModifier() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPrivate(true);
                    break;
                }
                case "PUBLIC": {
                    if (modifiers.hasAccessModifier() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPublic(true);
                    break;
                }
                case "PROTECTED": {
                    if (modifiers.hasAccessModifier() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setProtected(true);
                    break;
                }
                case "ABSTRACT": {
                    if (modifiers.isAbstract() || modifiers.isFinal() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setAbstract(true);
                    break;
                }
                case "FINAL": {
                    if (modifiers.isFinal() || modifiers.isAbstract() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setFinal(true);
                    break;
                }
                case "STRICTFP": {
                    if (modifiers.isStrictfp() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setStrictfp(true);
                    break;
                }
                case "SYNCHRONIZED": {
                    if (modifiers.isSynchronized() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setSynchronized(true);
                    break;
                }
                case "NATIVE": {
                    if (modifiers.isNative() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setNative(true);
                    break;
                }
                case "DEFAULT": {
                    if (modifiers.isDefault() || modifiers.isStatic() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setDefault(true);
                    break;
                }
                default: {
                    break loop;
                }
            }
        }
        return modifiers;
    }

    private ConstructorModifiers parseConstructorModifiers(TokenStack stack) throws UnexpectedTokenException {
        ConstructorModifiers modifiers = new ConstructorModifiers();
        loop:
        while (true) {
            switch (stack.trim().peek().getType().getName()) {
                case "PRIVATE": {
                    if (modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPrivate(true);
                    break;
                }
                case "PUBLIC": {
                    if (modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPublic(true);
                    break;
                }
                case "PROTECTED": {
                    if (modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setProtected(true);
                    break;
                }
                default: {
                    break loop;
                }
            }
        }
        return modifiers;
    }

    private TypedParameterList parseTypedParameterList(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().pop().getType() != TokenType.GROUP_START)
            return null;
        List<TypedParameterDefinition> parameters = new ArrayList<>();
        while (stack.trim().peek().getType() != TokenType.GROUP_END) {
            if(parameters.size() > 0) {
                Token t = stack.trim().pop();
                if(t.getType() != TokenType.SEPERATOR)
                    throw new UnexpectedTokenException(t);
            }
            TypedParameterDefinition param = parseTypedParameterDefinition(stack);
            parameters.add(param);
        }
        stack.pop();
        return new TypedParameterList(parameters);
    }

    private TypedParameterDefinition parseTypedParameterDefinition(TokenStack stack) throws UnexpectedTokenException {
        List<Annotation> annotations = parser.getGeneralParser().parseAnnotations(stack);
        Type type = parser.getGeneralParser().parseType(stack, true, true, true);
        Variable variable = parser.getGeneralParser().parseVariable(stack);
        return new TypedParameterDefinition(annotations, type, variable);
    }

}
