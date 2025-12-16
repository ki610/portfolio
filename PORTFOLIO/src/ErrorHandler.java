public class ErrorHandler {

    private ErrorHandler() {} // インスタンス化禁止

    /**
     * 計算中の例外を処理する共通メソッド
     * - 現状はログ出力とする
     * - Model 側は呼び出し後 state = ERROR に遷移する
     */
    public static void handle(Exception e) {
        // 本来はログファイルに書くが、デバッグでは標準出力
        System.err.println("[Calculator Error] " + e.getMessage());
    }
}

