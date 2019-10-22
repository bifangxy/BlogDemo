package com.xy.rxjavademo.data;

/**
 * Created by xieying on 2019/10/18.
 * Description：
 */
public class CreateOperatorData extends RxJavaOperatorData {

    private CreateOperator mCreateOperator;

    public CreateOperatorData(CreateOperator createOperator,String operatorName) {
        mCreateOperator = createOperator;
        mOperatorName = operatorName;
    }

    public CreateOperator getCreateOperator() {
        return mCreateOperator;
    }

    public void setCreateOperator(CreateOperator createOperator) {
        mCreateOperator = createOperator;
    }

    public enum CreateOperator {
        CREATE,
        JUST,
        FROM_ARRAY,
        FROM_ITERABLE,
        EMPTY,
        ERROR,
        NEVER,
        DEFER,
        TIMER,
        INTERVAL,
        INTERVAL_RANGE,
        RANGE,
        RANGE_LONG,
        MERGE,
        MERGE_ARRAY,
        ZIP
    }
}
