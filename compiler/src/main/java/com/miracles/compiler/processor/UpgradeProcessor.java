package com.miracles.compiler.processor;

import com.miracles.annotations.UpgradeInstance;
import com.miracles.annotations.VersionUpgrade;
import com.miracles.compiler.AnnotationProcessor;
import com.miracles.compiler.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by lxw
 */
public class UpgradeProcessor implements IAProcessor {
    private static final String CLASS_NAME_SUFFIX = "UpgradeManager";
    private static final String PACKAGE_NAME = "com.miracles.support.upgrade";

    @Override
    public void process(RoundEnvironment roundEnv, AnnotationProcessor mProcessor) {
        Set<TypeElement> elements = ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(UpgradeInstance.class));
        if (elements.isEmpty()) {
            return;
        }
        try {
            parse(mProcessor, elements);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            mProcessor.mMessager.printMessage(Diagnostic.Kind.ERROR, "UpgradeProcessor error...");
        }
    }

    private void parse(AnnotationProcessor mProcessor, Set<TypeElement> elements) throws Exception {
        Map<String, List<TypeElementHolder>> collection = collection(mProcessor, elements);
        for (Map.Entry<String, List<TypeElementHolder>> entry : collection.entrySet()) {
            String id = entry.getKey();
            List<TypeElementHolder> elementHolders = entry.getValue();
            generateCodeForAnnotationElements(mProcessor, id, elementHolders);
        }
    }

    private static class TypeElementHolder {
        private TypeElement typeElement;
        private ClassName typeArgClzName;

        private TypeElementHolder(TypeElement typeElement, ClassName typeArgClzName) {
            this.typeElement = typeElement;
            this.typeArgClzName = typeArgClzName;
        }
    }

    private Map<String, List<TypeElementHolder>> collection(AnnotationProcessor mProcessor, Set<TypeElement> elements) {
        Map<String, List<TypeElementHolder>> idSortMapping = new HashMap<>();
        String vhName = "com.miracles.upgrade.VersionHandler";
        for (TypeElement element : elements) {
            UpgradeInstance upgradeInstance = element.getAnnotation(UpgradeInstance.class);
            List<? extends TypeMirror> interfaces = element.getInterfaces();
            boolean implementVh = false;
            ClassName typeArgClzName = null;
            for (TypeMirror typeMirror : interfaces) {
                //mProcessor.mTypes.isSameType not support wildcard.
                implementVh = vhName.equals(mProcessor.mTypes.erasure(typeMirror).toString());
                if (implementVh) {
                    DeclaredType declaredType = (DeclaredType) typeMirror;
                    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                    if (typeArguments.isEmpty()) {
                        typeArgClzName = ClassName.OBJECT;
                    } else {
                        typeArgClzName = Utils.getType(mProcessor.mTypes.erasure(typeArguments.get(0)).toString());
                    }
                    break;
                }
            }
            if (!implementVh) {
                mProcessor.mMessager.printMessage(Diagnostic.Kind.ERROR, "you must implements interface VersionHandler in " + element.getQualifiedName());
                break;
            }
            String id = upgradeInstance.id();
            List<TypeElementHolder> elementList = idSortMapping.get(id);
            if (elementList == null) {
                elementList = new ArrayList<>();
                idSortMapping.put(id, elementList);
            }
            elementList.add(new TypeElementHolder(element, typeArgClzName));
        }
        return idSortMapping;
    }

    private static class UpgradeMethodHolder {
        private TypeElement typeElement;
        private String inApplyParamName;

        private UpgradeMethodHolder(TypeElement typeElement, String inApplyParamName) {
            this.typeElement = typeElement;
            this.inApplyParamName = inApplyParamName;
        }
    }

    private static class MethodInvokeHolder implements Comparator<MethodInvokeHolder> {
        private String methodName;
        private int methodPriority;
        private String inApplyParamName;

        public MethodInvokeHolder(String methodName, int methodPriority, String inApplyParamName) {
            this.methodName = methodName;
            this.methodPriority = methodPriority;
            this.inApplyParamName = inApplyParamName;
        }

        @Override
        public int compare(MethodInvokeHolder o1, MethodInvokeHolder o2) {
            return o1.methodPriority - o2.methodPriority;
        }
    }

    private MethodSpec generateInvokeVersionHandlerMethod(AnnotationProcessor mProcessor, MethodSpec.Builder applyUpgradeMethod, List<TypeElementHolder> elementHolders) {
        String invokeVersionHandlerName = "invokeVersionHandler";
        List<UpgradeMethodHolder> upgradeMethodHolderList = new ArrayList<>();
        Map<String, List<MethodInvokeHolder>> methodInvokeHolderMap = new HashMap<>();
        for (TypeElementHolder typeElementHolder : elementHolders) {
            TypeElement typeElement = typeElementHolder.typeElement;
            String inParamName = Utils.lowerFirstChar(typeElement.getSimpleName().toString());
            List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
            UpgradeMethodHolder upgradeMethodHolder = new UpgradeMethodHolder(typeElement, inParamName);
            for (Element element : enclosedElements) {
                VersionUpgrade annotation = element.getAnnotation(VersionUpgrade.class);
                if (annotation == null) {
                    continue;
                }
                int priority = annotation.priority();
                ExecutableElement executableElement = (ExecutableElement) element;
                List<? extends VariableElement> parameters = executableElement.getParameters();
                if (!parameters.isEmpty()) {
                    mProcessor.mMessager.printMessage(Diagnostic.Kind.ERROR, "you must implements upgrade method with no arguments in" + typeElement.getQualifiedName());
                    break;
                }
                String methodName = executableElement.getSimpleName().toString();
                String identifierName = versionProtocol(annotation.fromVersion(), annotation.toVersion());
                List<MethodInvokeHolder> methodInvokeHolders = methodInvokeHolderMap.get(identifierName);
                if (methodInvokeHolders == null) {
                    methodInvokeHolders = new ArrayList<>();
                    methodInvokeHolderMap.put(identifierName, methodInvokeHolders);
                }
                methodInvokeHolders.add(new MethodInvokeHolder(methodName, priority, inParamName));
            }
            upgradeMethodHolderList.add(upgradeMethodHolder);
        }
        String versionParamName = "version";
        MethodSpec.Builder invokeVersionHandlerMethod = MethodSpec.methodBuilder(invokeVersionHandlerName)
                .returns(TypeName.VOID).addModifiers(PRIVATE);
        for (UpgradeMethodHolder holder : upgradeMethodHolderList) {
            invokeVersionHandlerMethod.addParameter(ClassName.get(holder.typeElement), holder.inApplyParamName);
        }
        invokeVersionHandlerMethod.addParameter(String.class, versionParamName);
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.addStatement("if($L==null) return", versionParamName);
        codeBuilder.beginControlFlow("switch($L)", versionParamName);
        for (Map.Entry<String, List<MethodInvokeHolder>> entry : methodInvokeHolderMap.entrySet()) {
            codeBuilder.add("case $S:\n", entry.getKey());
            for (MethodInvokeHolder methodInvokeHolder : entry.getValue()) {
                codeBuilder.addStatement("$L.$L()", methodInvokeHolder.inApplyParamName, methodInvokeHolder.methodName);
            }
            codeBuilder.addStatement("break");
        }
        codeBuilder.add("default:\n");
        codeBuilder.addStatement("throw new $T ($S+$L)", RuntimeException.class, "you must implements upgrade method which identify is fromVersion->toVersion ", versionParamName);
        codeBuilder.endControlFlow();
        invokeVersionHandlerMethod.addCode(codeBuilder.build());
        return invokeVersionHandlerMethod.build();
    }

    private String versionProtocol(int fromVersion, int toVersion) {
        return fromVersion + "->" + toVersion;
    }

    private void generateCodeForAnnotationElements(AnnotationProcessor mProcessor, String id, List<TypeElementHolder> elementHolders) throws IOException {
        if (elementHolders == null || elementHolders.isEmpty()) {
            return;
        }
        String vhName = "com.miracles.upgrade.VersionHandler";
        String umName = "com.miracles.upgrade.UpgradeManager";
        String upExName = "com.miracles.upgrade.UpgradeException";

        ClassName vhClzName = Utils.getType(vhName);
        ClassName umClzName = Utils.getType(umName);
        ClassName upExClzName = Utils.getType(upExName);

        String umOldVersionParamName = "oldVersion";
        String umNewVersionParamName = "newVersion";
        String clzName = Utils.upperFirstChar(id) + CLASS_NAME_SUFFIX;
        String mSeedFieldName = "mSeedInstance";
        ClassName universalTypeArgClzName = elementHolders.get(0).typeArgClzName;
        ParameterizedTypeName umPtName = ParameterizedTypeName.get(umClzName, universalTypeArgClzName);
        ParameterizedTypeName vhPtName = ParameterizedTypeName.get(vhClzName, universalTypeArgClzName);
        TypeSpec.Builder clzBuilder = TypeSpec.classBuilder(clzName)
                .addSuperinterface(umPtName)
                .addField(universalTypeArgClzName, mSeedFieldName, PRIVATE)
                .addModifiers(PUBLIC, FINAL);
        //setSeedInstance
        MethodSpec.Builder setSeedMethod = MethodSpec.methodBuilder("setSeedInstance")
                .returns(TypeName.VOID).addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(universalTypeArgClzName, mSeedFieldName);
        setSeedMethod.addStatement("this.$L=$L", mSeedFieldName, mSeedFieldName);
        clzBuilder.addMethod(setSeedMethod.build());
        MethodSpec.Builder applyUpgradeMethod = MethodSpec.methodBuilder("applyUpgrade")
                .returns(TypeName.VOID).addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.INT, umOldVersionParamName)
                .addParameter(TypeName.INT, umNewVersionParamName)
                .addException(upExClzName);
        //invoke method
        MethodSpec invokeHandlerMethod = generateInvokeVersionHandlerMethod(mProcessor, applyUpgradeMethod, elementHolders);
        clzBuilder.addMethod(invokeHandlerMethod);
        //applyUpgrade;
        StringBuilder invokeParamSb = new StringBuilder();
        for (TypeElementHolder typeElementHolder : elementHolders) {
            TypeElement typeElement = typeElementHolder.typeElement;
            String inParamName = Utils.lowerFirstChar(typeElement.getSimpleName().toString());
            applyUpgradeMethod.addStatement("$T $L=new $T();", typeElement, inParamName, typeElement);
            applyUpgradeMethod.addStatement("$L.setSeedInstance($L);", inParamName, mSeedFieldName);
            invokeParamSb.append(inParamName);
            invokeParamSb.append(",");
        }
        applyUpgradeMethod.addStatement("int fromVersion=$L", umOldVersionParamName);
        applyUpgradeMethod.addStatement("int toVersion=fromVersion+1");
        CodeBlock.Builder tryCode = CodeBlock.builder();
        tryCode.beginControlFlow("try");
        tryCode.beginControlFlow("while ($L >= toVersion)", umNewVersionParamName);
        //handle upgrade
        tryCode.addStatement("String compact=$L+$S+$L", "fromVersion", "->", "toVersion");
        tryCode.addStatement("$L($L$L)", invokeHandlerMethod.name, invokeParamSb.toString(), "compact");
        tryCode.addStatement("fromVersion=toVersion");
        tryCode.addStatement("++toVersion");
        tryCode.endControlFlow();
        tryCode.endControlFlow();
        tryCode.beginControlFlow("catch ($T throwable)", ClassName.get(Throwable.class));
        tryCode.addStatement("throw new $T(throwable.getMessage(), throwable, fromVersion, toVersion)", upExClzName);
        tryCode.endControlFlow();
        applyUpgradeMethod.addCode(tryCode.build());
        clzBuilder.addMethod(applyUpgradeMethod.build());
        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, clzBuilder.build()).build();// 生成源代码
        javaFile.writeTo(mProcessor.mFiler);
    }
}
