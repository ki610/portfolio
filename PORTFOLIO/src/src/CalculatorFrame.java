/**
 * UIクラス
 * -画面を表示
 * -入力イベントの受取
 * -入力の通知
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CalculatorFrame {

    private JFrame frame;
    private JLabel displayLabel;
    private JPanel keypadPanel;

    // ★ Controller を保持（Modelは触らない）
    private CalculatorController controller;

    public CalculatorFrame() {

        // JFrame設定
        frame = new JFrame("Calculator");
        frame.setSize(300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 表示ラベル
        displayLabel = new JLabel("0", SwingConstants.RIGHT);
        displayLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        displayLabel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
        frame.add(displayLabel, BorderLayout.NORTH);

        // ボタンパネル
        keypadPanel = new JPanel(new GridLayout(5, 4, 5, 5));

        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "×",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C"
        };

        // 各ボタンにリスナー登録
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.PLAIN, 18));
            button.addActionListener(new ButtonClickListener());
            keypadPanel.add(button);
        }

        frame.add(keypadPanel, BorderLayout.CENTER);
    }

    /* ------------------------
     * Controller 接続
     * ------------------------ */
    public void bindController(CalculatorController controller) {
        this.controller = controller;
    }

    /* ------------------------
     * 表示更新（Controller から呼ばれる）
     * ------------------------ */
    public void setDisplay(String text) {
        displayLabel.setText(text);
    }

    /* ------------------------
     * 表示開始
     * ------------------------ */
    public void show() {
        frame.setVisible(true);
    }

    /* ------------------------
     * ボタン押下リスナー
     * ------------------------ */
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            String cmd = e.getActionCommand();

            if (controller == null) return;

            if (cmd.matches("[0-9]")) {
                controller.onDigit(cmd.charAt(0));
            } else if (cmd.equals(".")) {
                controller.onDot();
            } else if (cmd.equals("=")) {
                controller.onEquals();
            } else if (cmd.equals("C")) {
                controller.onClear();
            } else {
                // + - × /
                controller.onOperator(cmd);
            }
        }
    }
}
