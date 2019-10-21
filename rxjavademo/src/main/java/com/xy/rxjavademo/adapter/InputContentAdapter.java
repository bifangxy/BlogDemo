package com.xy.rxjavademo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xy.rxjavademo.R;

import java.util.List;

/**
 * Created by xieying on 2019/10/18.
 * Description：
 */
public class InputContentAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public InputContentAdapter(@Nullable List<String> data) {
        super(R.layout.item_content,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_content,item);
    }
}
