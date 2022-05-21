package eu.bebendorf.purejavaparser.parser;

import eu.bebendorf.purejavaparser.PureJavaParser;
import eu.bebendorf.purejavaparser.ast.Annotation;
import eu.bebendorf.purejavaparser.ast.type.field.FieldDefinition;
import eu.bebendorf.purejavaparser.ast.type.field.FieldModifiers;
import eu.bebendorf.purejavaparser.ast.VariableDefinition;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.TokenType;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class FieldDefinitionParser {

    PureJavaParser parser;

    public FieldDefinition parseFieldDefinition(TokenStack stack) throws UnexpectedTokenException {
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
            switch (stack.trim().peek().getType().getName()) {
                case "STATIC": {
                    if(modifiers.isStatic())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setStatic(true);
                    break;
                }
                case "PRIVATE": {
                    if(modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPrivate(true);
                    break;
                }
                case "PUBLIC": {
                    if(modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setPublic(true);
                    break;
                }
                case "PROTECTED": {
                    if(modifiers.hasAccessModifier())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setProtected(true);
                    break;
                }
                case "TRANSIENT": {
                    if(modifiers.isTransient())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setTransient(true);
                    break;
                }
                case "FINAL": {
                    if(modifiers.isFinal())
                        throw new UnexpectedTokenException(stack.pop());
                    stack.pop();
                    modifiers.setFinal(true);
                    break;
                }
                case "VOLATILE": {
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

}
