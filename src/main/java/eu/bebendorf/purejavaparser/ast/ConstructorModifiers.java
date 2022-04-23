package eu.bebendorf.purejavaparser.ast;

import java.util.ArrayList;
import java.util.List;

public class ConstructorModifiers {

    boolean privateModifier;
    boolean publicModifier;
    boolean protectedModifier;

    public void setPrivate(boolean value) {
        this.privateModifier = value;
    }

    public void setPublic(boolean value) {
        this.publicModifier = value;
    }

    public void setProtected(boolean value) {
        this.protectedModifier = value;
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

    public String toString() {
        List<String> modifiers = new ArrayList<>();
        if(publicModifier)
            modifiers.add("public");
        if(privateModifier)
            modifiers.add("private");
        if(protectedModifier)
            modifiers.add("protected");
        return String.join(" ", modifiers);
    }

}
