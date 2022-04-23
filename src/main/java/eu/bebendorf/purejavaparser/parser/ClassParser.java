package eu.bebendorf.purejavaparser.parser;

import eu.bebendorf.purejavaparser.PureJavaParser;
import eu.bebendorf.purejavaparser.ast.*;
import eu.bebendorf.purejavaparser.ast.type.ClassFileDefinition;
import eu.bebendorf.purejavaparser.ast.type.TypeDefinition;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.TokenType;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal = true)
@Getter
public class ClassParser {

    PureJavaParser parser;
    TypeDefinitionParser typeDefinitionParser;

    public ClassParser(PureJavaParser parser) {
        this.parser = parser;
        this.typeDefinitionParser = new TypeDefinitionParser(parser);
    }

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
            classes.add(typeDefinitionParser.parseTypeDefinition(stack, false));
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

}
