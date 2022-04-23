package eu.bebendorf.purejavaparser.parser;

import eu.bebendorf.purejavaparser.PureJavaParser;
import eu.bebendorf.purejavaparser.ast.*;
import eu.bebendorf.purejavaparser.ast.expression.ArgumentList;
import eu.bebendorf.purejavaparser.ast.statement.StatementBlock;
import eu.bebendorf.purejavaparser.token.Token;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.TokenType;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ClassParser {

    PureJavaParser parser;

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

    public ClassFileDefinition parseClassFile(TokenStack stack) throws UnexpectedTokenException {
        PackageStatement packageStatement = null;
        TokenStack stackCopy = stack.clone();
        List<Annotation> annotations = parser.getGeneralParser().parseAnnotations(stackCopy);
        if(stackCopy.trim().peek().getType() == TokenType.PACKAGE) {
            stack.copyFrom(stackCopy);
            stack.pop();
            List<String> name = parsePackageName(stack);
            if(stack.trim().peek().getType() != TokenType.STATEMENT_END)
                throw new UnexpectedTokenException(stack.pop());
            stack.pop();
            packageStatement = new PackageStatement(annotations, name);
        }
        List<ImportStatement> imports = new ArrayList<>();
        while (stack.trim().peek().getType() == TokenType.IMPORT)
            imports.add(parseImport(stack));
        List<TypeDefinition> classes = new ArrayList<>();
        while (stack.trim().peek().getType() != TokenType.EOF)
            classes.add(parseTypeDefinition(stack, false));
        return new ClassFileDefinition(packageStatement, imports, classes);
    }

    private List<String> parsePackageName(TokenStack stack) throws UnexpectedTokenException {
        List<String> parts = new ArrayList<>();
        if(stack.trim().peek().getType() != TokenType.NAME)
            throw new UnexpectedTokenException(stack.pop());
        parts.add(stack.pop().getValue());
        while (stack.trim().peek().getType() == TokenType.PROPERTY_ACCESS) {
            stack.pop();
            if(stack.trim().peek().getType() != TokenType.NAME)
                throw new UnexpectedTokenException(stack.pop());
            parts.add(stack.pop().getValue());
        }
        return parts;
    }

    private ImportStatement parseImport(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() != TokenType.IMPORT)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        boolean staticImport = false;
        if(stack.trim().peek().getType() == TokenType.STATIC) {
            stack.pop();
            staticImport = true;
        }
        List<String> name = new ArrayList<>();
        if(stack.trim().peek().getType() != TokenType.NAME)
            throw new UnexpectedTokenException(stack.pop());
        name.add(stack.pop().getValue());
        if(stack.trim().peek().getType() != TokenType.PROPERTY_ACCESS)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.NAME)
            throw new UnexpectedTokenException(stack.pop());
        name.add(stack.pop().getValue());
        boolean wildcard = false;
        while (stack.trim().peek().getType() == TokenType.PROPERTY_ACCESS) {
            stack.pop();
            if(stack.trim().peek().is(TokenType.ARITHMETIC_OP, "*")) {
                stack.pop();
                wildcard = true;
                break;
            }
            if(stack.trim().peek().getType() != TokenType.NAME)
                throw new UnexpectedTokenException(stack.pop());
            name.add(stack.pop().getValue());
        }
        if(stack.trim().peek().getType() != TokenType.STATEMENT_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        return new ImportStatement(name, wildcard, staticImport);
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
        ClassBody body = parseClassBody(stackCopy, name, local, false, false);
        stack.copyFrom(stackCopy);
        return new ClassDefinition(annotations, modifiers, name, superClass, interfaces, body);
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
        EnumBody body = (EnumBody) parseClassBody(stackCopy, name, local, false, true);
        stack.copyFrom(stackCopy);
        return new EnumDefinition(annotations, modifiers, name, interfaces, body);
    }

    public ClassBody parseClassBody(TokenStack stack, String className, boolean local, boolean anonymous, boolean isEnum) throws UnexpectedTokenException {
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
        List<FieldDefinition> fields = new ArrayList<>();
        List<MethodDefinition> methods = new ArrayList<>();
        List<ConstructorDefinition> constructors = new ArrayList<>();
        List<TypeDefinition> innerClasses = new ArrayList<>();
        while (stack.trim().peek().getType() != TokenType.CLOSE_CURLY_BRACKET) {
            UnexpectedTokenException ex;
            TokenStack s = stack.clone();
            try {
                fields.add(parseFieldDefinition(s));
                stack.copyFrom(s);
                continue;
            } catch (UnexpectedTokenException nextEx) {
                ex = nextEx;
            }
            if(!anonymous) {
                s = stack.clone();
                try {
                    constructors.add(parseConstructorDefinition(s, className));
                    stack.copyFrom(s);
                    continue;
                } catch (UnexpectedTokenException nextEx) {
                    ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
                }
            }
            s = stack.clone();
            try {
                methods.add(parseMethodDefinition(s));
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
            return new EnumBody(enumValues, fields, constructors, methods, innerClasses);
        else
            return new ClassBody(fields, constructors, methods, innerClasses);
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

    private FieldDefinition parseFieldDefinition(TokenStack stack) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        List<Annotation> annotations = parser.getGeneralParser().parseAnnotations(stackCopy);
        FieldModifiers modifiers = parseFieldModifiers(stackCopy);
        VariableDefinition variableDefinition = parser.getGeneralParser().parseVariableDefinition(stackCopy, false, false, false);
        if(stackCopy.trim().peek().getType() != TokenType.STATEMENT_END)
            throw new UnexpectedTokenException(stackCopy.pop());
        stackCopy.pop();
        stack.copyFrom(stackCopy);
        return new FieldDefinition(annotations, modifiers, variableDefinition);
    }

    private FieldModifiers parseFieldModifiers(TokenStack stack) throws UnexpectedTokenException {
        FieldModifiers modifiers = new FieldModifiers();
        loop:
        while (true) {
            switch (stack.trim().peek().getType()) {
                case STATIC: {
                    if(modifiers.isStatic())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setStatic(true);
                    break;
                }
                case PRIVATE: {
                    if(modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPrivate(true);
                    break;
                }
                case PUBLIC: {
                    if(modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPublic(true);
                    break;
                }
                case PROTECTED: {
                    if(modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setProtected(true);
                    break;
                }
                case TRANSIENT: {
                    if(modifiers.isTransient())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setTransient(true);
                    break;
                }
                case FINAL: {
                    if(modifiers.isFinal())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setFinal(true);
                    break;
                }
                case VOLATILE: {
                    if(modifiers.isVolatile())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setVolatile(true);
                    break;
                }
                default: {
                    break loop;
                }
            }
        }
        return modifiers;
    }

    public ConstructorDefinition parseConstructorDefinition(TokenStack stack, String className) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.clone();
        List<Annotation> annotations = parser.getGeneralParser().parseAnnotations(stackCopy);
        ConstructorModifiers modifiers = parseConstructorModifiers(stackCopy);
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
        return new ConstructorDefinition(annotations, modifiers, parameters, throwables, body);
    }

    public MethodDefinition parseMethodDefinition(TokenStack stack) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.clone();
        List<Annotation> annotations = parser.getGeneralParser().parseAnnotations(stackCopy);
        MethodModifiers modifiers = parseMethodModifiers(stackCopy);
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
        StatementBlock body = parser.getStatementParser().parseStatementBlock(stackCopy, false);
        stack.copyFrom(stackCopy);
        return new MethodDefinition(annotations, modifiers, type, variable, parameters, throwables, body);
    }

    private MethodModifiers parseMethodModifiers(TokenStack stack) throws UnexpectedTokenException {
        MethodModifiers modifiers = new MethodModifiers();
        loop:
        while (true) {
            switch (stack.trim().peek().getType()) {
                case STATIC: {
                    if (modifiers.isStatic())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setStatic(true);
                    break;
                }
                case PRIVATE: {
                    if (modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPrivate(true);
                    break;
                }
                case PUBLIC: {
                    if (modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPublic(true);
                    break;
                }
                case PROTECTED: {
                    if (modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setProtected(true);
                    break;
                }
                case ABSTRACT: {
                    if (modifiers.isAbstract() || modifiers.isFinal())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setAbstract(true);
                    break;
                }
                case FINAL: {
                    if (modifiers.isFinal() || modifiers.isAbstract())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setFinal(true);
                    break;
                }
                case STRICTFP: {
                    if (modifiers.isStrictfp())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setStrictfp(true);
                    break;
                }
                case SYNCHRONIZED: {
                    if (modifiers.isSynchronized())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setSynchronized(true);
                    break;
                }
                case NATIVE: {
                    if (modifiers.isNative())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setNative(true);
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
            switch (stack.trim().peek().getType()) {
                case PRIVATE: {
                    if (modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPrivate(true);
                    break;
                }
                case PUBLIC: {
                    if (modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPublic(true);
                    break;
                }
                case PROTECTED: {
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
