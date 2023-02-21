package com.hhwyz;


import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static void main(String[] args) throws Exception {
//        FileUtils f = new FileUtils();
//        f.reBuildIndex(Paths.get("/"));
    }

    public List<File> getAllFileList(Path root) throws IOException {
        List<File> fileList = new ArrayList<>();
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileList.add(file.toFile());
                if (fileList.size() % 10000 == 0) {
                    System.out.printf("已加载%d个文件\n", fileList.size());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                System.out.printf("file %s fail\n", file.toAbsolutePath());
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }

    public void reBuildIndex(Path root, JLabel label) {
        label.setText("重建索引中...\t\t总文件数：0");
        long a = System.nanoTime();
        FileDAO fileDAO = new FileDAO();
        fileDAO.deleteAll();
        long b = System.nanoTime();
        List<File> fileList = new ArrayList<>();
        final int[] ct = {0};
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    fileList.add(file.toFile());
                    ct[0]++;
                    label.setText("重建索引中...\t\t总文件数：" + ct[0]);
                    if (fileList.size() % 10000 == 0) {
                        System.out.printf("已加载%d个文件\n", ct[0]);
                        List<FileDO> allFileDOList = new ArrayList<>();
                        for (File f : fileList) {
                            FileDO fileDO = new FileDO();
                            fileDO.setPath(f.getAbsolutePath());
                            fileDO.setSize(f.length());
                            allFileDOList.add(fileDO);
                        }
                        fileDAO.batchInsertFile(allFileDOList);
                        fileList.clear();
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    //                System.out.printf("file %s fail\n", file.toAbsolutePath());
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.printf("总文件数量：%d\n", ct[0]);
        List<FileDO> allFileDOList = new ArrayList<>();
        for (File f : fileList) {
            FileDO fileDO = new FileDO();
            fileDO.setPath(f.getAbsolutePath());
            fileDO.setSize(f.length());
            allFileDOList.add(fileDO);
        }
        fileDAO.batchInsertFile(allFileDOList);
        long c = System.nanoTime();
        System.out.println("重建索引完成");
        System.out.printf("清空索引耗时：%s\n重建索引耗时：%s\n", Duration.ofNanos(b - a), Duration.ofNanos(c - b));
    }
}
