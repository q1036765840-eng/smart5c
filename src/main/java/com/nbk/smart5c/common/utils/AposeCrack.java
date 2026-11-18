package com.nbk.smart5c.common.utils;

import javassist.*;

import java.io.IOException;

public class AposeCrack {
    public static void crackAsposeCells(String JarPath) throws NotFoundException, CannotCompileException, IOException {
        // 这个是得到反编译的池
        ClassPool pool = ClassPool.getDefault();

        // 取得需要反编译的jar文件，设定路径
        pool.insertClassPath(JarPath);

        CtClass ctClass = pool.get("com.aspose.cells.License");

        CtMethod method_isLicenseSet = ctClass.getDeclaredMethod("isLicenseSet");
        method_isLicenseSet.setBody("return true;");
        CtMethod method_setLicense = ctClass.getDeclaredMethod("setLicense");
        method_setLicense.setBody("{    a = new com.aspose.cells.License();\n" +
                "    com.aspose.cells.zbkl.a();}");
        CtMethod methodL = ctClass.getDeclaredMethod("l");
        methodL.setBody("return new java.util.Date(Long.MAX_VALUE);");

        ctClass.writeFile("/home/qwe/");
    }

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
        crackAsposeCells(AposeCrack.class.getClassLoader().getResource("lib/aspose-cells-21.8.jar").getPath());
    }

}
