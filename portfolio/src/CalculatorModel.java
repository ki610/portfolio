/**
 * Model（ドメイン）クラス。
 *
 * 本クラスは、電卓アプリにおける状態管理・入力制御・演算処理を担い、
 * 表示用の文字列を生成する中核ロジックを提供する。
 * 計算の実行タイミングは、演算子入力時および「=」入力時とする。
 * 数値入力は最大8桁までとし、桁数制限には小数点および負号は含めない。
 * また、「-」入力は負号開始としても使用可能とする。
 */
import java.math.BigDecimal;
import java.util.EnumSet;//enum を 集合として扱うためのクラス。「この状態のときは入力OK」などの制御に使う

public class CalculatorModel {
    private BigDecimal leftOperand;//左辺値（すでに確定した数値）
    private StringBuilder currentInput = new StringBuilder();//数値を文字列として保持(現在入力中の数値)
    private Operator pendingOp;
    private InputState state = InputState.READY;//電卓の現在状態
    private static final int MAX_DIGITS = 8;
    // 入力許可状態(ERROR 状態では入力を無視する)
    private static final EnumSet<InputState> INPUT_ALLOWED =EnumSet.of(InputState.READY,InputState.INPUT_NUMBER,InputState.INPUT_OPERATOR);
    // ==================================================
    // 数字入力
    // ==================================================
    //数字ボタンが押されたときに呼ばれる
    public void appendDigit(char digit) {
        if (state == InputState.ERROR) return;
        if (!INPUT_ALLOWED.contains(state)) return;
        if (countDigits() >= MAX_DIGITS) return;
        // 先頭 0 の抑制(01,02等表記にならない)
        if (currentInput.length() == 1 && currentInput.charAt(0) == '0') {
            currentInput.setLength(0);
        }
        currentInput.append(digit);//入力された数字を末尾に追加
        state = InputState.INPUT_NUMBER;//「数値入力中」の状態に変更
    }
    // ==================================================
    // 小数点入力
    // ==================================================
    public void appendDot() {
        if (state == InputState.ERROR) return;
        if (!INPUT_ALLOWED.contains(state)) return;//入力不可状態なら無視
        if (currentInput.toString().contains(".")) return;//すでに . があれば2つ目は禁止
        if (countDigits() >= MAX_DIGITS) return;
        if (currentInput.length() == 0) {
            // 数値未入力時の "." は無効（仕様）
            return;
        }
        currentInput.append(".");
        state = InputState.INPUT_NUMBER;//数値入力中に変更
    }
    // ==================================================
    // 演算子入力
    // ==================================================
    public void inputOperator(Operator operator) {
        if (state == InputState.ERROR) return;//エラー中は無視
        if (!INPUT_ALLOWED.contains(state)) return;//入力不可なら無視
        /*
         * 【負号開始】
         * 下記3つの条件時は- を負号として扱う
         * ・演算子が SUB
         * ・現在数値未入力
         * ・READY または 演算子入力直後
         *  数値入力扱いにして終了
         */
        if (operator == Operator.SUB&& currentInput.length() == 0&& (state == InputState.READY || state == InputState.INPUT_OPERATOR)){
            currentInput.append("-");//- を数値の先頭として追加
            state = InputState.INPUT_NUMBER;
            return;
        }
        /*
         * 【演算子上書き】
         * 数値未入力状態での演算子入力は pendingOp を上書き
         */
        if (currentInput.length() == 0) {
            pendingOp = operator;//直前の演算子を上書き
            state = InputState.INPUT_OPERATOR;//演算子入力扱いにして終了
            return;
        }
        /*
         * 【計算処理】
         * 初回演算なら現在入力値を左辺にセット
         * すでに左辺と演算子がある場合計算を実行
         * 計算中にエラーが出たら中断
         */
        if (leftOperand == null) {
            leftOperand = getCurrentValue();
        } else if (pendingOp != null) {
            applyOperator();
            if (state == InputState.ERROR) return;
        }
        currentInput.setLength(0);//入力中の数値をクリア
        pendingOp = operator;//直前の演算子を上書き
        state = InputState.INPUT_OPERATOR;//新しい演算子を保持
    }
    // ==================================================
    // = 入力
    // ==================================================
    public void equalsOp() {
        /*
         * 初回演算なら現在入力値を左辺にセット
         * すでに左辺と演算子がある場合計算を実行
         * 計算中にエラーが出たら中断
         */
        System.out.println(leftOperand.toPlainString());
        if (state == InputState.ERROR) return;
        if (pendingOp == null || currentInput.length() == 0) return;
        applyOperator();
        if (state == InputState.ERROR) return;
        pendingOp = null;
        currentInput.setLength(0);//入力中の数値をクリア
        state = InputState.READY;//READY状態にして終了
    }
    // ==================================================
    // C（クリア）
    // ==================================================
    public void clearAll() {
        leftOperand = null;
        currentInput.setLength(0);
        pendingOp = null;
        state = InputState.READY;
    }
    // ==================================================
    // 内部計算処理
    // ==================================================
    //実際の計算処理
    private void applyOperator() {
        /*
         * 右辺値を取得
         * 演算子ごとの計算を実行
         * 例外発生時はエラー状態へ
         */
        try {
            BigDecimal right = getCurrentValue();
            leftOperand = pendingOp.apply(leftOperand, right);
        } catch (Exception e) {
            ErrorHandler.handle(e);
            state = InputState.ERROR;
        }
    }
    // ==================================================
    // 補助メソッド
    // ==================================================
    //入力中の数値を BigDecimal に変換
    private BigDecimal getCurrentValue() {
        if (currentInput.length() == 0) return BigDecimal.ZERO;//未入力なら 0 扱い
        return new BigDecimal(currentInput.toString());//文字列 → 数値変換
    }
    //数字"のみ"の桁数を数える
    private int countDigits() {
        int count = 0;
        for (int i = 0; i < currentInput.length(); i++) {
            //- や . は数えない
            if (Character.isDigit(currentInput.charAt(i))) {
                count++;
            }
        }
        return count;
    }
    // ==================================================
    // 表示用
    // ==================================================
    //画面に表示する文字列を返す
    public String getDisplayText() {
        /*
         * 入力中の値を表示
         * 何もなければ 0
         * 計算結果を整形して表示
         */
        if (state == InputState.ERROR) return "ERROR";
        if (currentInput.length() > 0) return currentInput.toString();
        if (leftOperand == null) return "0";
        return FormatterUtil.formatForDisplay(leftOperand, MAX_DIGITS);
    }
}
