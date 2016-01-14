package com.aliouswang.sprite.http.processor;

import com.aliouswang.sprite.http.processor.annotation.InjectFactory;
import com.aliouswang.sprite.http.processor.annotation.POST;
import com.aliouswang.sprite.http.processor.target.TargetClassInjector;
import com.aliouswang.sprite.http.processor.target.TargetMethodInjector;

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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by aliouswang on 16/1/14.
 */
public class InjectProcessor extends AbstractProcessor{

    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.filer = processingEnv.getFiler();
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, TargetClassInjector> targetClassMap = findAllClassTarget(roundEnv);

        for (Map.Entry<TypeElement, TargetClassInjector> entry : targetClassMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            TargetClassInjector injector = entry.getValue();
            try {
                String value = injector.brewJava();

                JavaFileObject jfo = filer.createSourceFile(injector.getFullClassName(), typeElement);
                Writer writer = jfo.openWriter();
                writer.write(value);
                writer.flush();
                writer.close();
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), typeElement);
            }
        }
        return false;
    }

    private Map<TypeElement, TargetClassInjector> findAllClassTarget(RoundEnvironment roundEnv) {
        Map<TypeElement, TargetClassInjector> targetClassMap = new LinkedHashMap<>();
        Set<ExecutableElement> pathElementSet =
                (Set<ExecutableElement>) roundEnv.getElementsAnnotatedWith(POST.class);
        for (ExecutableElement element : pathElementSet) {
            //get class or interface name
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            //get method name
            TargetClassInjector targetClassInjector = createTargetClass(targetClassMap, enclosingElement);
            TargetMethodInjector targetMethodInjector = new TargetMethodInjector(element);
            targetClassInjector.addMethod(targetMethodInjector);
        }
        return targetClassMap;
    }

    private TargetClassInjector createTargetClass(Map<TypeElement, TargetClassInjector> targetClassMap,
                                                  TypeElement enclosingElement) {
        TargetClassInjector injector = targetClassMap.get(enclosingElement);
        if (injector == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = elementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
            String className = enclosingElement.getQualifiedName().toString()
                    .substring(classPackage.length() + 1).replace('.', '$') + InjectFactory.INJECTOR_SUFFIX;

            TypeMirror elementType = enclosingElement.asType();
            boolean isInterface = isInterface(elementType);
            injector = new TargetClassInjector(classPackage, className, targetType, isInterface);
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
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(POST.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
