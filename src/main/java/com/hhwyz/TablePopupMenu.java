package com.hhwyz;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TablePopupMenu extends JPopupMenu {
    public TablePopupMenu(String[] selectedPaths) {

        JMenuItem open = new JMenuItem("打开");
        JMenuItem explore = new JMenuItem("打开所在文件夹");
        JMenuItem copyPath = new JMenuItem("拷贝路径");
        open.addActionListener(e -> {
            try {
                for (String path : selectedPaths) {
                    Desktop.getDesktop().open(new File(path));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        explore.addActionListener(e -> {
            if (Files.exists(Paths.get("/usr/bin/open"))) {
                try {
                    for (String path : selectedPaths) {
                        new ProcessBuilder("/usr/bin/open",
                                "-R",
                                path).start();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    for (String path : selectedPaths) {
                        Desktop.getDesktop().open(new File(path).getParentFile());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        copyPath.addActionListener(e -> {
            ClipboardUtil.setClipboardString(String.join("\n", selectedPaths));
        });
        this.add(open);
        this.add(explore);
        this.add(copyPath);
    }

}
