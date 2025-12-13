/*
 * Java Swingアプリの起動クラス
 * - invokeLater() :「この中の処理をGUI専用スレッドで安全に実行、初期化してね」という命令です。
 * - CalculatorFrameを生成して表示。※この時点でウィンドウの中身（ボタンやラベルなど）が準備されます。
 * - show() は画面表示（setVisible(true)でもOK）
 */

public class CalculatorApp {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            CalculatorFrame frame = new CalculatorFrame();
            frame.show();
        });
    }
}



