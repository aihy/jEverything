package com.hhwyz;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.*;

public class Main extends JFrame {
    // 文件名称、文件类型、文件大小的标题
    private static final String[] columnNames = {"文件名", "类型", "大小", "路径"};

    FileDAO fileDAO;
    JTable table;
    DefaultTableModel tableModel;
    ExecutorService executor = new ThreadPoolExecutor(1,
            1,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());
//    ExecutorService executor = Executors.newSingleThreadExecutor();

    public Main() {
        if (Files.notExists(Paths.get(System.getProperty("user.home"), ".jEverything"))) {
            try {
                Files.createDirectory(Paths.get(System.getProperty("user.home"), ".jEverything"));
            } catch (IOException e) {
                System.out.println("error creating .jEverything, %s");
                e.printStackTrace();
                System.exit(-1);
            }
        }
        fileDAO = new FileDAO();
        if (fileDAO.check() == 0) {
            fileDAO.init();
        }
        // 初始化界面
        initUI();
    }

    // main函数
    public static void main(String[] args) {
        new Main();
    }

    // 初始化界面
    private void initUI() {
        // 设置窗口标题
        setTitle("jEverything");

        // 设置大小和位置
        setSize(800, 600);
        setLocationRelativeTo(null);

        // 设置默认关闭模式
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置窗口布局
        setLayout(new BorderLayout());

        // 创建输入框
        JTextField textField = new JTextField();

        // 设置输入框位置
        add(textField, BorderLayout.NORTH);

        // 创建表格模型
        tableModel = new DefaultTableModel(columnNames, 0);

        // 创建表格
        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row == -1) {
                        return;
                    }
                    // 如果右键的这个是选中的，那么获取所有选中的列表
                    if (Arrays.stream(table.getSelectedRows()).anyMatch(x -> x == row)) {
                        String[] selectedPaths = new String[table.getSelectedRows().length];
                        for (int i = 0; i < table.getSelectedRows().length; i++) {
                            selectedPaths[i] = (String) table.getValueAt(table.getSelectedRows()[i], 3);
                        }
                        new TablePopupMenu(selectedPaths).show(table, e.getX(), e.getY());
                    }
                    // 否则选中当前指针指向的一个
                    else {
                        table.setRowSelectionInterval(row, row);
                        String valueAt = (String) table.getValueAt(table.getSelectedRow(), 3);
                        new TablePopupMenu(new String[]{valueAt}).show(table, e.getX(), e.getY());
                    }


                }
            }
        });

        table.registerKeyboardAction(a -> {
            if (Files.exists(Paths.get("/usr/bin/qlmanage"))) {
                try {
                    String[] selectedPaths = new String[table.getSelectedRows().length];
                    for (int i = 0; i < table.getSelectedRows().length; i++) {
                        selectedPaths[i] = (String) table.getValueAt(table.getSelectedRows()[i], 3);
                    }
                    for (String path : selectedPaths) {
                        new ProcessBuilder("/usr/bin/qlmanage",
                                "-p",
                                path).start();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }, KeyStroke.getKeyStroke(' '), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        table.setDragEnabled(true);
        table.setTransferHandler(new ClipboardUtil.FileTransferHandler());

        // 设置表格位置
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 监听输入框的变化
        textField.getDocument().addDocumentListener(new MyDocumentListener(textField));

        int count = fileDAO.count();
        String updateTime = fileDAO.selectTime();

        JPanel southPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(southPanel, BoxLayout.X_AXIS);
        southPanel.setLayout(boxLayout);
        JLabel label = new JLabel(String.format("索引更新时间：%s\t\t总文件数：%d", updateTime, count));
        JButton button = new JButton("重建索引");
        southPanel.add(label);
        southPanel.add(Box.createGlue());
        southPanel.add(button);

        button.addActionListener(l -> {
            Thread thread = new Thread(() -> {
                button.setEnabled(false);
                FileUtils fileUtils = new FileUtils();
                fileUtils.reBuildIndex(Paths.get(System.getProperty("user.home")), label);
                int newCount = fileDAO.count();
                String newUpdateTime = fileDAO.selectTime();
                label.setText(String.format("索引更新时间：%s\t\t总文件数：%d", newUpdateTime, newCount));
                button.setEnabled(true);
                Search.search(textField.getText(), tableModel);
            });
            thread.start();
        });

        add(southPanel, BorderLayout.SOUTH);

        if (count == 0) {
            button.doClick();
        }

        Search.search("", tableModel);

        setVisible(true);
    }

    class MyDocumentListener implements DocumentListener {
        JTextField textField;

        public MyDocumentListener(JTextField textField) {
            this.textField = textField;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            executor.submit(() -> search(e));
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            executor.submit(() -> search(e));
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            executor.submit(() -> search(e));
        }

        private void search(DocumentEvent e) {
            String text;
            try {
                text = e.getDocument().getText(0, e.getDocument().getLength());
                Search.search(text, tableModel);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}