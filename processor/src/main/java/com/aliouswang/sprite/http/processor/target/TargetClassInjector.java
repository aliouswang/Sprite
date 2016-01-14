package com.aliouswang.sprite.http.processor.target;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by aliouswang on 16/1/14.
 */
public class TargetClassInjector {

    private final String classPackage;
    private final String className;
    private final String targetClass;
    private final boolean isInterface;
    private final Set<TargetMethodInjector> methods;

    public TargetClassInjector(String classPackage, String className,
                               String targetClass, boolean isInterface) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetClass = targetClass;
        this.isInterface = isInterface;
        this.methods = new LinkedHashSet<>();
    }

    public void addMethod(TargetMethodInjector e) {
        methods.add(e);
    }

    public String getFullClassName() {
        return classPackage + "." + className;
    }

    public String brewJava() throws Exception {
        StringBuilder builder = new StringBuilder("package " + this.classPackage + ";\n");
        builder.append("import com.aliouswang.sprite.http.library.HttpTask;\n");
        builder.append("import okhttp3.Request;\n" +
                "import okhttp3.RequestBody;\n" +
                "import okhttp3.Response;\n" +
                "import rx.Observable;\n");
        builder.append("import com.aliouswang.http.sprite.model.Pojo;\n");
        builder.append("import com.alibaba.fastjson.JSON;\n");
        builder.append("import java.io.IOException;\n");

        String action = this.isInterface ? "implements" : "extends";

        builder.append("public class " + this.className + " " + action + " " + this.targetClass + " {\n");
        for (TargetMethodInjector methodInjector : methods) {
            builder.append(methodInjector.brewJava());
        }
        builder.append("}\n");
        return builder.toString();
    }

}
