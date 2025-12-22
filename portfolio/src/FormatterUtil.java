/**
 * 表示整形用ユーティリティクラス。
 *
 * 本クラスは、計算結果の BigDecimal を電卓画面表示用の
 * 文字列に整形する処理を提供する。
 *
 * 数値の桁数が maxDigits を超える場合は指数表記とし、
 * 整数値の場合は不要な小数点（.0）を付与しない。
 * また、末尾の不要な 0 は stripTrailingZeros() により除去する。
 *
 * 本クラスは計算処理や状態管理は行わない。
 */
import java.math.BigDecimal;

public class FormatterUtil {

    //Model から呼ばれ、View に渡される文字列を生成する
    public static String formatForDisplay(BigDecimal value, int maxDigits) {
        /*
         * 末尾の不要な 0 を除去
         */
        if (value == null) return "0";
        //小数点の.0表示を削除、だけを整える。計算値は変えず、表示だけを整える
        BigDecimal normalized = value.stripTrailingZeros();
        // 整数部の桁数（符号なし）
        int integerDigits =normalized.abs().toBigInteger().toString().length();
        // 規定桁数以内なら通常表示
        if (integerDigits <= maxDigits) {
            return normalized.toPlainString();
        }
        /*
         * ---- 指数表記 ----
         * 先頭 1 桁 × 10^n
         */
        //10の何乗か
        int exponent = integerDigits - 1;
        //小数点を左に移動、不要な 0 を削除
        BigDecimal mantissa =normalized.movePointLeft(exponent).stripTrailingZeros();
        return mantissa.toPlainString() + "e" + exponent;//e を使う
    }
}




