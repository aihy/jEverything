package com.hhwyz;

import javax.swing.table.DefaultTableModel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Search {
    static FileDAO fileDAO = new FileDAO();

    public static void search(String text, DefaultTableModel tableModel) {
        String[] s = text.split(" ");
        List<String> leftPattern = new ArrayList<>();
        List<String> midPattern = new ArrayList<>();
        List<String> rightPattern = new ArrayList<>();
        for (String s1 : s) {
            if (s1.startsWith("^")) {
                leftPattern.add(s1.substring(1));
                continue;
            }
            if (s1.endsWith("$")) {
                rightPattern.add(s1.substring(0, s1.length() - 1));
                continue;
            }
            midPattern.add(s1);
        }
        List<FileDO> select = fileDAO.select(leftPattern.toArray(new String[0]), midPattern.toArray(new String[0]), rightPattern.toArray(new String[0]));
        List<String[]> resultList = new ArrayList<>();
        for (FileDO fileDO : select) {
            Path p = Paths.get(fileDO.getPath());
            String fileName = p.getFileName().toString();
            String type = "";
            if (fileName.contains(".")) {
                String[] split = p.getFileName().toString().split("\\.");
                type = split[split.length - 1];
            }
            String size;
            if (fileDO.getSize() > 1000 * 1000 * 1000) {
                size = String.format("%.1f GB", ((double) fileDO.getSize()) / 1000 / 1000 / 1000);
            } else if (fileDO.getSize() > 1000 * 1000) {
                size = String.format("%.1f MB", ((double) fileDO.getSize()) / 1000 / 1000);
            } else if (fileDO.getSize() > 1000) {
                size = String.format("%.1f KB", ((double) fileDO.getSize()) / 1000);
            } else {
                size = String.format("%d B", fileDO.getSize());
            }
            String path = fileDO.getPath();
            resultList.add(new String[]{fileName, type, size, path});
        }

        tableModel.setRowCount(0);
        for (String[] strings : resultList) {
            tableModel.addRow(strings);
        }
    }
}
