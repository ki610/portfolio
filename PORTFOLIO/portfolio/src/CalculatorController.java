/**
 * 制御クラス。
 *
 * 本クラスは、View（CalculatorFrame）から通知される
 * ボタンおよびキー入力イベントを受け取り、
 * 対応する処理を Model（CalculatorModel）へ委譲する。
 *
 * 計算ロジックや入力状態の管理は行わず、
 * Model の状態変化に応じて表示更新を View に指示する。
 */
public class CalculatorController {

    private final CalculatorModel model;
    private final CalculatorFrame view;

    public CalculatorController(CalculatorModel model, CalculatorFrame view) {
        this.model = model;
        this.view = view;
    }
    // ------------------------
    // 数字入力:数字ボタンが押されたときに呼ばれる
    // ------------------------
    public void onDigit(char digit) {
        model.appendDigit(digit);//数字入力の処理を Model に委譲
        updateView();
    }
    // ------------------------
    // 小数点:ボタン押下時
    // ------------------------
    public void onDot() {
        model.appendDot();
        updateView();
    }
    // ------------------------
    // 演算子:+ - × ÷ ボタン押下時
    // ------------------------
    public void onOperator(String operatorSymbol) {
        Operator operator = Operator.fromDisplay(operatorSymbol);
        //未対応の演算子が来た場合は何もしない
        if (operator == null) {
            return;
        }
        model.inputOperator(operator);
        updateView();
    }
    // ------------------------
    // = := ボタン押下時
    // ------------------------
    public void onEquals() {
        model.equalsOp();
        updateView();
    }
    // ------------------------
    // C :C ボタン押下時
    // ------------------------
    public void onClear() {
        model.clearAll();
        updateView();
    }
    // ------------------------
    // View更新 :入力のたびに呼ばれる共通処理
    // ------------------------
    private void updateView() {
        view.setDisplay(model.getDisplayText());
    }
}

