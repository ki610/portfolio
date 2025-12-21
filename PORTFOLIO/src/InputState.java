/**
 * 入力状態を表す列挙型。
 *
 * 電卓の入力状況（数値入力中、演算子入力後、エラー状態など）を
 * 状態として管理し、CalculatorModel における
 * 入力制御および処理分岐に利用する。
 */
public enum InputState {
    READY,          // 入力前の状態（初期状態・計算終了直後など）
    INPUT_NUMBER,   // 数値入力中（0〜9 または . を入力した直後）
    INPUT_OPERATOR, // 演算子を入力した直後
    ERROR           // 0除算などエラー発生
}
