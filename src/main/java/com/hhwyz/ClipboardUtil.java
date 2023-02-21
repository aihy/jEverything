package com.hhwyz;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClipboardUtil {

    /**
     * 从剪贴板中获取文本（粘贴）
     */
    public static String getClipboardString() {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // 获取剪贴板中的内容
        Transferable trans = clipboard.getContents(null);

        if (trans != null) {
            // 判断剪贴板中的内容是否支持文本
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    // 获取剪贴板中的文本内容
                    String text = (String) trans.getTransferData(DataFlavor.stringFlavor);
                    return text;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 把文本设置到剪贴板（复制）
     */
    public static void setClipboardString(String text) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装文本内容
        Transferable trans = new StringSelection(text);
        // 把文本内容设置到系统剪贴板
        clipboard.setContents(trans, null);
    }

    public static class FileTransferHandler extends TransferHandler {

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTable table = (JTable) c;
            String[] selectedPaths = new String[table.getSelectedRows().length];
            for (int i = 0; i < table.getSelectedRows().length; i++) {
                selectedPaths[i] = (String) table.getValueAt(table.getSelectedRows()[i], 3);
            }
            List<File> listOfFiles = new ArrayList<>();
            for (String path : selectedPaths) {
                File file = new File(path);
                listOfFiles.add(file);
            }
            return new FileTransferable(listOfFiles);
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }
    }

    private static class FileTransferable implements Transferable {

        private List<File> files;

        public FileTransferable(List<File> files) {
            this.files = files;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.javaFileListFlavor);
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return files;
        }
    }
}
