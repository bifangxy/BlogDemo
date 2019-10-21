package com.xy.rxjavademo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xy.rxjavademo.R;
import com.xy.rxjavademo.data.CreateOperatorData;

import java.util.List;

/**
 * Created by xieying on 2019/10/18.
 * Description：
 */
public class RxJavaOperatorAdapter extends BaseQuickAdapter<CreateOperatorData, BaseViewHolder> {

    public RxJavaOperatorAdapter(@Nullable List<CreateOperatorData> data) {
        super(R.layout.item_operator, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CreateOperatorData item) {
        helper.setText(R.id.tv_operator_name, item.getOperatorName());
    }
}
