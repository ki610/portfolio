/**
 * enumによって式の型翻訳を行うクラス
 * -「UI表示文字」→「計算ロジック」の変換
 * -更新：enumMapからHashMapに変更
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public enum Operator {
    

    ADD("+", "+", BigDecimal::add),
    SUB("-", "-", BigDecimal::subtract),
    ML("×", "*", BigDecimal::multiply),
    DIV("÷", "/", (l, r) -> {
        if (r.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Divide by zero");
        }
        return l.divide(r, 10, RoundingMode.HALF_UP);
    });
    /** UIに表示する文字 */
    private final String display;

    /** 内部処理用の記号 */
    private final String symbol;
    
    private final BiFunction<BigDecimal, BigDecimal, BigDecimal> action;
    Operator(String display,
            String symbol,
            BiFunction<BigDecimal, BigDecimal, BigDecimal> action) {
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

    /* ---------- UI表示 → Operator のマッピング ---------- */
    private static final Map<String, Operator> DISPLAY_MAP = new HashMap<>();

    static {
        for (Operator op : values()) {
            DISPLAY_MAP.put(op.display, op);
        }
    }

    /** UIボタン文字から Operator を取得 */
    public static Operator fromDisplay(String display) {
        return DISPLAY_MAP.get(display);
    }
}




