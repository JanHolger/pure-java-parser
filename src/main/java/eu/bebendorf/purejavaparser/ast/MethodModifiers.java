package eu.bebendorf.purejavaparser.ast;

import java.util.ArrayList;
import java.util.List;

public class MethodModifiers {

    boolean staticModifier;
    boolean privateModifier;
    boolean publicModifier;
    boolean protectedModifier;
    boolean finalModifier;
    boolean strictfpModifier;
    boolean abstractModifier;
    boolean synchronizedModifier;
    boolean nativeModifier;

    public void setStatic(boolean value) {
        this.staticModifier = value;
    }

    public void setPrivate(boolean value) {
        this.privateModifier = value;
    }

    public void setPublic(boolean value) {
        this.publicModifier = value;
    }

    public void setProtected(boolean value) {
        this.protectedModifier = value;
    }

    public void setFinal(boolean value) {
        this.finalModifier = value;
    }

    public void setStrictfp(boolean value) {
        this.strictfpModifier = value;
    }

    public void setAbstract(boolean value) {
        this.abstractModifier = value;
    }

    public void setSynchronized(boolean value) {
        this.synchronizedModifier = value;
    }

    public void setNative(boolean value) {
        this.nativeModifier = value;
    }

    public boolean isStatic() {
        return staticModifier;
    }

    public boolean isPrivate() {
        return privateModifier;
    }

    public boolean isPublic() {
        return publicModifier;
    }

    public boolean isProtected() {
        return protectedModifier;
    }

    public boolean hasAccessModifier() {
        return privateModifier || publicModifier || protectedModifier;
    }

    public boolean isFinal() {
        return finalModifier;
    }

    public boolean isStrictfp() {
        return strictfpModifier;
    }

    public boolean isAbstract() {
        return abstractModifier;
    }

    public boolean isSynchronized() {
        return synchronizedModifier;
    }

    public boolean isNative() {
        return nativeModifier;
    }

    public String toString() {
        List<String> modifiers = new ArrayList<>();
        if(publicModifier)
            modifiers.add("public");
        if(privateModifier)
            modifiers.add("private");
        if(protectedModifier)
            modifiers.add("protected");
        if(staticModifier)
            modifiers.add("static");
        if(finalModifier)
            modifiers.add("final");
        if(abstractModifier)
            modifiers.add("abstract");
        if(nativeModifier)
            modifiers.add("native");
        if(synchronizedModifier)
            modifiers.add("synchronized");
        if(strictfpModifier)
            modifiers.add("strictfp");
        return String.join(" ", modifiers);
    }

}
