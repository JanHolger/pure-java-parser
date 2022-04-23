package eu.bebendorf.purejavaparser.ast.type.field;

import java.util.ArrayList;
import java.util.List;

public class FieldModifiers {

    boolean staticModifier;
    boolean privateModifier;
    boolean publicModifier;
    boolean protectedModifier;
    boolean finalModifier;
    boolean volatileModifier;
    boolean transientModifier;

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

    public void setVolatile(boolean value) {
        this.volatileModifier = value;
    }

    public void setTransient(boolean value) {
        this.transientModifier = value;
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

    public boolean isVolatile() {
        return volatileModifier;
    }

    public boolean isTransient() {
        return transientModifier;
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
        if(transientModifier)
            modifiers.add("transient");
        if(volatileModifier)
            modifiers.add("volatile");
        return String.join(" ", modifiers);
    }

}
