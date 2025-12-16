/**
 * 入力状態を分類し、他クラスに振り分けるクラス
 * -
 */
public class CalculatorController {

    private final CalculatorModel model;
    private final CalculatorFrame view;

    public CalculatorController(CalculatorModel model, CalculatorFrame view) {
        this.model = model;
        this.view = view;
    }

    // ------------------------
    // 数字入力
    // ------------------------
    public void onDigit(char ch) {
        model.appendDigit(ch);
        updateView();
    }

    // ------------------------
    // 小数点
    // ------------------------
    public void onDot() {
        model.appendDot();
        updateView();
    }

    // ------------------------
    // 演算子
    // ------------------------
    public void onOperator(String displaySymbol) {
        Operator op = Operator.fromDisplay(displaySymbol);
        if (op == null) return;

        model.inputOperator(op);
        updateView();
    }

    // ------------------------
    // =
    // ------------------------
    public void onEquals() {
        model.equalsOp();
        updateView();
    }

    // ------------------------
    // C
    // ------------------------
    public void onClear() {
        model.clearAll();
        updateView();
    }

    // ------------------------
    // View更新
    // ------------------------
    private void updateView() {
        view.setDisplay(model.getDisplayText());
    }
}

