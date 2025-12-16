/*
 * 入力状態の管理クラス
 */
public enum InputState {
    READY,          // 入力前の状態（初期状態・計算終了直後など）
    INPUT_NUMBER,   // 数値入力中（0〜9 または . を入力した直後）
    INPUT_OPERATOR, // 演算子を入力した直後
    ERROR           // 0除算などエラー発生
}
