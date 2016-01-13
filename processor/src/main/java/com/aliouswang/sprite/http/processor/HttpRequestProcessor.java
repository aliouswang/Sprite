package com.aliouswang.sprite.http.processor;

import com.aliouswang.sprite.http.processor.annotation.POST;
import com.aliouswang.sprite.http.processor.inject.APIClassInjector;
import com.aliouswang.sprite.http.processor.inject.ApiMethodInjector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class HttpRequestProcessor extends AbstractProcessor{

    private static final String SUFFIX = "$$HTTPREQUESTINJECTOR";

    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, APIClassInjector> targetClassMap = findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, APIClassInjector> entry : targetClassMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            APIClassInjector injector = entry.getValue();
            try {
                String value = injector.brewJava();

                JavaFileObject jfo = filer.createSourceFile(injector.getFqcn(), typeElement);
                Writer writer = jfo.openWriter();
                writer.write(value);
                writer.flush();
                writer.close();
            } catch (Exception e) {
                processingEnv.getMessager().
                        printMessage(Diagnostic.Kind.ERROR, e.getMessage(), typeElement);
            }
        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(POST.class.getCanonicalName());
        return supportTypes;
    }

    private Map<TypeElement, APIClassInjector> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, APIClassInjector> targetClassMap = new LinkedHashMap<>();


        for (Element element : env.getElementsAnnotatedWith(POST.class)) {
            ExecutableElement executableElement = (ExecutableElement) element;


            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();


            APIClassInjector injector = getOrCreateTargetClass(targetClassMap, enclosingElement);
            ApiMethodInjector methodInjector = new ApiMethodInjector(executableElement);
            injector.addMethod(methodInjector);
        }
        return targetClassMap;
    }


    private APIClassInjector getOrCreateTargetClass(Map<TypeElement, APIClassInjector> targetClassMap, TypeElement enclosingElement) {
        APIClassInjector injector = targetClassMap.get(enclosingElement);
        if (injector == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + SUFFIX;

            TypeMirror elementType = enclosingElement.asType();
            boolean isInterface = isInterface(elementType);

            injector = new APIClassInjector(classPackage, className, targetType, isInterface);
            targetClassMap.put(enclosingElement, injector);
        }
        return injector;
    }

    private boolean isInterface(TypeMirror typeMirror) {
        if (!(typeMirror instanceof DeclaredType)) {
            return false;
        }
        return ((DeclaredType) typeMirror).asElement().getKind() == ElementKind.INTERFACE;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private void writeLog(String str) {
        try {
            FileWriter fw = new FileWriter(new File("/Users/geminiwen/process.txt"), true);
            fw.write(str + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
