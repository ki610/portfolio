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
    public static String formatForDisplay(BigDecimal value, int maxDigits) {
    if (value == null) return "0";
        BigDecimal normalized = value.stripTrailingZeros();//末尾の無意味な0除去
        String plain = normalized.toPlainString();

        // マイナス符号と小数点を除いた桁数
        int digits = plain.replace("-", "").replace(".", "").length();

        //結果8桁超過時は指数表記
        if (digits > maxDigits) {
            // BigDecimal の指数表記を使用（不要な 0 は既に除去済み）
            String exp = normalized.toEngineeringString();

            // E → e に統一（仕様）
            return exp.replace("E", "e");
        }

        return plain;
    }
}

