/**
 * アプリケーションのエントリーポイントクラス。
 *
 * 本クラスは、Java Swing 電卓アプリの起動処理を担当し、
 * MVC を構成する各クラスのインスタンス生成および
 * 相互の関連付け（配線）を行う。
 * 本クラス自身は、計算処理・入力制御・表示制御などの
 * アプリケーションロジックは一切持たない。
 */

public class CalculatorApp {
    
    public static void main(String[] args) {

        //Swing の画面処理をイベントディスパッチスレッド（EDT） で実行させる
        javax.swing.SwingUtilities.invokeLater(() -> {

            // ------------------------
            // 状態・計算ロジック
            // ------------------------
            CalculatorModel model = new CalculatorModel();

            // ------------------------
            //画面表示
            // ------------------------
            CalculatorFrame frame = new CalculatorFrame();

            // ------------------------
            // 入力制御
            // ------------------------
            CalculatorController controller =new CalculatorController(model, frame);
            // View と Controller を関連付ける
            frame.bindController(controller);
            // 画面表示
            frame.show();
        });
    }
}




