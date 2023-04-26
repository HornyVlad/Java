import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Math.cos;

public class Form extends JFrame {
    private JPanel MainPanel;
    private JTextField textField1;
    private JButton enterButton;
    private JTextField textField2;
    private JTextField textField3;
    public JTable table1;
    private JButton removeButton;
    private JButton calculateButton;
    private JButton FillButton;
    private JButton ClearButton;
    private JButton WriteButton;
    private JButton WriteButtonBin;
    private JButton OpenButton;
    private JButton OpenButtonBin;
    private JFileChooser fileChooser;

    public ArrayList<RecIntegral> Stroka = new ArrayList<>();


    public Form() {
        table1.setDefaultEditor(Object.class, null);
        DefaultTableModel MyModel = (DefaultTableModel) table1.getModel();
        fileChooser = new JFileChooser("D:\\Work2\\Work\\Java_Proj\\FirstLab");

        MyModel.addColumn("Верх");
        MyModel.addColumn("Низ");
        MyModel.addColumn("Шаг");
        MyModel.addColumn("Результат");

        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double a = Double.parseDouble(textField1.getText());
                double b = Double.parseDouble(textField2.getText());
                double c = Double.parseDouble(textField3.getText());

                try {
                    if ((a < 0.000001) || (a > 1000000) || (b < 0.000001) || (b > 1000000) || (c < 0.000001) || (c > 1000000)) {
                        throw new MyException("Присутствует слишком малое либо слишком большое число.\n Укажите пожалуйста число в диапазоне от 0.000001 до 1000000");
                    }
                    if (a < b) throw new MyException("Нижний не может быть больше верхнего");
                    MyModel.addRow(new Object[]{Double.parseDouble(textField1.getText()),
                            Double.parseDouble(textField2.getText()),
                            Double.parseDouble(textField3.getText()), 0.0});
                } catch (MyException ex) {
                    JOptionPane.showMessageDialog(MainPanel, ex.getMessage());
                }
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table1.getSelectedRowCount() == 1) {
                    MyModel.removeRow(table1.getSelectedRow());
                } else
                    JOptionPane.showMessageDialog(calculateButton, "Пожалуйста выберите 1 любую строку");
            }
        });
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DatagramSocket clientSocket = new DatagramSocket();
                    InetAddress IPAddress = InetAddress.getByName("localhost");

                    byte[] sendingDataBuffer = new byte[1024];
                    String message;
                    DatagramPacket sendingPacket;

                    byte[] receivingDataBuffer = new byte[1024];
                    DatagramPacket receivingPacket;

                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 3; j++) {
                            message = new String(MyModel.getValueAt(i, j).toString());
                            sendingDataBuffer = message.getBytes();
                            sendingPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddress, 8080);
                            clientSocket.send(sendingPacket);
                        }
                    }
                    for (int i = 0; i < 9; i++) {
                        receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
                        clientSocket.receive(receivingPacket);
                        String receivedData = new String(receivingPacket.getData());
                        table1.setValueAt(Double.parseDouble(receivedData), i, 3);
                    }

                    clientSocket.close();
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                } catch (SocketException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
//                Thread[] thread = new Thread[9];
//                for (int i = 0; i < 9; i++) {
//                    thread[i] = new Thread(new MyThread(Double.parseDouble(MyModel.getValueAt(i, 0).toString()),
//                            Double.parseDouble(MyModel.getValueAt(i, 1).toString()),
//                            Double.parseDouble(MyModel.getValueAt(i, 2).toString()),
//                            i, table1));
//                    thread[i].start();
//                }
            }
        });

        ClearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = table1.getRowCount();
                for (int i = 0; i < count; i++) {
                    Stroka.add(new RecIntegral(Double.parseDouble(table1.getValueAt(0, 0).toString()),
                            Double.parseDouble(table1.getValueAt(0, 1).toString()),
                            Double.parseDouble(table1.getValueAt(0, 2).toString()),
                            Double.parseDouble(table1.getValueAt(0, 3).toString())));
                    MyModel.removeRow(0);
                }
            }
        });
        FillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < Stroka.size(); i++) {
                    double high = Stroka.get(i).high;
                    double low = Stroka.get(i).low;
                    double height = Stroka.get(i).height;
                    double result = Stroka.get(i).result;
                    //MyModel.removeRow(0);
                    MyModel.addRow(new Object[]{high, low, height, result});
                }
                Stroka.clear();
            }
        });

        WriteButtonBin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ObjectOutputStream out = null;
                ArrayList<SaveData> save = new ArrayList<SaveData>();
                for (int i = 0; i < table1.getRowCount(); i++) {
                    save.add(new SaveData(Double.parseDouble(table1.getValueAt(i, 0).toString()),
                            Double.parseDouble(table1.getValueAt(i, 1).toString()),
                            Double.parseDouble(table1.getValueAt(i, 2).toString()),
                            Double.parseDouble(table1.getValueAt(i, 3).toString())));
                }
                try {
                    out = new ObjectOutputStream(new BufferedOutputStream(
                            new FileOutputStream("A.ser")));
                    out.writeObject(save);
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        OpenButtonBin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("Выбор файла");
                //fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(Form.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    while (MyModel.getRowCount() > 0) {
                        MyModel.removeRow(0);
                    }
                    ObjectInputStream in = null;
                    ArrayList<SaveData> open = new ArrayList<SaveData>();
                    try {
                        in = new ObjectInputStream(new BufferedInputStream(
                                new FileInputStream(fileChooser.getSelectedFile())));
                        open = (ArrayList<SaveData>) in.readObject();
                        for (int i = 0; i < open.size(); i++) {
                            double first = open.get(i).getFirstColumn();
                            double second = open.get(i).getSecondColumn();
                            double third = open.get(i).getThirdColumn();
                            double fourth = open.get(i).getFourthColumn();
                            MyModel.addRow(new Object[]{first, second, third, fourth});
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        WriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveData save = new SaveData(Double.parseDouble(table1.getValueAt(0, 0).toString()),
                        Double.parseDouble(table1.getValueAt(0, 1).toString()),
                        Double.parseDouble(table1.getValueAt(0, 2).toString()),
                        Double.parseDouble(table1.getValueAt(0, 3).toString()));
                try {
                    FileWriter writer = new FileWriter("A.txt", false);
                    for (int i = 0; i < table1.getRowCount(); i++) {
                        writer.write(table1.getValueAt(i, 0).toString());
                        writer.write(' ');
                        writer.write(table1.getValueAt(i, 1).toString());
                        writer.write(' ');
                        writer.write(table1.getValueAt(i, 2).toString());
                        writer.write(' ');
                        writer.write(table1.getValueAt(i, 3).toString());
                        writer.write('\n');
                        writer.flush();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        OpenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("Выбор файла");
                //fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(Form.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    while (MyModel.getRowCount() > 0) {
                        MyModel.removeRow(0);
                    }
                    try {
                        FileReader open = new FileReader(fileChooser.getSelectedFile());
                        char[] buf = new char[256];
                        int a;
                        String readData = null;
                        while ((a = open.read(buf)) != -1) {
                            readData = String.valueOf(buf, 0, a);
                        }
                        open.close();

                        int numberColumn = 0;
                        StringBuilder first = new StringBuilder();
                        StringBuilder second = new StringBuilder();
                        StringBuilder third = new StringBuilder();
                        StringBuilder fourth = new StringBuilder();
                        for (int i = 0; i < readData.length(); i++) {
                            if (readData.charAt(i) != ' ') {
                                if (readData.charAt(i) != '\n') {

                                    switch (numberColumn) {
                                        case 0:
                                            first.append(readData.charAt(i));
                                            break;
                                        case 1:
                                            second.append(readData.charAt(i));
                                            break;
                                        case 2:
                                            third.append(readData.charAt(i));
                                            break;
                                        case 3:
                                            fourth.append(readData.charAt(i));
                                            break;
                                    }
                                } else {
                                    MyModel.addRow(new Object[]{Double.parseDouble(first.toString()),
                                            Double.parseDouble(second.toString()),
                                            Double.parseDouble(third.toString()),
                                            Double.parseDouble(fourth.toString())});
                                    first.delete(0, 255);
                                    second.delete(0, 255);
                                    third.delete(0, 255);
                                    fourth.delete(0, 255);
                                    numberColumn = 0;
                                }
                            } else numberColumn++;
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        Form f = new Form();
        f.setContentPane(f.MainPanel);
        f.setTitle("Shedevr");
        f.setSize(600, 300);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        MainPanel = new JPanel();
        MainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(25, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Верх");
        MainPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Шаг");
        MainPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textField1 = new JTextField();
        MainPanel.add(textField1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Нижн");
        MainPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textField2 = new JTextField();
        MainPanel.add(textField2, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textField3 = new JTextField();
        MainPanel.add(textField3, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        enterButton = new JButton();
        enterButton.setText("Добавить");
        MainPanel.add(enterButton, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeButton = new JButton();
        removeButton.setText("Удалить");
        MainPanel.add(removeButton, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        calculateButton = new JButton();
        calculateButton.setText("Рассчитать");
        MainPanel.add(calculateButton, new com.intellij.uiDesigner.core.GridConstraints(2, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ClearButton = new JButton();
        ClearButton.setText("Очистить");
        MainPanel.add(ClearButton, new com.intellij.uiDesigner.core.GridConstraints(23, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        FillButton = new JButton();
        FillButton.setText("Заполнить");
        MainPanel.add(FillButton, new com.intellij.uiDesigner.core.GridConstraints(23, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        MainPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(24, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        MainPanel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 19, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, -1, scrollPane1.getFont()), new Color(-4473925)));
        table1 = new JTable();
        table1.setDropMode(DropMode.USE_SELECTION);
        table1.setEnabled(true);
        table1.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
        table1.putClientProperty("Table.isFileList", Boolean.FALSE);
        table1.putClientProperty("html.disable", Boolean.FALSE);
        table1.putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);
        scrollPane1.setViewportView(table1);
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        MainPanel.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        WriteButton = new JButton();
        WriteButton.setText("Записать");
        MainPanel.add(WriteButton, new com.intellij.uiDesigner.core.GridConstraints(4, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        WriteButtonBin = new JButton();
        WriteButtonBin.setText("Записать (BIN)");
        MainPanel.add(WriteButtonBin, new com.intellij.uiDesigner.core.GridConstraints(5, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        OpenButton = new JButton();
        OpenButton.setText("Открыть");
        MainPanel.add(OpenButton, new com.intellij.uiDesigner.core.GridConstraints(6, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        OpenButtonBin = new JButton();
        OpenButtonBin.setText("Открыть (BIN)");
        MainPanel.add(OpenButtonBin, new com.intellij.uiDesigner.core.GridConstraints(7, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
