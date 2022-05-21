package eu.bebendorf.purejavaparser.parser;

import eu.bebendorf.purejavaparser.PureJavaParser;
import eu.bebendorf.purejavaparser.ast.*;
import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.purejavaparser.ast.statement.*;
import eu.bebendorf.purejavaparser.ast.type.TypeDefinition;
import eu.bebendorf.purejavaparser.token.Token;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.TokenType;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal = true)
public class StatementParser {

    PureJavaParser parser;
    ControlStatementParser controlStatementParser;

    public StatementParser(PureJavaParser parser) {
        this.parser = parser;
        this.controlStatementParser = new ControlStatementParser(parser);
    }

    public StatementBlock parseStatementBlock(TokenStack stack, boolean isSwitch) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() != TokenType.OPEN_CURLY_BRACKET)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        List<Statement> statements = new ArrayList<>();
        while (stack.trim().peek().getType() != TokenType.CLOSE_CURLY_BRACKET) {
            if(stack.peek().getType() == TokenType.STATEMENT_END) {
                stack.pop();
                continue;
            }
            TokenStack stackCopy = stack.clone();
            Statement statement = parseStatement(stackCopy, true, isSwitch);
            if(isSwitch && statements.size() == 0 && !(statement instanceof CaseStatement))
                throw new UnexpectedTokenException(stack.pop());
            stack.copyFrom(stackCopy);
            statements.add(statement);
        }
        stack.pop();
        if(isSwitch)
            return new SwitchBlock(statements);
        else
            return new StatementBlock(statements);
    }

    public Statement parseStatement(TokenStack stack, boolean allowTypeDefinition, boolean allowCase) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        switch (stackCopy.peek().getType().getName()) {
            case "OPEN_CURLY_BRACKET": {
                StatementBlock block = parseStatementBlock(stackCopy, false);
                stack.copyFrom(stackCopy);
                return block;
            }
            case "IF": {
                IfStatement statement = controlStatementParser.parseIfStatement(stackCopy);
                stack.copyFrom(stackCopy);
                return statement;
            }
            case "SWITCH": {
                SwitchStatement statement = controlStatementParser.parseSwitchStatement(stackCopy);
                stack.copyFrom(stackCopy);
                return statement;
            }
            case "WHILE": {
                WhileStatement statement = controlStatementParser.parseWhileStatement(stackCopy);
                stack.copyFrom(stackCopy);
                return statement;
            }
            case "DO": {
                DoWhileStatement statement = controlStatementParser.parseDoWhileStatement(stackCopy);
                stack.copyFrom(stackCopy);
                return statement;
            }
            case "TRY": {
                TryStatement statement = controlStatementParser.parseTryStatement(stackCopy);
                stack.copyFrom(stackCopy);
                return statement;
            }
            case "RETURN": {
                ReturnStatement ret = parseReturn(stackCopy);
                Token t = stackCopy.trim().pop();
                if(t.getType() != TokenType.STATEMENT_END)
                    throw new UnexpectedTokenException(t);
                stack.copyFrom(stackCopy);
                return ret;
            }
            case "ASSERT": {
                stackCopy.pop();
                Expression assertion = parser.getExpressionParser().parseExpression(stackCopy);
                if(stackCopy.trim().peek().getType() != TokenType.STATEMENT_END)
                    throw new UnexpectedTokenException(stackCopy.pop());
                stackCopy.pop();
                stack.copyFrom(stackCopy);
                return new AssertStatement(assertion);
            }
            case "BREAK": {
                stackCopy.pop();
                String label = null;
                if(stackCopy.trim().peek().getType() == TokenType.NAME)
                    label = stackCopy.pop().getValue();
                if(stackCopy.trim().peek().getType() != TokenType.STATEMENT_END)
                    throw new UnexpectedTokenException(stackCopy.pop());
                stackCopy.pop();
                stack.copyFrom(stackCopy);
                return new BreakStatement(label);
            }
            case "CONTINUE": {
                stackCopy.pop();
                String label = null;
                if(stackCopy.trim().peek().getType() == TokenType.NAME)
                    label = stackCopy.pop().getValue();
                if(stackCopy.trim().peek().getType() != TokenType.STATEMENT_END)
                    throw new UnexpectedTokenException(stackCopy.pop());
                stackCopy.pop();
                stack.copyFrom(stackCopy);
                return new ContinueStatement(label);
            }
            case "THROW": {
                stackCopy.pop();
                Expression value = parser.getExpressionParser().parseExpression(stackCopy);
                if(stackCopy.trim().peek().getType() != TokenType.STATEMENT_END)
                    throw new UnexpectedTokenException(stackCopy.pop());
                stackCopy.pop();
                stack.copyFrom(stackCopy);
                return new ThrowStatement(value);
            }
            case "CASE": {
                if(!allowCase)
                    throw new UnexpectedTokenException(stackCopy.pop());
                stackCopy.pop();
                Expression value = parser.getExpressionParser().parseExpression(stackCopy);
                if(stackCopy.trim().peek().getType() != TokenType.COLON)
                    throw new UnexpectedTokenException(stackCopy.pop());
                stackCopy.pop();
                stack.copyFrom(stackCopy);
                return new CaseStatement(value);
            }
            case "DEFAULT": {
                if(!allowCase)
                    throw new UnexpectedTokenException(stackCopy.pop());
                stackCopy.pop();
                if(stackCopy.trim().peek().getType() != TokenType.COLON)
                    throw new UnexpectedTokenException(stackCopy.pop());
                stackCopy.pop();
                stack.copyFrom(stackCopy);
                return new CaseStatement(null);
            }
        }
        UnexpectedTokenException ex;
        stackCopy = stack.clone();
        try {
            if(stackCopy.trim().peek().getType() != TokenType.NAME)
                throw new UnexpectedTokenException(stackCopy.pop());
            String name = stackCopy.pop().getValue();
            if(stackCopy.trim().peek().getType() != TokenType.COLON)
                throw new UnexpectedTokenException(stackCopy.pop());
            stack.copyFrom(stackCopy);
            return new LabelStatement(name);
        } catch (UnexpectedTokenException nextEx) {
            ex = nextEx;
        }
        stackCopy = stack.clone();
        try {
            VariableDefinition vd = parser.getGeneralParser().parseVariableDefinition(stackCopy, true, true, false);
            Token t = stackCopy.trim().pop();
            if(t.getType() != TokenType.STATEMENT_END)
                throw new UnexpectedTokenException(t);
            stack.copyFrom(stackCopy);
            return vd;
        } catch (UnexpectedTokenException nextEx) {
            ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
        }
        stackCopy = stack.clone();
        try {
            Assignment assignment = parseAssignment(stackCopy);
            Token t = stackCopy.trim().pop();
            if(t.getType() != TokenType.STATEMENT_END)
                throw new UnexpectedTokenException(t);
            stack.copyFrom(stackCopy);
            return assignment;
        } catch (UnexpectedTokenException nextEx) {
            ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
        }
        stackCopy = stack.clone();
        if(allowTypeDefinition) {
            try {
                TypeDefinition typeDefinition = parser.getClassParser().getTypeDefinitionParser().parseTypeDefinition(stackCopy, true);
                stack.copyFrom(stackCopy);
                return typeDefinition;
            } catch (UnexpectedTokenException nextEx) {
                ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
            }
            stackCopy = stack.clone();
        }
        try {
            Expression expression = parser.getExpressionParser().parseExpression(stackCopy);
            Token t = stackCopy.trim().pop();
            if(t.getType() != TokenType.STATEMENT_END)
                throw new UnexpectedTokenException(t);
            stack.copyFrom(stackCopy);
            return (Statement) expression;
        } catch (UnexpectedTokenException nextEx) {
            ex = nextEx.getToken().getPos() > ex.getToken().getPos() ? nextEx : ex;
        }
        throw ex;
    }

    private ReturnStatement parseReturn(TokenStack stack) throws UnexpectedTokenException {
        Token t = stack.trim().pop();
        if(t.getType() != TokenType.RETURN)
            throw new UnexpectedTokenException(t);
        TokenStack stackCopy = stack.clone();
        try {
            Expression expression = parser.getExpressionParser().parseExpression(stackCopy);
            stack.copyFrom(stackCopy);
            return new ReturnStatement(expression);
        } catch (UnexpectedTokenException e) {
            return new ReturnStatement();
        }
    }

    private Assignment parseAssignment(TokenStack stack) throws UnexpectedTokenException {
        Assignable destination = parseAssignable(stack);
        Token t = stack.trim().pop();
        if(t.getType() != TokenType.ASSIGN_OP)
            throw new UnexpectedTokenException(t);
        Expression value = parser.getExpressionParser().parseExpression(stack);
        return new Assignment(destination, value);
    }

    private Assignable parseAssignable(TokenStack stack) throws UnexpectedTokenException {
        TokenStack stackCopy = stack.trim().clone();
        Variable variable = parser.getGeneralParser().parseVariable(stackCopy);
        stack.copyFrom(stackCopy);
        return variable;
    }

}
