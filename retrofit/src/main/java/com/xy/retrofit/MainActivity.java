package com.xy.retrofit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.xy.common.base.BaseActivity;
import com.xy.common.utils.LogUtils;
import com.xy.retrofit.download.data.DownloadFile;
import com.xy.retrofit.download.DownloadListener;
import com.xy.retrofit.download.DownloadManager;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 001;

    @BindView(R.id.tv_describe)
    TextView mTvDescribe;

    private int successCount = 0;

    private int failCount = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    mTvDescribe.setText("success count = " + successCount + " fail count = " + failCount);
                    break;
                default:
                    break;
            }
        }
    };

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

    @OnClick(R.id.bt_translate)
    public void translate(){
       /* TranslationManager.translation("试试看", new TranslationManager.OnTranslationResultListener() {
            @Override
            public void success(Translation translation) {
                mTvDescribe.setText(translation.toString());
            }

            @Override
            public void fail(String error) {
                mTvDescribe.setText("翻译失败");
                LogUtils.d("s = "+error);
            }
        });*/
       String path = Environment.getExternalStorageDirectory() + File.separator
               + "DownloadDemo" + File.separator + "download/";
        DownloadFile downloadFile = new DownloadFile("11.apk","http://dldir1.qq.com/weixin/android/weixin703android1400.apk",path);
        downloadFile.setNeedMultithreading(true);
        downloadFile.setNeedOverrideFile(true);
        DownloadManager.getInstance().startDownload(downloadFile);

        DownloadManager.getInstance().setDownloadListener(new DownloadListener() {
            @Override
            public void cancel() {
                LogUtils.d("cancel Task");
            }

            @Override
            public void success() {
                successCount++;
                mHandler.sendEmptyMessage(0);
//                translate();
                LogUtils.d("success download");
            }

            @Override
            public void progress(float progress) {
                LogUtils.d("download progress = " + progress);
            }

            @Override
            public void fail() {
                failCount++;
                mHandler.sendEmptyMessage(0);
//                translate();
                LogUtils.d("fail download");
            }
        });
    }

    @OnClick(R.id.bt_cancel)
    public void cancel(){
        DownloadManager.getInstance().cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (!checkPermission(permission)) {
            if (shouldShowRationale(permission)) {
                showMessage("需要读写权限...");
            }
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    protected boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    protected boolean shouldShowRationale(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
    }
}
