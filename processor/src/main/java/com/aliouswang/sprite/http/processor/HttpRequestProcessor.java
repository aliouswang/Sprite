package com.aliouswang.sprite.http.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public class HttpRequestProcessor extends AbstractProcessor{

    private static final String SUFFIX = "$$HTTPREQUESTINJECTOR";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }

}
