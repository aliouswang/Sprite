package com.aliouswang.sprite.http.processor.inject;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by geminiwen on 15/5/21.
 */
public class APIClassInjector {

    private final String classPackage;
    private final String className;
    private final String targetClass;
    private final boolean isInterface;
    private final Set<ApiMethodInjector> methods;

    public APIClassInjector(String classPackage, String className, String targetClass, boolean isInterface) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetClass = targetClass;
        this.isInterface = isInterface;
        this.methods = new LinkedHashSet<>();
    }

    public void addMethod(ApiMethodInjector e) {
        methods.add(e);
    }

    public String getFqcn() {
        return classPackage + "." + className;
    }

    public String brewJava() throws Exception{
        StringBuilder builder = new StringBuilder("package " + this.classPackage + ";\n");
        builder.append("import com.aliouswang.sprite.http.library.*;\n");

        String action = this.isInterface ? "implements" : "extends";

        builder.append("public class " + this.className + " " + action + " " + this.targetClass + " {\n");
        for (ApiMethodInjector methodInjector : methods) {
            builder.append(methodInjector.brewJava());
        }
        builder.append("}\n");
        return builder.toString();
    }
}
