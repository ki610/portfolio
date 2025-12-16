import java.math.BigDecimal;

public class FormatterUtil {

    /**
     * 計算結果の BigDecimal を電卓表示用の文字列に整形する。
     * - maxDigits を超えたら指数表記
     * - 整数は ".0" を付けない
     * - 無駄な 0 は stripTrailingZeros() で削除
     */
    public static String formatForDisplay(BigDecimal value, int maxDigits) {
        if (value == null) return "0";

        BigDecimal normalized = value.stripTrailingZeros();
        String plain = normalized.toPlainString();

        // マイナス符号と小数点以外の桁数を取得
        int digits = plain.replace("-", "").replace(".", "").length();

        // 指数表記判定
        if (digits > maxDigits) {
            // Java の %e は必ず小文字 e（電卓でも一般的）
            return String.format("%e", normalized);
        }

        return plain;
    }
}

