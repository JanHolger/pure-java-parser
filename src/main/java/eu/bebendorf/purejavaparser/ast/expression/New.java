package eu.bebendorf.purejavaparser.ast.expression;

import eu.bebendorf.purejavaparser.ast.type.ClassBody;
import eu.bebendorf.purejavaparser.ast.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class New implements Expression {

    Type type;
    List<Expression> arraySizes;
    ArrayInitializer arrayInitializer;
    ArgumentList arguments;
    ClassBody anonymousBody;

    public String toString() {
        StringBuilder sb = new StringBuilder("new ").append(type.toComponentString());
        if(type.getArrayDepth() > 0) {
            for(int i=0; i<type.getArrayDepth(); i++) {
                sb.append("[");
                if(i < arraySizes.size())
                    sb.append(arraySizes.get(i));
                sb.append("]");
            }
            if(arrayInitializer != null)
                sb.append(" ").append(arrayInitializer);
        } else {
            sb.append(arguments);
            if(anonymousBody != null)
                sb.append(" ").append(anonymousBody);
        }
        return sb.toString();
    }

}
