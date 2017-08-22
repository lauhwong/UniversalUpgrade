package com.miracles.compiler.processor;


import com.miracles.compiler.AnnotationProcessor;

import javax.annotation.processing.RoundEnvironment;

/**
 * Created by lxw
 */
public interface IAProcessor {

    void process(RoundEnvironment roundEnv, AnnotationProcessor mProcessor);
}
