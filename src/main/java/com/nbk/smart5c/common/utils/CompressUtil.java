package com.nbk.smart5c.common.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressUtil {


    /**
     * 把文件放进文件夹
     *
     * @param map      是路径对应名字
     * @param filePath
     */
    public static void unloading(Map<String, String> map, String filePath) {
        byte[] bytes = new byte[1024];
        FileInputStream is = null;
        FileOutputStream os = null;
        String name = "";
        for (String s : map.keySet()) {
            File f = new File(s);
            try {
                is = new FileInputStream(f);
                name = map.get(s);
                File file = new File(filePath + File.separator + name);
                os = new FileOutputStream(file);
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                os.flush();
                os.close();
                is.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 生成zip压缩文件
     *
     * @param zipFilePath
     */
    public static void compress(List<String> url, String zipFilePath) {
        byte[] buf = new byte[1024];
        File zipFile = new File(zipFilePath);
        if (zipFile.isDirectory()) {
            if (!zipFile.exists()) {
                return;
            }
            for (String s : url) {
                File file = new File(s);//原图
                File image = new File(zipFilePath + File.separator + file.getName());//要下载到的目标地
                try {
                    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(image));
                    if (file == null || !file.exists()) {
                        continue;
                    }
                    FileInputStream fis = new FileInputStream(file);
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    int len;
                    while ((len = fis.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                    fis.close();
                    zos.flush();
                    zos.closeEntry();
                    zos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (!zipFile.exists()) {
                return;
            }
            try {
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
                for (String s : url) {
                    File sourceFile = new File(s);
                    if (sourceFile == null || !sourceFile.exists()) {
                        continue;
                    }
                    FileInputStream fis = new FileInputStream(sourceFile);
                    zos.putNextEntry(new ZipEntry(sourceFile.getName()));
                    int len;
                    while ((len = fis.read(buf)) > 0) {
                        zos.write(buf, 0, len);
                    }
                    zos.flush();
                    zos.closeEntry();
                }
                zos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载zip
     *
     * @param response
     * @param zipName  浏览器header中zip名称
     * @param zipFile  zipFile文件
     */
    public static void downloadZip(HttpServletResponse response, String zipName, File zipFile) {
        //下载文件
        try {
            response.setCharacterEncoding("utf-8");
//            response.setContentType("application/zip");
            String encode = URLEncoder.encode(zipName, "utf-8");
            //这里不能识别字符串，要编码
            response.setHeader("Content-Disposition", "attachment;FileName=" + encode);
            ServletOutputStream out = response.getOutputStream();
            ZipOutputStream zos = new ZipOutputStream(out);
            if (!zipFile.exists()) {
                return;
            }
            //若路径为目录（文件夹）
            if (zipFile.isDirectory()) {
                //取出文件夹中的文件（或子文件夹）
                File[] fileList = zipFile.listFiles();
                //若文件夹为空，则创建一个目录进入点
                if (fileList.length == 0) {
                    //文件名称后跟File.separator表示这是一个文件夹
                    zos.putNextEntry(new ZipEntry(zipName));
                    //若文件夹非空，则递归调用compressFile,对文件夹中的每个文件或每个文件夹进行压缩
                } else {
                    for (int i = 0; i < fileList.length; i++) {
                        compressFile(zos, fileList[i], fileList[i].getName());//filePath + File.separator + f.getName()
                    }
                }
                //若为文件,则先创建目录进入点,再将文件写入zip文件中
            } else {
                ZipEntry ze = new ZipEntry(zipName);
                //设置ZipEntry的最后修改时间为源文件的最后修改时间
                ze.setTime(zipFile.lastModified());
                zos.putNextEntry(ze);
                FileInputStream fis = new FileInputStream(zipFile);
                copyStream(fis, zos);
                if (Objects.nonNull(fis)) {
                    fis.close();
                }
            }
            if (Objects.nonNull(zos)) {
                zos.close();
            }
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void compressFile(ZipOutputStream zos, File sourceFile, String baseName) throws Exception {

        if (!sourceFile.exists()) {
            return;
        }
        //若路径为目录（文件夹）
        if (sourceFile.isDirectory()) {
            //取出文件夹中的文件（或子文件夹）
            File[] fileList = sourceFile.listFiles();
            //若文件夹为空，则创建一个目录进入点
            if (fileList.length == 0) {
                //文件名称后跟File.separator表示这是一个文件夹
                zos.putNextEntry(new ZipEntry(baseName));
                //若文件夹非空，则递归调用compressFile,对文件夹中的每个文件或每个文件夹进行压缩
            } else {
                for (int i = 0; i < fileList.length; i++) {
                    compressFile(zos, fileList[i], baseName + File.separator + fileList[i].getName());
                }
            }
            //若为文件,则先创建目录进入点,再将文件写入zip文件中
        } else {
            ZipEntry ze = new ZipEntry(baseName);
            //设置ZipEntry的最后修改时间为源文件的最后修改时间
            ze.setTime(sourceFile.lastModified());
            zos.putNextEntry(ze);
            FileInputStream fis = new FileInputStream(sourceFile);
            copyStream(fis, zos);
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                System.out.println("输入流关闭异常：" + e);
            }
        }
    }

    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        int bufferLength = 1024 * 100;
        synchronized (in) {
            synchronized (out) {
                int count = 0;

                byte[] buffer = new byte[bufferLength];
                while ((count = in.read(buffer, 0, bufferLength)) != -1) {
                    out.write(buffer, 0, count);
                }
                out.flush();
            }
        }
    }

    /**
     * 递归删除文件夹中的目录及文件
     *
     * @param sourceFile
     * @throws Exception
     */
    private void deleteFile(File sourceFile) throws Exception {
        //如果路径为目录
        if (sourceFile.isDirectory()) {
            //取出文件夹中的文件或子文件夹
            File[] fList = sourceFile.listFiles();
            if (fList.length == 0) {
                sourceFile.delete();
            } else {
                for (int i = 0; i < fList.length; i++) {
                    this.deleteFile(fList[i]);
                }
                sourceFile.delete();
            }
            //如果为文件则直接删除
        } else {
            sourceFile.delete();
        }
    }

    /**
     * 当前路径下若已有同名文件又不愿意覆盖，
     * 则依次追加后缀
     *
     * @param path
     * @param docName
     * @return
     * @throws Exception
     */
    private File createFile(String path, final String docName) throws Exception {
        //创建目标文件
        File destFile = new File(path, docName);
        //如果路径下存在同名文件又不愿意覆盖，
        //那么则依次给文件加后缀（2）、（3）……
        if (destFile.exists()) {
            int i = 1;
            do {
                ++i;
                //按“.”分割
                String[] doc = docName.split("\\.");
                destFile = new File(path, doc[0] + "(" + i + ")" + "." + doc[1]);

                //直到文件创建成功则跳出循环
            } while (!destFile.createNewFile());
        } else {
            destFile.createNewFile();
        }
        return destFile;
    }
}


