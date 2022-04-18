package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Return implements Statement {

    Expression value;

    public String toString() {
        return "return" + (value != null ? (" " + value) : "");
    }

}
