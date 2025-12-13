// --- CalculatorFrame を JPanel として実装（まだ Controller 未導入） ---

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CalculatorFrame {
    private JFrame frame;
    private JLabel displayLabel;
    private JPanel keypadPanel;
    private CalculatorModel model;

    public CalculatorFrame() {
        model = new CalculatorModel();

        // JFrame設定
        frame = new JFrame("Calculator");
        frame.setSize(300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 表示ラベル
        displayLabel = new JLabel("0", SwingConstants.RIGHT);
        displayLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        displayLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

    /** 表示更新 */
    private void updateDisplay() {
        displayLabel.setText(model.getDisplayText());
    }

    /** 表示開始 */
    public void show() {
        frame.setVisible(true);
    }

    /** ボタン押下リスナー */
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();

            if (cmd.equals("C")) { 
                model = new CalculatorModel(); // Cボタンでリセット
            } else {
                model.processInput(cmd);
            }

            updateDisplay(); // ★ displayLabelを更新！
        }
    }
}
