package xy.animationdemo;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;

import com.xy.common.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xieying on 2019/7/28
 * Function:
 */
public class FrameAnimationActivity extends BaseActivity {
    @BindView(R.id.iv_animation)
    ImageView mIvAnimation;

    private AnimationDrawable mAnimationDrawable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_frame_animation;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {


//方式1：以xml的形式来获取动画对象
//        mIvAnimation.setImageResource(R.drawable.animation);
//        mAnimationDrawable = (AnimationDrawable) mIvAnimation.getDrawable();


//方式2 以代码的形式设置
        mAnimationDrawable = new AnimationDrawable();
        for (int i = 0;i<5;i++){
            int id = getResources().getIdentifier("img0000"+i,"drawable",getPackageName());
            Drawable drawable = getResources().getDrawable(id,null);
            mAnimationDrawable.addFrame(drawable,200);
        }
        mAnimationDrawable.setOneShot(true);
        mIvAnimation.setImageDrawable(mAnimationDrawable);
    }

    @Override
    protected void initEvent() {

    }

    @OnClick(R.id.bt_start)
    public void startAnimation() {
        if (mAnimationDrawable != null){
            //在start前需要加上stop，否则动画会停在最后一帧，只能播放一次
            mAnimationDrawable.stop();
            mAnimationDrawable.start();
        }
    }

    @OnClick(R.id.bt_stop)
    public void stopAnimation() {
        if (mAnimationDrawable != null)
            mAnimationDrawable.stop();
    }
}
