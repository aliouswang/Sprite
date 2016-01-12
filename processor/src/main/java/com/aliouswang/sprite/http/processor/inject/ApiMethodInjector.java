package com.aliouswang.sprite.http.processor.inject;

import com.aliouswang.sprite.http.processor.annotation.Name;
import com.aliouswang.sprite.http.processor.annotation.POST;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by aliouswang on 16/1/12.
 */
public class ApiMethodInjector {

    private static final String RESPONSE_LISETENER = "org.gemini.httpengine.library.OnResponseListener";

    private ExecutableElement executableElement;

    private final TypeMirror returnType;
    private final List<? extends VariableElement> arguments;
    private final String methodName;

    private String taskId;
    private String httpMethod;
    private String url;

    public ApiMethodInjector(ExecutableElement executableElement) {
        String methodName = executableElement.getSimpleName().toString();
        TypeMirror returnType = executableElement.getReturnType();
        List<? extends VariableElement> arguments = executableElement.getParameters();

        this.returnType = returnType;
        this.arguments = arguments;
        this.methodName = methodName;

        this.url = executableElement.getAnnotation(POST.class).value();

        if (executableElement.getAnnotation(POST.class) != null) {
            this.httpMethod = "POST";
        }
    }

    public String brewJava() throws Exception {
        StringBuilder sb = new StringBuilder(" public ");

        TypeKind returnTypeKind = returnType.getKind();

        String responseListenerName = null;
        Map<String, String> parameterNameMap = new LinkedHashMap<>();

        switch (returnTypeKind) {
            case VOID:
                sb.append("void ");
                break;
            default: throw new Exception("other types are not supported");
        }

        sb.append(methodName + "(");
        boolean isFirst = true;

        for (VariableElement variableElement : arguments) {
            DeclaredType type = (DeclaredType) variableElement.asType();
            String typeName = type.asElement().toString();
            String variableName = variableElement.getSimpleName().toString();

            if (typeName.equals(RESPONSE_LISETENER)) {
                responseListenerName = variableName;
            }else {
                Name nameAnnotation = variableElement.getAnnotation(Name.class);
                String parameterName = variableName;
                if (nameAnnotation != null) {
                    parameterName = nameAnnotation.value();
                }
                parameterNameMap.put(parameterName, variableName);
            }

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
        sb.append(buildMethodBody(parameterNameMap, responseListenerName));
        sb.append("}\n");
        return sb.toString();
    }

    private String buildMethodBody(Map<String, String> parameters, String responseListerName) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> parameter: parameters.entrySet()) {
            String name = parameter.getKey();

        }

        sb.append("OkHttpClient client = new OkHttpClient();\n" +
                "\n" +
                "        Request request = new Request.Builder()\n" +
                "                .url(\"http://test.api.51jiabo.com:1080/hxjb/decoration/case/v1.0/list.do\")\n" +
                "                .build();\n" +
                "\n" +
                "        client.newCall(request).enqueue(new Callback() {\n" +
                "            @Override\n" +
                "            public void onFailure(Request request, IOException e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "\n" +
                "            @Override\n" +
                "            public void onResponse(Response response) throws IOException {\n" +
                "                if (!response.isSuccessful()) {\n" +
                "                    throw new IOException(\"Unexpected code \" + response);\n" +
                "                }\n" +
                "\n" +
                "                Log.e(\"sprite\", response.body().string());\n" +
                "            }\n" +
                "        });");

        return sb.toString();
    }


}
