package com.miracles.compiler.utils;

import com.squareup.javapoet.ClassName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by lxw
 */
public class Utils {
    public static final String ANNOTATION = "@..Presenter";

    public static boolean isPublic(TypeElement element) {
        return element.getModifiers().contains(PUBLIC);
    }

    public static boolean isAbstract(TypeElement element) {
        return element.getModifiers().contains(ABSTRACT);
    }

    public static boolean isValidClass(Messager messager, TypeElement element) {
        if (element.getKind() != ElementKind.CLASS) {
            return false;
        }

        if (!isPublic(element)) {
            String message = String.format("Classes annotated with %s must be public.", ANNOTATION);
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }

        if (isAbstract(element)) {
            String message = String.format("Classes annotated with %s must not be abstract.", ANNOTATION);
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }

        return true;
    }

    public static String getPackageName(Elements elements, TypeElement typeElement) {
        PackageElement pkg = elements.getPackageOf(typeElement);
        if (pkg.isUnnamed()) {
            return null;
        }
        return pkg.getQualifiedName().toString();
    }


    public static String getClassName(TypeElement typeElement) {
        return ClassName.get(typeElement).simpleName();
    }

    public static String getPackageName(String fullName) {
        return fullName.substring(0, fullName.lastIndexOf("."));
    }

    public static String getClzName(String fullName) {
        return fullName.substring(fullName.lastIndexOf(".") + 1, fullName.length());
    }

    public static ClassName getType(String className) {
        return ClassName.get(getPackageName(className), getClzName(className));
    }

    public static String upperFirstChar(String name) {
        if (null == name || "".equals(name)) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return String.valueOf(chars);
    }

    public static String lowerFirstChar(String name) {
        if (null == name || "".equals(name)) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return String.valueOf(chars);
    }

}
