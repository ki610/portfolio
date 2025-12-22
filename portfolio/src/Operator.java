/**
 * 演算子を表す列挙型。
 *
 * 本列挙型は、UI 上で入力される演算子表示と、
 * 計算処理で使用する演算ロジックを対応付ける。
 *
 * 各演算子は対応する計算処理を関数として保持し、
 * CalculatorModel から呼び出される。
 *
 * 除算時の 0 除算などの計算エラーは例外として通知し、
 * エラー状態への遷移判断は Model 側に委ねる。
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public enum Operator {

    ADD("+", "+", BigDecimal::add),
    SUB("-", "-", BigDecimal::subtract),
    MUL("×", "*", BigDecimal::multiply),
    DIV("÷", "/", (left, right) -> {
        if (right.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Divide by zero");
        }
        return left.divide(right, 10, RoundingMode.HALF_UP);
    });

    //UIに表示する文字（+, × など）
    private final String display;

    //内部処理・ログ用の記号（+, *, / など） 
    private final String symbol;

    //計算処理 
    private final BiFunction<BigDecimal, BigDecimal, BigDecimal> action;
    Operator(
            String display,
            String symbol,
            BiFunction<BigDecimal, BigDecimal, BigDecimal> action
    ) {
        this.display = display;
        this.symbol = symbol;
        this.action = action;
    }

    public String getDisplay() {
        return display;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal apply(BigDecimal left, BigDecimal right) {
        return action.apply(left, right);
    }

    //UI表示 → Operator のマッピング 

    private static final Map<String, Operator> DISPLAY_MAP = new HashMap<>();

    static {
        for (Operator operator : values()) {
            DISPLAY_MAP.put(operator.display, operator);
        }
    }

    /**
     * UIボタン表示文字から Operator を取得する
     *
     * @param display UI表示文字
     * @return 対応する Operator（存在しない場合は null）
     */
    public static Operator fromDisplay(String display) {
        return DISPLAY_MAP.get(display);
    }
}