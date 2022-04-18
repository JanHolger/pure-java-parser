package eu.bebendorf.purejavaparser.util;

public class PrintUtil {

    public static String prefixLines(String s, String prefix) {
        return prefix + String.join("\n" + prefix, s.split("\n"));
    }

}
