package com.aliouswang.sprite.http.processor.target;

import com.aliouswang.sprite.http.processor.annotation.GET;
import com.aliouswang.sprite.http.processor.annotation.POST;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Created by aliouswang on 16/1/14.
 */
public class TargetMethodInjector {

    public static final String _POST = "post";
    public static final String _GET = "get";

    private ExecutableElement executableElement;

    private final TypeMirror returnType;
    private final List<? extends VariableElement> arguments;
    private final String methodName;

    private String httpMethod;
    private String url;

    public TargetMethodInjector(ExecutableElement executableElement) {
        this.executableElement = executableElement;
        this.methodName = executableElement.getSimpleName().toString();
        this.returnType = executableElement.getReturnType();
        this.arguments = executableElement.getParameters();

        if (executableElement.getAnnotation(POST.class) != null) {
            httpMethod = _POST;
            url = executableElement.getAnnotation(POST.class).value();
        }else if (executableElement.getAnnotation(GET.class) != null) {
            httpMethod = _GET;
            url = executableElement.getAnnotation(GET.class).value();
        }
    }

    public String brewJava() throws Exception {
        Map<String, String> parameterNameMap = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder(" public ");

        String returnTypeName = this.returnType.toString();

        sb.append(returnTypeName + " ");
        sb.append(methodName + "(");
        boolean isFirst = true;

        for (VariableElement variableElement : arguments) {
            DeclaredType type = (DeclaredType)variableElement.asType();
            String typeName = type.asElement().toString();
            String variableName = variableElement.getSimpleName().toString();

            String parameterName = variableName;

            parameterNameMap.put(parameterName, variableName);
            if (!isFirst) {
                sb.append(", ");
            }

            sb.append(typeName);
            sb.append(" " + variableName);

            if (isFirst) {
                isFirst = false;
            }
        }

        sb.append(") {\n");
        sb.append(buildFunctionBody(parameterNameMap));
        sb.append("}\n");

        return sb.toString();
    }

    private String buildFunctionBody(Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<String, String> parameter: parameters.entrySet()) {
//            String name = parameter.getKey();
//        }
        sb.append("Request.Builder builder = new Request.Builder()\n" +
                "                .url(\"" + url + "\");\n");
        sb.append("Request request = builder.build();\n");
        sb.append("String result = \"\";\n" +
                "        try {\n" +
                "            Response response = HttpTask.getHttpClientInstance().newCall(request).execute();\n" +
                "            if (response.isSuccessful()) {\n" +
                "                result = response.body().string();\n" +
                "            }\n" +
                "        } catch (IOException e) {\n" +
                "            e.printStackTrace();\n" +
                "        } finally {\n" +
                "               Pojo pojo = JSON.parseObject(result, Pojo.class);"   +
                "            return Observable.just(pojo);\n" +
                "        }");
        return sb.toString();
    }
}
