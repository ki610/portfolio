/*
 * Java Swingアプリの起動クラス
 * - インスタンス作成
 * - クラス同士を「配線」している
 */

public class CalculatorApp {

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(() -> {

            // ------------------------
            // Model
            // ------------------------
            CalculatorModel model = new CalculatorModel();

            // ------------------------
            // View
            // ------------------------
            CalculatorFrame frame = new CalculatorFrame();

            // ------------------------
            // Controller
            // ------------------------
            CalculatorController controller =
                    new CalculatorController(model, frame);

            // View と Controller を接続
            frame.bindController(controller);

            // 画面表示
            frame.show();
        });
    }
}




