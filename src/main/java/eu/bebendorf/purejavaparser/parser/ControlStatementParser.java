package eu.bebendorf.purejavaparser.parser;

import eu.bebendorf.purejavaparser.PureJavaParser;
import eu.bebendorf.purejavaparser.ast.*;
import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.purejavaparser.ast.statement.*;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.TokenType;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ControlStatementParser {

    PureJavaParser parser;

    public IfStatement parseIfStatement(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() != TokenType.IF)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression condition = parser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Statement statement = parseStatementOrEnd(stack);
        Statement elseStatement = null;
        if(stack.trim().peek().getType() == TokenType.ELSE) {
            stack.pop();
            elseStatement = parseStatementOrEnd(stack);
        }
        return new IfStatement(condition, ensureBlock(statement), ensureBlock(elseStatement));
    }

    public WhileStatement parseWhileStatement(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() != TokenType.WHILE)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression condition = parser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Statement statement = parseStatementOrEnd(stack);
        return new WhileStatement(condition, ensureBlock(statement));
    }

    public DoWhileStatement parseDoWhileStatement(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() != TokenType.DO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Statement statement = parseStatementOrEnd(stack);
        if(stack.trim().peek().getType() != TokenType.WHILE)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression condition = parser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.STATEMENT_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        return new DoWhileStatement(ensureBlock(statement), condition);
    }

    public TryStatement parseTryStatement(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() != TokenType.TRY)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        List<VariableDefinition> resources = new ArrayList<>();
        if(stack.trim().peek().getType() == TokenType.GROUP_START) {
            stack.pop();
            while (stack.trim().peek().getType() != TokenType.GROUP_END) {
                if(resources.size() > 0) {
                    if(stack.peek().getType() != TokenType.STATEMENT_END)
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                }
                resources.add(parser.getGeneralParser().parseVariableDefinition(stack, true, true, true));
            }
            stack.pop();
        }
        StatementBlock tryStatement = parser.getStatementParser().parseStatementBlock(stack);
        List<CatchStatement> catchStatements = new ArrayList<>();
        while (stack.trim().peek().getType() == TokenType.CATCH) {
            stack.pop();
            if(stack.trim().peek().getType() != TokenType.GROUP_START)
                throw new UnexpectedTokenException(stack.pop());
            stack.pop();
            List<Type> types = new ArrayList<>();
            types.add(parser.getGeneralParser().parseType(stack, false, false, false));
            while (stack.trim().peek().is(TokenType.BITWISE_OP, "|")) {
                stack.pop();
                types.add(parser.getGeneralParser().parseType(stack, false, false, false));
            }
            Variable variable = parser.getGeneralParser().parseVariable(stack);
            if(stack.trim().peek().getType() != TokenType.GROUP_END)
                throw new UnexpectedTokenException(stack.pop());
            stack.pop();
            StatementBlock statement = parser.getStatementParser().parseStatementBlock(stack);
            catchStatements.add(new CatchStatement(types, variable, statement));
        }
        StatementBlock finallyStatement = null;
        if(stack.trim().peek().getType() == TokenType.FINALLY) {
            stack.pop();
            finallyStatement = parser.getStatementParser().parseStatementBlock(stack);
        }
        return new TryStatement(resources, tryStatement, catchStatements, finallyStatement);
    }

    private Statement parseStatementOrEnd(TokenStack stack) throws UnexpectedTokenException {
        if(stack.trim().peek().getType() == TokenType.STATEMENT_END) {
            stack.pop();
            return null;
        }
        return parser.getStatementParser().parseStatement(stack, false);
    }

    private StatementBlock ensureBlock(Statement statement) {
        if(statement instanceof StatementBlock) {
            return (StatementBlock) statement;
        } else {
            List<Statement> statements = new ArrayList<>();
            if(statement != null)
                statements.add(statement);
            return new StatementBlock(statements);
        }
    }

}
