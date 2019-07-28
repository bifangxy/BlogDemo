package xy.animationdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.xy.common.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initEvent() {

    }

    @OnClick(R.id.bt_frame_animation)
    public void toFrameAnimation(){
        startActivity(new Intent(this,FrameAnimationActivity.class));
    }

    @OnClick(R.id.bt_tween_animation)
    public void toTweenAnimation(){

    }

    @OnClick(R.id.bt_property_animation)
    public void toPropertyAnimation(){

    }
}
