package eu.bebendorf.purejavaparser.parser;

import eu.bebendorf.purejavaparser.PureJavaParser;
import eu.bebendorf.purejavaparser.ast.*;
import eu.bebendorf.purejavaparser.ast.expression.ArgumentList;
import eu.bebendorf.purejavaparser.ast.statement.StatementBlock;
import eu.bebendorf.purejavaparser.ast.type.*;
import eu.bebendorf.purejavaparser.ast.type.field.FieldDefinition;
import eu.bebendorf.purejavaparser.ast.type.method.ConstructorDefinition;
import eu.bebendorf.purejavaparser.ast.type.method.MethodDefinition;
import eu.bebendorf.purejavaparser.token.Token;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.TokenType;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal = true)
@Getter
public class TypeDefinitionParser {

    private static final TokenType[] NORMAL_CLASS_MODIFIERS = {
            TokenType.PRIVATE,
            TokenType.PUBLIC,
            TokenType.PROTECTED,
            TokenType.STATIC,
            TokenType.ABSTRACT,
            TokenType.FINAL,
            TokenType.STRICTFP
    };

    private static final TokenType[] LOCAL_CLASS_MODIFIERS = {
            TokenType.ABSTRACT,
            TokenType.FINAL,
            TokenType.STRICTFP
    };

    private static final TokenType[] NORMAL_INTERFACE_MODIFIERS = {
            TokenType.PRIVATE,
            TokenType.PUBLIC,
            TokenType.PROTECTED,
            TokenType.STATIC,
            TokenType.ABSTRACT,
            TokenType.STRICTFP
    };

    private static final TokenType[] LOCAL_INTERFACE_MODIFIERS = {
            TokenType.ABSTRACT,
            TokenType.STRICTFP
    };

    private static final TokenType[] NORMAL_ENUM_MODIFIERS = {
            TokenType.PRIVATE,
            TokenType.PUBLIC,
            TokenType.PROTECTED,
            TokenType.STATIC,
            TokenType.STRICTFP
    };

    private static final TokenType[] LOCAL_ENUM_MODIFIERS = {
            TokenType.STRICTFP
    };

    PureJavaParser parser;
    FieldDefinitionParser fieldDefinitionParser;
    MethodDefinitionParser methodDefinitionParser;

    public TypeDefinitionParser(PureJavaParser parser) {
        this.parser = parser;
        this.fieldDefinitionParser = new FieldDefinitionParser(parser);
        this.methodDefinitionParser = new MethodDefinitionParser(parser);
    }

    public TypeDefinition parseTypeDefinition(TokenStack stack, boolean local) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        UnexpectedTokenException ex;
        try {
            ClassDefinition classDefinition = parseClassDefinition(stackCopy, local);
            stack.copyFrom(stackCopy);
            return classDefinition;
        } catch (UnexpectedTokenException nextEx) {
            ex = nextEx;
        }
        stackCopy = stack.clone();
        try {
            EnumDefinition enumDefinition = parseEnumDefinition(stackCopy, local);
            stack.copyFrom(stackCopy);
            return enumDefinition;
        } catch (UnexpectedTokenException nextEx) {
            ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
        }
        stackCopy = stack.clone();
        try {
            InterfaceDefinition interfaceDefinition = parseInterfaceDefinition(stackCopy, local);
            stack.copyFrom(stackCopy);
            return interfaceDefinition;
        } catch (UnexpectedTokenException nextEx) {
            ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
        }
        throw ex;
    }

    private ClassDefinition parseClassDefinition(TokenStack stack, boolean local) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        List<Annotation> annotations = parser.getGeneralParser().parseAnnotations(stackCopy);
        ClassModifiers modifiers = parseClassModifiers(stackCopy, local ? LOCAL_CLASS_MODIFIERS : NORMAL_CLASS_MODIFIERS);
        if(stackCopy.trim().peek().getType() != TokenType.CLASS)
            throw new UnexpectedTokenException(stackCopy.pop());
        stackCopy.pop();
        if(stackCopy.trim().peek().getType() != TokenType.NAME)
            throw new UnexpectedTokenException(stackCopy.pop());
        String name = stackCopy.pop().getValue();
        GenericDefinitionList genericDefinitions = null;
        if(stackCopy.trim().peek().is(TokenType.RELATIONAL_OP, "<"))
            genericDefinitions = parser.getGeneralParser().parseGenericDefinitions(stackCopy);
        Type superClass = null;
        if(stackCopy.trim().peek().getType() == TokenType.EXTENDS) {
            stackCopy.pop();
            superClass = parser.getGeneralParser().parseType(stackCopy, true, false, false);
        }
        List<Type> interfaces = new ArrayList<>();
        if(stackCopy.trim().peek().getType() == TokenType.IMPLEMENTS) {
            stackCopy.pop();
            interfaces.add(parser.getGeneralParser().parseType(stackCopy, true, false, false));
            while (stackCopy.trim().peek().getType() == TokenType.SEPERATOR) {
                stackCopy.pop();
                interfaces.add(parser.getGeneralParser().parseType(stackCopy, true, false, false));
            }
        }
        ClassBody body = (ClassBody) parseTypeBody(stackCopy, name, local, false, false, false);
        stack.copyFrom(stackCopy);
        return new ClassDefinition(annotations, modifiers, name, genericDefinitions, superClass, interfaces, body);
    }

    private InterfaceDefinition parseInterfaceDefinition(TokenStack stack, boolean local) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        List<Annotation> annotations = parser.getGeneralParser().parseAnnotations(stackCopy);
        ClassModifiers modifiers = parseClassModifiers(stackCopy, local ? LOCAL_INTERFACE_MODIFIERS : NORMAL_INTERFACE_MODIFIERS);
        if(stackCopy.trim().peek().getType() != TokenType.INTERFACE)
            throw new UnexpectedTokenException(stackCopy.pop());
        stackCopy.pop();
        if(stackCopy.trim().peek().getType() != TokenType.NAME)
            throw new UnexpectedTokenException(stackCopy.pop());
        String name = stackCopy.pop().getValue();
        GenericDefinitionList genericDefinitions = null;
        if(stackCopy.trim().peek().is(TokenType.RELATIONAL_OP, "<"))
            genericDefinitions = parser.getGeneralParser().parseGenericDefinitions(stackCopy);
        List<Type> interfaces = new ArrayList<>();
        if(stackCopy.trim().peek().getType() == TokenType.EXTENDS) {
            stackCopy.pop();
            interfaces.add(parser.getGeneralParser().parseType(stackCopy, true, false, false));
            while (stackCopy.trim().peek().getType() == TokenType.SEPERATOR) {
                stackCopy.pop();
                interfaces.add(parser.getGeneralParser().parseType(stackCopy, true, false, false));
            }
        }
        InterfaceBody body = (InterfaceBody) parseTypeBody(stackCopy, name, local, false, false, true);
        stack.copyFrom(stackCopy);
        return new InterfaceDefinition(annotations, modifiers, name, genericDefinitions, interfaces, body);
    }

    private EnumDefinition parseEnumDefinition(TokenStack stack, boolean local) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        List<Annotation> annotations = parser.getGeneralParser().parseAnnotations(stackCopy);
        ClassModifiers modifiers = parseClassModifiers(stackCopy, local ? LOCAL_ENUM_MODIFIERS : NORMAL_ENUM_MODIFIERS);
        if(stackCopy.trim().peek().getType() != TokenType.ENUM)
            throw new UnexpectedTokenException(stackCopy.pop());
        stackCopy.pop();
        if(stackCopy.trim().peek().getType() != TokenType.NAME)
            throw new UnexpectedTokenException(stackCopy.pop());
        String name = stackCopy.pop().getValue();
        List<Type> interfaces = new ArrayList<>();
        if(stackCopy.trim().peek().getType() == TokenType.IMPLEMENTS) {
            stackCopy.pop();
            interfaces.add(parser.getGeneralParser().parseType(stackCopy, true, false, false));
            while (stackCopy.trim().peek().getType() == TokenType.SEPERATOR) {
                stackCopy.pop();
                interfaces.add(parser.getGeneralParser().parseType(stackCopy, true, false, false));
            }
        }
        EnumBody body = (EnumBody) parseTypeBody(stackCopy, name, local, false, true, false);
        stack.copyFrom(stackCopy);
        return new EnumDefinition(annotations, modifiers, name, interfaces, body);
    }

    public TypeBody parseTypeBody(TokenStack stack, String className, boolean local, boolean anonymous, boolean isEnum, boolean isInterface) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() != TokenType.OPEN_CURLY_BRACKET)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        List<EnumValue> enumValues = isEnum ? new ArrayList<>() : null;
        if(isEnum && stack.trim().peek().getType() != TokenType.CLOSE_CURLY_BRACKET) {
            while (stack.trim().peek().getType() != TokenType.STATEMENT_END) {
                if(enumValues.size() > 0) {
                    if(stack.trim().peek().getType() != TokenType.SEPERATOR)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                }
                Variable variable = parser.getGeneralParser().parseVariable(stack);
                ArgumentList arguments = parser.getExpressionParser().parseArgumentList(stack);
                enumValues.add(new EnumValue(variable, arguments));
            }
            stack.pop();
        }
        List<StatementBlock> staticInitializers = new ArrayList<>();
        List<StatementBlock> initializers = new ArrayList<>();
        List<FieldDefinition> fields = new ArrayList<>();
        List<MethodDefinition> methods = new ArrayList<>();
        List<ConstructorDefinition> constructors = new ArrayList<>();
        List<TypeDefinition> innerClasses = new ArrayList<>();
        while (stack.trim().peek().getType() != TokenType.CLOSE_CURLY_BRACKET) {
            UnexpectedTokenException ex;
            TokenStack s = stack.clone();
            try {
                fields.add(fieldDefinitionParser.parseFieldDefinition(s));
                stack.copyFrom(s);
                continue;
            } catch (UnexpectedTokenException nextEx) {
                ex = nextEx;
            }
            if(!isInterface) {
                s = stack.clone();
                if(!anonymous && s.peek().getType() == TokenType.STATIC) {
                    try {
                        s.pop();
                        staticInitializers.add(parser.getStatementParser().parseStatementBlock(s, false));
                        stack.copyFrom(s);
                        continue;
                    } catch (UnexpectedTokenException nextEx) {
                        ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
                    }
                } else {
                    try {
                        initializers.add(parser.getStatementParser().parseStatementBlock(s, false));
                        stack.copyFrom(s);
                        continue;
                    } catch (UnexpectedTokenException nextEx) {
                        ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
                    }
                }
            }
            if(!anonymous && !isInterface) {
                s = stack.clone();
                try {
                    constructors.add(methodDefinitionParser.parseConstructorDefinition(s, className));
                    stack.copyFrom(s);
                    continue;
                } catch (UnexpectedTokenException nextEx) {
                    ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
                }
            }
            s = stack.clone();
            try {
                methods.add(methodDefinitionParser.parseMethodDefinition(s, isInterface));
                stack.copyFrom(s);
                continue;
            } catch (UnexpectedTokenException nextEx) {
                ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
            }
            s = stack.clone();
            try {
                innerClasses.add(parseTypeDefinition(s, local));
                stack.copyFrom(s);
            } catch (UnexpectedTokenException nextEx) {
                throw nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
            }
        }
        stack.pop();
        if(isEnum)
            return new EnumBody(enumValues, staticInitializers, initializers, fields, constructors, methods, innerClasses);
        else if(isInterface)
            return new InterfaceBody(fields, methods, innerClasses);
        else
            return new ClassBody(staticInitializers, initializers, fields, constructors, methods, innerClasses);
    }

    private ClassModifiers parseClassModifiers(TokenStack stack, TokenType... allowed) throws UnexpectedTokenException {
        ClassModifiers modifiers = new ClassModifiers();
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
            switch (t.getType()) {
                case STATIC: {
                    if(modifiers.isStatic() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setStatic(true);
                    break;
                }
                case PRIVATE: {
                    if(modifiers.hasAccessModifier() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPrivate(true);
                    break;
                }
                case PUBLIC: {
                    if(modifiers.hasAccessModifier() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPublic(true);
                    break;
                }
                case PROTECTED: {
                    if(modifiers.hasAccessModifier() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setProtected(true);
                    break;
                }
                case ABSTRACT: {
                    if(modifiers.isAbstract() || modifiers.isFinal() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setAbstract(true);
                    break;
                }
                case FINAL: {
                    if(modifiers.isFinal() || modifiers.isAbstract() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setFinal(true);
                    break;
                }
                case STRICTFP: {
                    if(modifiers.isStrictfp() || !isAllowed)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setStrictfp(true);
                    break;
                }
                default: {
                    break loop;
                }
            }
        }
        return modifiers;
    }

}
