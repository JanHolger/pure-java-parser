# pure-java-parser
A Java parser written in pure java (no jdk required, zero dependencies) that generates a syntax tree from source code

## Usage
For parsing an entire class file it's as simple as initializing a parser and calling a single method.
```java
public class SimpleExample {
    public static void main(String[] args) throws UnexpectedCharacterException, UnexpectedTokenException {
        String source = "class MyClass {}"; // Example source code
        PureJavaParser parser = new PureJavaParser(); // Initialize a new parser
        ClassFileDefinition classFile = parser.parse("MyClass.java", source); // Parse the source into an ast
        System.out.println(classFile.toString()); // Generate and print source code for visualization
    }
}
```
Most language constructs can also be manually parsed. This can be useful if you only want to parse a single expression for example.

```java
public class ExpressionExample {
    public static void main(String[] args) throws UnexpectedCharacterException, UnexpectedTokenException {
        String source = "a + b * c"; // Example source code
        PureJavaParser parser = new PureJavaParser(); // Initialize a new parser
        TokenStack stack = Tokenizer.tokenize("MyClass.java", source); // Manually tokenize the source
        Expression expression = parser.getExpressionParser().parseExpression(stack); // Parse the source into an ast
        if (stack.trim().peek().getType() != TokenType.EOF) // Manually check if the end of input is reached
            throw new UnexpectedTokenException(stack.pop()); // Throw an error in case there are unconsumed tokens
        System.out.println(expression.toString()); // Generate and print source code for visualization
    }
}
```

## TODO
These are features that are known to be missing
- Ternary Operator
- Type casting
- Generic type definition for classes and methods
- Instance and static initializers