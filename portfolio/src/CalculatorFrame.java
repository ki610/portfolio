/**
 * View（UI）クラス。
 *
 * 本クラスは、計算機画面および各種ボタンの配置を行い、
 * 表示更新用の API を提供する。
 * ユーザー操作による入力イベントを受け取り、
 * その内容を Controller（CalculatorController）へ通知する。
 * 計算処理や入力状態の管理は行わない。
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CalculatorFrame {

    private JFrame frame;
    private JLabel displayLabel;
    private JPanel keypadPanel;

    // Controller を保持（Modelは触らない）
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
            "7", "8", "9", "÷",
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
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            if (controller == null) {
                return;
            }
            if (command.matches("[0-9]")) {
                controller.onDigit(command.charAt(0));
            } else if (command.equals(".")) {
                controller.onDot();
            } else if (command.equals("=")) {
                controller.onEquals();
            } else if (command.equals("C")) {
                controller.onClear();
            } else {
                // + - × ÷
                controller.onOperator(command);
            }
        }
    }
}
