
/**
 * 計算実行クラス
 * -実行のタイミングは演算子入力と= 入力の際のみ
 */
import java.math.BigDecimal;
import java.util.EnumSet;

public class CalculatorModel {

    private BigDecimal leftOperand;
    private StringBuilder currentInput = new StringBuilder();
    private Operator pendingOp;
    private InputState state = InputState.READY;

    private static final int MAX_DIGITS = 8;

    /* ---------- EnumSet ---------- */
    private static final EnumSet<InputState> INPUT_ALLOWED =
            EnumSet.of(InputState.READY, InputState.INPUT_NUMBER, InputState.INPUT_OPERATOR);

    // ------------------------
    // 数字入力
    // ------------------------
    //charなのはViewの入力単位が１桁だから
    public void appendDigit(char digit) {
        if (!INPUT_ALLOWED.contains(state)) return;

        if (currentInput.length() >= MAX_DIGITS) return;

        if (currentInput.length() == 1 && currentInput.charAt(0) == '0') {
            currentInput.setLength(0);
        }

        currentInput.append(digit);
        state = InputState.INPUT_NUMBER;
    }

    // ------------------------
    // 小数点
    // ------------------------
    public void appendDot() {
        if (!INPUT_ALLOWED.contains(state)) return;
        if (currentInput.length() == 0) {
            currentInput.append("0.");
        } else if (!currentInput.toString().contains(".")) {
            currentInput.append(".");
        }

        state = InputState.INPUT_NUMBER;
    }

    // ------------------------
    // 演算子
    // ------------------------
    public void inputOperator(Operator op) {
        if (!INPUT_ALLOWED.contains(state)) return;

        if (currentInput.length() == 0) {
            pendingOp = op;
            return;
        }

        if (leftOperand == null) {
            leftOperand = getCurrentValue();
        } else if (pendingOp != null) {
            applyOperator();
        }

        currentInput.setLength(0);
        pendingOp = op;
        state = InputState.INPUT_OPERATOR;
    }

    // ------------------------
    // =
    // ------------------------
    public void equalsOp() {
        if (state == InputState.ERROR) return;
        if (pendingOp == null || currentInput.length() == 0) return;

        applyOperator();
        pendingOp = null;
        currentInput.setLength(0);
        state = InputState.READY;
    }

    // ------------------------
    // C
    // ------------------------
    public void clearAll() {
        leftOperand = null;
        currentInput.setLength(0);
        pendingOp = null;
        state = InputState.READY;
    }

    // ------------------------
    // 内部計算
    // ------------------------
    private void applyOperator() {
        try {
            BigDecimal right = getCurrentValue();
            leftOperand = pendingOp.apply(leftOperand, right);
        } catch (Exception e) {
            ErrorHandler.handle(e);
            state = InputState.ERROR;
        }
    }

    private BigDecimal getCurrentValue() {
        if (currentInput.length() == 0) return BigDecimal.ZERO;
        return new BigDecimal(currentInput.toString());
    }

    // ------------------------
    // 表示
    // ------------------------
    public String getDisplayText() {
        if (state == InputState.ERROR) return "ERROR";
        if (currentInput.length() > 0) return currentInput.toString();
        if (leftOperand == null) return "0";
        return FormatterUtil.formatForDisplay(leftOperand, MAX_DIGITS);
    }
}



