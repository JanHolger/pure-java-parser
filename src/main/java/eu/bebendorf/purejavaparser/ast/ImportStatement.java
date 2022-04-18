package eu.bebendorf.purejavaparser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ImportStatement {

    List<String> name;
    boolean wildcard;
    boolean staticImport;

    public String toString() {
        return "import " + (staticImport ? "static " : "") + String.join(".", name) + (wildcard ? ".*" : "") + ";";
    }

}
