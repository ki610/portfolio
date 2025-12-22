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
import java.util.EnumSet;

public class CalculatorModel {

    private BigDecimal leftOperand;
    private StringBuilder currentInput = new StringBuilder();
    private Operator pendingOp;
    private InputState state = InputState.READY;
    private static final int MAX_DIGITS = 8;

    //入力許可状態
    private static final EnumSet<InputState> INPUT_ALLOWED =
            EnumSet.of(
                    InputState.READY,
                    InputState.INPUT_NUMBER,
                    InputState.INPUT_OPERATOR
            );

    // ==================================================
    // 数字入力
    // ==================================================
    // charなのは View から「1桁ずつ」渡ってくるため
    public void appendDigit(char digit) {
        if (state == InputState.ERROR) return;
        if (!INPUT_ALLOWED.contains(state)) return;    
        if (countDigits() >= MAX_DIGITS) return;// 数字のみで桁数制限
        // 先頭 0 の抑制（0 → 5 で 5 にする）
        if (currentInput.length() == 1 && currentInput.charAt(0) == '0') {
            currentInput.setLength(0);
        }
        currentInput.append(digit);
        state = InputState.INPUT_NUMBER;
    }

    // ==================================================
    // 小数点入力 (桁数制限には小数点を含めない)
    // ==================================================
    public void appendDot() {
        if (state == InputState.ERROR) return;
        if (!INPUT_ALLOWED.contains(state)) return;
        if (currentInput.toString().contains(".")) return;
        if (countDigits() >= MAX_DIGITS) return;
        if (currentInput.length() == 0) {
            currentInput.append("0.");
        } else {
            currentInput.append(".");
        }
        state = InputState.INPUT_NUMBER;
    }


    // ==================================================
    // 演算子入力
    // ==================================================
    public void inputOperator(Operator operator) {
        if (state == InputState.ERROR) return;
        if (!INPUT_ALLOWED.contains(state)) return;

        //負号開始
        if (operator == Operator.SUB
                && (state == InputState.READY || state == InputState.INPUT_OPERATOR)
                && currentInput.length() == 0) {

            currentInput.append("-");
            state = InputState.INPUT_NUMBER;
            return;
        }
        // 演算子連打 → 上書き
        if (currentInput.length() == 0) {
            pendingOp = operator;
            return;
        }
        // 計算処理
        if (leftOperand == null) {
            leftOperand = getCurrentValue();
        } else if (pendingOp != null) {
            applyOperator();
        }
        currentInput.setLength(0);
        pendingOp = operator;
        state = InputState.INPUT_OPERATOR;
    }

    // ==================================================
    // = 入力
    // ==================================================
    public void equalsOp() {
        if (state == InputState.ERROR) return;
        if (pendingOp == null || currentInput.length() == 0) return;

        applyOperator();
        pendingOp = null;
        currentInput.setLength(0);
        state = InputState.READY;
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
    private void applyOperator() {
        try {
            BigDecimal right = getCurrentValue();
            leftOperand = pendingOp.apply(leftOperand, right);
        } catch (Exception e) {
            ErrorHandler.handle(e);
            state = InputState.ERROR;// ERROR 状態へ遷移
        }
    }
    
    // ==================================================
    // 補助メソッド
    // ==================================================
    private BigDecimal getCurrentValue() {
        if (currentInput.length() == 0) return BigDecimal.ZERO;
        return new BigDecimal(currentInput.toString());
    }

    // 数字のみの桁数を数える（. と - は除外）
    private int countDigits() {
        int count = 0;
        for (int i = 0; i < currentInput.length(); i++) {
            if (Character.isDigit(currentInput.charAt(i))) {
                count++;
            }
        }
        return count;
    }

    // ==================================================
    // 表示用
    // ==================================================
    public String getDisplayText() {
        if (state == InputState.ERROR) return "ERROR";
        if (currentInput.length() > 0) return currentInput.toString();
        if (leftOperand == null) return "0";

        return FormatterUtil.formatForDisplay(leftOperand, MAX_DIGITS);
    }
}




