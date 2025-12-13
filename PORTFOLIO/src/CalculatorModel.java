import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CalculatorModel {

    // ================================
    // 状態
    // ================================
    private enum State {
        INPUT,   // 入力中
        RESULT,  // 結果表示中
        ERROR    // エラー表示中（Cでのみ復帰）
    }

    // 電卓状態
    private State state = State.INPUT;

    // 左オペランド
    private BigDecimal leftOperand = null;

    // 現在入力中の数値
    private StringBuilder currentInput = new StringBuilder();

    // 保留中の演算子
    private String pendingOp = null;

    // 桁数制限（符号と小数点はカウントしない）
    private static final int MAX_DIGITS = 8;

    // BigDecimal 計算精度
    private static final MathContext MC = MathContext.DECIMAL64;


    // =========================================================
    // 外部からの入力（数字・演算子・=・.・C）
    // =========================================================
    public void processInput(String cmd) {

        // C → 全リセット
        if (cmd.equals("C")) {
            clear();
            return;
        }

        // ERROR 状態は C 以外受け付けない
        if (state == State.ERROR) return;

        switch (cmd) {
            case "+":
            case "-":
            case "×":
            case "÷":
                inputOperator(cmd);
                break;

            case "=":
                inputEqual();
                break;

            case ".":
                inputDot();
                break;

            default: // 数字
                if (cmd.matches("[0-9]")) {
                    inputDigit(cmd);
                }
                break;
        }
    }


    // =========================================================
    // 数字入力
    // =========================================================
    private void inputDigit(String digit) {

        // 結果後 → 新規入力開始
        if (state == State.RESULT) {
            currentInput.setLength(0);
            leftOperand = null;
            pendingOp = null;
            state = State.INPUT;
        }

        // 桁数チェック（符号と . は除く）
        if (countDigits(currentInput.toString()) >= MAX_DIGITS) return;

        currentInput.append(digit);
        state = State.INPUT;
    }

    // 桁数カウント
    private int countDigits(String s) {
        int c = 0;
        for (char ch : s.toCharArray()) {
            if (Character.isDigit(ch)) c++;
        }
        return c;
    }


    // =========================================================
    // 小数点入力
    // =========================================================
    private void inputDot() {

        // 結果後 → 0. から開始
        if (state == State.RESULT) {
            currentInput.setLength(0);
            currentInput.append("0.");
            leftOperand = null;
            pendingOp = null;
            state = State.INPUT;
            return;
        }

        // 未入力 → 0. 開始
        if (currentInput.length() == 0) {
            currentInput.append("0.");
            return;
        }

        // 既に小数点あり → 無視
        if (currentInput.indexOf(".") >= 0) return;

        // 桁オーバー防止
        if (countDigits(currentInput.toString()) >= MAX_DIGITS) return;

        currentInput.append(".");
    }


    // =========================================================
    // 演算子入力（＋ − × ÷）
    // =========================================================
    private void inputOperator(String op) {

        // 負号開始
        if (op.equals("-") &&
            currentInput.length() == 0 &&
            leftOperand == null) {
            currentInput.append("-");
            return;
        }

        // 演算子連打 → 上書き
        if (currentInput.length() == 0 &&
            leftOperand != null &&
            state != State.RESULT) {
            pendingOp = op;
            return;
        }

        // 直前の演算を処理
        applyPendingOperation();

        // ERROR なら終了
        if (state == State.ERROR) return;

        pendingOp = op;
        state = State.INPUT;
    }


    // =========================================================
    // =（イコール）
    // =========================================================
    private void inputEqual() {

        // 計算不可条件：
        if (pendingOp == null ||
            currentInput.length() == 0 ||
            currentInput.toString().equals("-")) {

            // 計算せず表示維持
            state = State.RESULT;
            return;
        }

        applyPendingOperation();

        if (state == State.ERROR) return;

        pendingOp = null;
        state = State.RESULT;
    }


    // =========================================================
    // 演算処理（左→右の順次計算）
    // =========================================================
    private void applyPendingOperation() {

        BigDecimal right = getCurrentValue();

        if (leftOperand == null) {
            leftOperand = right;
        } else if (pendingOp != null) {

            try {
                switch (pendingOp) {
                    case "+":
                        leftOperand = leftOperand.add(right, MC);
                        break;
                    case "-":
                        leftOperand = leftOperand.subtract(right, MC);
                        break;
                    case "×":
                        leftOperand = leftOperand.multiply(right, MC);
                        break;
                    case "÷":
                        if (right.compareTo(BigDecimal.ZERO) == 0) {
                            state = State.ERROR;
                            return;
                        }
                        leftOperand = leftOperand.divide(right, 10, RoundingMode.HALF_UP);
                        break;
                }

            } catch (Exception e) {
                state = State.ERROR;
                return;
            }
        }

        currentInput.setLength(0);
    }

    private BigDecimal getCurrentValue() {

        if (currentInput.length() == 0 ||
            currentInput.toString().equals("-")) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(currentInput.toString());
        } catch (Exception e) {
            state = State.ERROR;
            return BigDecimal.ZERO;
        }
    }


    // =========================================================
    // 表示テキスト（通常／指数表記／エラー）
    // =========================================================
    public String getDisplayText() {

        if (state == State.ERROR) return "エラー";

        // 入力中 → currentInput を表示
        if (currentInput.length() > 0 && state == State.INPUT) {
            return currentInput.toString();
        }

        // 左オペランドが無い → 0
        if (leftOperand == null) return "0";

        // 結果表示
        BigDecimal val = leftOperand.stripTrailingZeros();
        String plain = val.toPlainString();

        if (plain.endsWith(".")) {
            plain = plain.substring(0, plain.length() - 1);
        }

        // 8桁以内 → 通常表示
        if (countDigits(plain) <= MAX_DIGITS) {
            return plain;
        }

        // 8桁超 → 指数表記
        String exp = val.toString().replace("E", "e");

        // 末尾0を除去
        if (exp.contains("e")) {
            String[] parts = exp.split("e");
            String mantissa = parts[0].replaceAll("0+$", "");
            if (mantissa.endsWith(".")) {
                mantissa = mantissa.substring(0, mantissa.length() - 1);
            }
            return mantissa + "e" + parts[1];
        }

        return exp;
    }


    // =========================================================
    // C（クリア）
    // =========================================================
    public void clear() {
        leftOperand = null;
        pendingOp = null;
        currentInput.setLength(0);
        state = State.INPUT;
    }
}

