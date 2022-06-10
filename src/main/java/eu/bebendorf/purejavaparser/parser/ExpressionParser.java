package eu.bebendorf.purejavaparser.parser;

import eu.bebendorf.purejavaparser.PureJavaParser;
import eu.bebendorf.purejavaparser.ast.*;
import eu.bebendorf.purejavaparser.ast.expression.*;
import eu.bebendorf.purejavaparser.ast.statement.ReturnStatement;
import eu.bebendorf.purejavaparser.ast.statement.Statement;
import eu.bebendorf.purejavaparser.ast.statement.StatementBlock;
import eu.bebendorf.purejavaparser.ast.type.ClassBody;
import eu.bebendorf.purejavaparser.token.Token;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.TokenType;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@AllArgsConstructor
public class ExpressionParser {

    PureJavaParser parser;

    public Expression parseExpression(TokenStack stack) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        Expression expression = tryExpandExpressionTo(stackCopy, resolveGroup(stackCopy), Expansion.TERNARY);
        stack.copyFrom(stackCopy);
        return expression;
    }

    private Expression parseLambdaExpression(TokenStack stack) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        ParameterList parameters;
        if(stackCopy.peek().getType() == TokenType.GROUP_START) {
            parameters = parseParameterList(stackCopy);
        } else if(stackCopy.peek().getType() == TokenType.NAME) {
            Variable variable = parser.getGeneralParser().parseVariable(stackCopy);
            List<ParameterDefinition> definitions = new ArrayList<>();
            definitions.add(new ParameterDefinition(variable));
            parameters = new ParameterList(definitions);
        } else {
            throw new UnexpectedTokenException(stackCopy.pop());
        }
        if(stackCopy.trim().peek().getType() != TokenType.LAMBDA_ARROW)
            throw new UnexpectedTokenException(stackCopy.pop());
        stackCopy.pop();
        StatementBlock body;
        if(stackCopy.trim().peek().getType() == TokenType.OPEN_CURLY_BRACKET) {
            body = parser.getStatementParser().parseStatementBlock(stackCopy, false);
        } else {
            Expression expression = parseExpression(stackCopy);
            List<Statement> statements = new ArrayList<>();
            statements.add(new ReturnStatement(expression));
            body = new StatementBlock(statements);
        }
        stack.copyFrom(stackCopy);
        return new Lambda(parameters, body);
    }

    private Expression parseCastExpression(TokenStack stack) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        if(stackCopy.peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stackCopy.pop());
        stackCopy.pop();
        Type type = parser.getGeneralParser().parseType(stackCopy, true, true, false);
        if(stackCopy.peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stackCopy.pop());
        stackCopy.pop();
        Expression value = parseExpression(stackCopy);
        stack.copyFrom(stackCopy);
        return new Cast(type, value);
    }

    private ParameterList parseParameterList(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().pop().getType() != TokenType.GROUP_START)
            return null;
        List<ParameterDefinition> parameters = new ArrayList<>();
        while (stack.trim().peek().getType() != TokenType.GROUP_END) {
            if(parameters.size() > 0) {
                Token t = stack.trim().pop();
                if(t.getType() != TokenType.SEPERATOR)
                    throw new UnexpectedTokenException(t);
            }
            ParameterDefinition param = parseParameterDefinition(stack);
            parameters.add(param);
        }
        stack.pop();
        return new ParameterList(parameters);
    }

    private ParameterDefinition parseParameterDefinition(TokenStack stack) throws UnexpectedTokenException {
        return new ParameterDefinition(parser.getGeneralParser().parseVariable(stack));
    }

    private Expression resolveGroup(TokenStack stack) throws UnexpectedTokenException {
        if (stack.trim().peek().getType() == TokenType.GROUP_START) {
            UnexpectedTokenException ex;
            try {
                TokenStack stackCopy = stack.clone();
                Expression l = parseLambdaExpression(stackCopy);
                stack.copyFrom(stackCopy);
                return l;
            } catch (UnexpectedTokenException nextEx) {
                ex = nextEx;
            }
            try {
                TokenStack stackCopy = stack.clone();
                Expression l = parseCastExpression(stackCopy);
                stack.copyFrom(stackCopy);
                return l;
            } catch (UnexpectedTokenException nextEx) {
                ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
            }
            try {
                stack.pop();
                Expression expression = parseExpression(stack);
                if (stack.trim().peek().getType() != TokenType.GROUP_END)
                    throw new UnexpectedTokenException(stack.pop());
                stack.pop();
                return new ExpressionGroup(expression);
            } catch (UnexpectedTokenException nextEx) {
                throw nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
            }
        } else if(stack.peek().getType() == TokenType.NEW) {
            return parseNewExpression(stack);
        } else if(stack.peek().getType() == TokenType.NOT_OP) {
            TokenStack stackCopy = stack.clone();
            stackCopy.pop();
            Expression exp = resolveGroup(stackCopy);
            stack.copyFrom(stackCopy);
            return new Not(exp);
        } else if(stack.peek().getType() == TokenType.UNARY_OP) {
            TokenStack stackCopy = stack.clone();
            stackCopy.pop();
            Expression exp = resolveGroup(stackCopy);
            stack.copyFrom(stackCopy);
            return new Unary(exp);
        } else {
            return parseTerminalExpression(stack);
        }
    }

    private Expression tryExpandTernaryExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.peek().getType() == TokenType.TERNARY_OP) {
            stack.pop();
            Expression first = tryExpandExpressionTo(stack, resolveGroup(stack), Expansion.TERNARY);
            if(stack.trim().peek().getType() != TokenType.COLON)
                throw new UnexpectedTokenException(stack.pop());
            stack.pop();
            Expression second = tryExpandExpressionTo(stack, resolveGroup(stack), Expansion.TERNARY);
            return new Ternary(left, first, second);
        }
        return left;
    }

    private Expression tryExpandLogicalOrExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.trim().peek().is(TokenType.LOGICAL_OP, "||")) {
            stack.pop();
            return tryExpandExpressionTo(stack, new LogicalOr(left, resolveGroup(stack)), Expansion.LOGICAL_OR);
        }
        return left;
    }

    private Expression tryExpandLogicalAndExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.trim().peek().is(TokenType.LOGICAL_OP, "&&")) {
            stack.pop();
            return tryExpandExpressionTo(stack, new LogicalAnd(left, resolveGroup(stack)), Expansion.LOGICAL_AND);
        }
        return left;
    }

    private Expression tryExpandBitwiseOrExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.trim().peek().is(TokenType.BITWISE_OP, "|")) {
            stack.pop();
            return tryExpandExpressionTo(stack, new BitwiseOr(left, resolveGroup(stack)), Expansion.BITWISE_OR);
        }
        return left;
    }

    private Expression tryExpandBitwiseXorExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.trim().peek().is(TokenType.BITWISE_OP, "^")) {
            stack.pop();
            return tryExpandExpressionTo(stack, new BitwiseXor(left, resolveGroup(stack)), Expansion.BITWISE_XOR);
        }
        return left;
    }

    private Expression tryExpandBitwiseAndExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.trim().peek().is(TokenType.BITWISE_OP, "&")) {
            stack.pop();
            return tryExpandExpressionTo(stack, new BitwiseAnd(left, resolveGroup(stack)), Expansion.BITWISE_AND);
        }
        return left;
    }

    private Expression tryExpandEqualityExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() == TokenType.EQUALITY_OP) {
            Token t = stack.pop();
            return tryExpandExpressionTo(stack, new Comparison(t.getValue(), left, resolveGroup(stack)), Expansion.EQUALITY);
        }
        return left;
    }

    private Expression tryExpandRelationalExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() == TokenType.INSTANCEOF) {
            stack.pop();
            Type type = parser.getGeneralParser().parseType(stack, false, true, false);
            return new InstanceOf(left, type);
        }
        if(stack.peek().getType() == TokenType.RELATIONAL_OP) {
            Token t = stack.pop();
            return tryExpandExpressionTo(stack, new Comparison(t.getValue(), left, resolveGroup(stack)), Expansion.RELATIONAL);
        }
        return left;
    }

    private Expression tryExpandShiftExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() == TokenType.SHIFT_OP) {
            Token t = stack.peek();
            switch (t.getValue()) {
                case "<<": {
                    stack.pop();
                    return tryExpandExpressionTo(stack, new ShiftLeft(left, resolveGroup(stack)), Expansion.SHIFT);
                }
                case ">>": {
                    stack.pop();
                    return tryExpandExpressionTo(stack, new ShiftRight(left, resolveGroup(stack)), Expansion.SHIFT);
                }
                case ">>>": {
                    stack.pop();
                    return tryExpandExpressionTo(stack, new LogicalShiftRight(left, resolveGroup(stack)), Expansion.SHIFT);
                }
            }
        }
        return left;
    }

    private Expression tryExpandAdditiveExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() == TokenType.ARITHMETIC_OP) {
            Token t = stack.peek();
            switch (t.getValue()) {
                case "+": {
                    stack.pop();
                    return tryExpandExpressionTo(stack, new Addition(left, resolveGroup(stack)), Expansion.ADDITIVE);
                }
                case "-": {
                    stack.pop();
                    return tryExpandExpressionTo(stack, new Subtraction(left, resolveGroup(stack)), Expansion.ADDITIVE);
                }
            }
        }
        return left;
    }

    private Expression tryExpandMultiplicativeExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.peek().getType() == TokenType.ARITHMETIC_OP) {
            Token t = stack.peek();
            switch (t.getValue()) {
                case "*": {
                    stack.pop();
                    return tryExpandExpressionTo(stack, new Multiplication(left, resolveGroup(stack)), Expansion.MULTIPLICATIVE);
                }
                case "/": {
                    stack.pop();
                    return tryExpandExpressionTo(stack, new Division(left, resolveGroup(stack)), Expansion.MULTIPLICATIVE);
                }
                case "%": {
                    stack.pop();
                    return tryExpandExpressionTo(stack, new Modulo(left, resolveGroup(stack)), Expansion.MULTIPLICATIVE);
                }
            }
        }
        return left;
    }

    private Expression tryExpandPostfixExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        if(stack.peek().getType() == TokenType.INCREMENTAL_OP) {
            Token t = stack.peek();
            switch (t.getValue()) {
                case "++": {
                    stack.pop();
                    return new PostfixIncrement(left);
                }
                case "--": {
                    stack.pop();
                    return new PostfixDecrement(left);
                }
            }
        }
        return left;
    }

    private Expression tryExpandAccessExpression(TokenStack stack, Expression left) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        if(stackCopy.peek().getType() == TokenType.PROPERTY_ACCESS) {
            stackCopy.pop();
            if(stackCopy.trim().peek().getType() == TokenType.NAME) {
                Expression newTerminal = new PropertyAccess(left, stackCopy.pop().getValue());
                stack.copyFrom(stackCopy);
                return tryExpandAccessExpression(stack, newTerminal);
            } else {
                throw new UnexpectedTokenException(stackCopy.pop());
            }
        }
        if(stackCopy.peek().getType() == TokenType.ARRAY_START) {
            stackCopy.pop();
            Expression index = parseExpression(stackCopy);
            if(stackCopy.trim().peek().getType() != TokenType.ARRAY_END)
                throw new UnexpectedTokenException(stackCopy.pop());
            Expression newTerminal = new ArrayAccess(left, index);
            stack.copyFrom(stackCopy);
            return tryExpandAccessExpression(stack, newTerminal);
        }
        if(stackCopy.peek().getType() == TokenType.GROUP_START) {
            ArgumentList argumentList = parseArgumentList(stackCopy);
            Expression newTerminal = new MethodCall(left, argumentList);
            stack.copyFrom(stackCopy);
            return tryExpandAccessExpression(stack, newTerminal);
        }
        return left;
    }

    private Expression tryExpandExpressionTo(TokenStack stack, Expression expression, Expansion to) throws UnexpectedTokenException {
        expression = tryExpandAccessExpression(stack, expression);
        if(to == Expansion.ACCESS)
            return expression;
        expression = tryExpandPostfixExpression(stack, expression);
        if(to == Expansion.POSTFIX)
            return expression;
        expression = tryExpandMultiplicativeExpression(stack, expression);
        if(to == Expansion.MULTIPLICATIVE)
            return expression;
        expression = tryExpandAdditiveExpression(stack, expression);
        if(to == Expansion.ADDITIVE)
            return expression;
        expression = tryExpandShiftExpression(stack, expression);
        if(to == Expansion.SHIFT)
            return expression;
        expression = tryExpandRelationalExpression(stack, expression);
        if(to == Expansion.RELATIONAL)
            return expression;
        expression = tryExpandEqualityExpression(stack, expression);
        if(to == Expansion.EQUALITY)
            return expression;
        expression = tryExpandBitwiseAndExpression(stack, expression);
        if(to == Expansion.BITWISE_AND)
            return expression;
        expression = tryExpandBitwiseXorExpression(stack, expression);
        if(to == Expansion.BITWISE_XOR)
            return expression;
        expression = tryExpandBitwiseOrExpression(stack, expression);
        if(to == Expansion.BITWISE_OR)
            return expression;
        expression = tryExpandLogicalAndExpression(stack, expression);
        if(to == Expansion.LOGICAL_AND)
            return expression;
        expression = tryExpandLogicalOrExpression(stack, expression);
        if(to == Expansion.LOGICAL_OR)
            return expression;
        return tryExpandTernaryExpression(stack, expression);
    }

    private enum Expansion {
        ACCESS,
        POSTFIX,
        MULTIPLICATIVE,
        ADDITIVE,
        SHIFT,
        RELATIONAL,
        EQUALITY,
        BITWISE_AND,
        BITWISE_XOR,
        BITWISE_OR,
        LOGICAL_AND,
        LOGICAL_OR,
        TERNARY
    }

    private Expression parseTerminalExpression(TokenStack stack) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.clone();
        try {
            Expression literal = parseLiteral(stackCopy);
            stack.copyFrom(stackCopy);
            return literal;
        } catch (UnexpectedTokenException ex) {
            return parser.getGeneralParser().parseVariable(stack);
        }
    }

    private New parseNewExpression(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() != TokenType.NEW)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Type type = parser.getGeneralParser().parseType(stack, true, false, false);
        ArgumentList argumentList = null;
        int arrayDepth = 0;
        List<Expression> arraySizes = new ArrayList<>();
        ArrayInitializer arrayInitializer = null;
        ClassBody anonymousBody = null;
        if(stack.trim().peek().getType() == TokenType.ARRAY_START) {
            while (stack.trim().peek().getType() == TokenType.ARRAY_START) {
                if(stack.trim().peek().getType() != TokenType.ARRAY_END && (arrayDepth == 0 || arraySizes.size() > 0)) {
                    Expression size = parseExpression(stack);
                    arraySizes.add(size);
                }
                if(stack.trim().peek().getType() != TokenType.ARRAY_END)
                    throw new UnexpectedTokenException(stack.pop());
                stack.pop();
                arrayDepth++;
            }
            if(arraySizes.size() == 0)
                arrayInitializer = parseArrayInitializer(stack, arrayDepth);
            type = new Type(type.getName(), type.getGenericTypes(), type.getExtend(), arrayDepth, false);
        } else {
            argumentList = parseArgumentList(stack);
            if(stack.trim().peek().getType() == TokenType.OPEN_CURLY_BRACKET)
                anonymousBody = (ClassBody) parser.getClassParser().getTypeDefinitionParser().parseTypeBody(stack, null, true, true, false, false);
        }
        return new New(type, arraySizes, arrayInitializer, argumentList, anonymousBody);
    }

    private ArrayInitializer parseArrayInitializer(TokenStack stack, int depth) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() != TokenType.OPEN_CURLY_BRACKET)
            throw new UnexpectedTokenException(stack.pop());
        List<Expression> values = new ArrayList<>();
        while (stack.trim().peek().getType() != TokenType.CLOSE_CURLY_BRACKET) {
            if(values.size() > 0) {
                if(stack.trim().peek().getType() != TokenType.SEPERATOR)
                    throw new UnexpectedTokenException(stack.pop());
                stack.pop();
            }
            if(depth > 1) {
                values.add(parseArrayInitializer(stack, depth - 1));
            } else {
                UnexpectedTokenException ex = null;
                if(depth < 1) {
                    try {
                        TokenStack stackCopy = stack.clone();
                        values.add(parseArrayInitializer(stackCopy, depth));
                        stack.copyFrom(stackCopy);
                        continue;
                    } catch (UnexpectedTokenException nextEx) {
                        ex = nextEx;
                    }
                }
                try {
                    values.add(parseExpression(stack));
                } catch (UnexpectedTokenException nextEx) {
                    throw (ex == null || (nextEx.getToken().getPos() > ex.getToken().getPos())) ? nextEx : ex;
                }
            }
        }
        stack.pop();
        return new ArrayInitializer(values);
    }

    public Expression parseLiteral(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() == TokenType.NULL_LITERAL) {
            stack.pop();
            return NullLiteral.INSTANCE;
        }
        if(stack.peek().getType() == TokenType.BOOLEAN_LITERAL) {
            String value = stack.pop().getValue();
            return value.equals("true") ? BooleanLiteral.TRUE : BooleanLiteral.FALSE;
        }
        TokenStack stackCopy = stack.clone();
        String s = "";
        if(stackCopy.peek().is(TokenType.ARITHMETIC_OP, "+"))
            stackCopy.pop();
        else if(stackCopy.peek().is(TokenType.ARITHMETIC_OP, "-"))
            s = stackCopy.pop().getValue();
        if(stackCopy.peek().getType() == TokenType.DECIMAL_INT_LITERAL || stackCopy.peek().getType() == TokenType.HEX_INT_LITERAL || stackCopy.peek().getType() == TokenType.BINARY_INT_LITERAL) {
            s += stackCopy.pop().getValue().replace("_", "").toLowerCase(Locale.ROOT);
            switch (s.charAt(s.length()-1)) {
                case 'd':
                    DoubleLiteral d = DoubleLiteral.fromString(s.substring(0, s.length()-1));
                    if(d != null) {
                        stack.copyFrom(stackCopy);
                        return d;
                    }
                    break;
                case 'f':
                    FloatLiteral f = FloatLiteral.fromString(s.substring(0, s.length()-1));
                    if(f != null) {
                        stack.copyFrom(stackCopy);
                        return f;
                    }
                    break;
                case 'l':
                    LongLiteral l = LongLiteral.fromString(s.substring(0, s.length()-1));
                    if(l != null) {
                        stack.copyFrom(stackCopy);
                        return l;
                    }
                    break;
                default:
                    IntegerLiteral i = IntegerLiteral.fromString(s);
                    if(i != null) {
                        stack.copyFrom(stackCopy);
                        return i;
                    }
                    break;
            }
        }
        if(stackCopy.peek().getType() == TokenType.DECIMAL_FLOAT_LITERAL) {
            s += stackCopy.pop().getValue().replace("_", "").toLowerCase(Locale.ROOT);
            if(s.charAt(s.length()-1) == 'f') {
                FloatLiteral f = FloatLiteral.fromString(s.substring(0, s.length()-1));
                if(f != null) {
                    stack.copyFrom(stackCopy);
                    return f;
                }
            } else {
                DoubleLiteral d = DoubleLiteral.fromString(s.charAt(s.length() - 1) == 'd' ? s.substring(0, s.length()-1) : s);
                if(d != null) {
                    stack.copyFrom(stackCopy);
                    return d;
                }
            }
        }
        if(stack.peek().getType() == TokenType.STRING_LITERAL) {
            String value = stack.pop().getValue();
            return new StringLiteral(unescapeStringLiteral(value.substring(1, value.length() - 1)));
        }
        if(stack.peek().getType() == TokenType.CHAR_LITERAL) {
            String value = stack.pop().getValue();
            return new CharLiteral(unescapeCharLiteral(value.substring(1, value.length() - 1)));
        }
        if(stack.peek().getType() == TokenType.NAME) {
            stackCopy = stack.trim().clone();
            ClassLiteral classLiteral = parseClassLiteral(stackCopy);
            stack.copyFrom(stackCopy);
            return classLiteral;
        }
        throw new UnexpectedTokenException(stack.pop());
    }

    private ClassLiteral parseClassLiteral(TokenStack stack) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        if(stackCopy.peek().getType() != TokenType.NAME)
            throw new UnexpectedTokenException(stackCopy.pop());
        List<String> parts = new ArrayList<>();
        parts.add(stackCopy.pop().getValue());
        while (true) {
            if(stackCopy.trim().peek().getType() != TokenType.PROPERTY_ACCESS)
                throw new UnexpectedTokenException(stackCopy.pop());
            stackCopy.pop();
            if(stackCopy.trim().peek().getType() == TokenType.CLASS) {
                stackCopy.pop();
                stack.copyFrom(stackCopy);
                return new ClassLiteral(parts);
            }
            if(stackCopy.peek().getType() != TokenType.NAME)
                throw new UnexpectedTokenException(stackCopy.pop());
            parts.add(stackCopy.pop().getValue());
        }
    }

    public ArgumentList parseArgumentList(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        List<Expression> arguments = new ArrayList<>();
        while (stack.trim().peek().getType() != TokenType.GROUP_END) {
            if(arguments.size() > 0) {
                if(stack.trim().pop().getType() != TokenType.SEPERATOR)
                    throw new UnexpectedTokenException(stack.pop());
            }
            Expression arg = parseExpression(stack);
            arguments.add(arg);
        }
        stack.pop();
        return new ArgumentList(arguments);
    }

    private char unescapeCharLiteral(String s) {
        switch (s) {
            case "\\'":
                return '\'';
            case "\\n":
                return '\n';
            case "\\r":
                return '\r';
            case "\\t":
                return '\t';
            case "\\b":
                return '\b';
            case "\\f":
                return '\f';
            case "\\0":
                return '\0';
        }
        return s.charAt(0);
    }

    private String unescapeStringLiteral(String s) {
        return s
                .replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\r", "\r")
                .replace("\\f", "\f")
                .replace("\\b", "\b")
                .replace("\\0", "\0");
    }

}
