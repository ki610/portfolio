/**
 * エラーハンドリング用ユーティリティクラス。
 *
 * 本クラスは、計算処理中に発生した例外を集約して扱う責務を持つ。
 * 主に Model（CalculatorModel）から呼び出され、
 * 例外内容のログ出力などの共通処理を行う。
 *
 * 【設計方針】
 * - 本クラスではアプリケーション状態の変更は行わない
 *   （ERROR 状態への遷移は Model 側の責務とする）
 * - 画面表示の更新やユーザー通知は行わない
 * - 現状は標準エラー出力へのログ出力のみを行う
 * - 将来的にログファイル出力等へ拡張可能な構成とする
 */
public class ErrorHandler {

    // ユーティリティクラスのためインスタンス化を禁止
    private ErrorHandler() {}

    /**
     * 計算中に発生した例外を処理する。
     *
     * @param e 発生した例外
     */
    public static void handle(Exception e) {
        // 本来はログファイルに出力するが、開発・デバッグ段階では標準エラー出力とする
        System.err.println("[Calculator Error] " + e.getMessage());
    }
}


