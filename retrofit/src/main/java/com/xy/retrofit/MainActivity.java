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
import com.xy.retrofit.download.SingleDownloadListener;
import com.xy.retrofit.download.data.DownloadFile;
import com.xy.retrofit.download.DownloadListener;
import com.xy.retrofit.download.DownloadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 001;

    @BindView(R.id.tv_describe)
    TextView mTvDescribe;

    private int successCount = 0;

    private int failCount = 0;

    private DownloadFile mDownloadFile;

    private String path;

    private List<DownloadFile> mDownloadFileList = new ArrayList<>();

    private DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void start() {
            LogUtils.d("start");
        }

        @Override
        public void success(String path) {
            LogUtils.d("success "+path);
        }

        @Override
        public void progress(float progress) {
            LogUtils.d("progress "+progress);
        }

        @Override
        public void pause() {
            LogUtils.d("pause");
        }

        @Override
        public void fail() {
            LogUtils.d("fail");
        }

        @Override
        public void cancel() {
            LogUtils.d("cancel");
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    mTvDescribe.setText(String.format("success count = %s fail count = %s", successCount, failCount));
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
        path = Environment.getExternalStorageDirectory() + File.separator
                + "DownloadDemo" + File.separator + "download/";
        mDownloadFile = new DownloadFile("http://ucdl.25pp.com/fs08/2019/10/21/6/106_8934aae6ab171a529d82a04ca36cf2be.apk?cc=3483200688&fname=%E6%90%9C%E4%B9%A6%E5%A4%A7%E5%B8%88&productid=&packageid=800830119&pkg=com.flyersoft.seekbooks&vcode=161302&yingid=pp_wap_ppcn&vh=60888726ac2001ae5a1ce44545ea1e5a&sf=10508148&sh=10&appid=7646511&apprd=7646511&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2019%2F10%2F22%2F9%2F106_ab3245a34078dc339ff949bab011188f_con.png&did=a43c541f5a557fa90ded8fb2582d86b3&md5=a84f58514c580ca89eac2513f501c003", "test.apk", path);
        mDownloadFile.setNeedMultithreading(true);
        mDownloadFile.setNeedOverrideFile(true);

        mDownloadFileList.add(mDownloadFile);

        DownloadFile downloadFile = new DownloadFile("https://zhiyundata.oss-cn-shenzhen.aliyuncs.com/zyplay/share/966896/254911/2019-11-22-16:14:47.mp4", "1.mp4", path);
        downloadFile.setNeedMultithreading(true);
        downloadFile.setNeedOverrideFile(true);
        mDownloadFileList.add(downloadFile);

        DownloadFile downloadFile2 = new DownloadFile("https://zhiyundata.oss-cn-shenzhen.aliyuncs.com/zyplay/share/972125/254855/2019-11-22-12:53:15.mp4", "2.mp4", path);
        downloadFile2.setNeedMultithreading(true);
        downloadFile2.setNeedOverrideFile(true);
        mDownloadFileList.add(downloadFile2);

        DownloadFile downloadFile3 = new DownloadFile("https://zhiyundata.oss-cn-shenzhen.aliyuncs.com/zyplay/share/159929/254921/2019-11-22-16:24:30.mp4", "3.mp4", path);
        downloadFile3.setNeedMultithreading(true);
        downloadFile3.setNeedOverrideFile(true);
        mDownloadFileList.add(downloadFile3);

    }

    @Override
    protected void initEvent() {
        DownloadManager.getInstance().setSingleDownloadListener(new SingleDownloadListener() {
            @Override
            public void singleStart(String url) {
                LogUtils.d("start = " + url);
            }

            @Override
            public void singleProgress(String url, float progress) {
                LogUtils.d("progress = " + progress);
            }

            @Override
            public void singleSuccess(String url, String path) {
                LogUtils.d("success path = " + path);
            }

            @Override
            public void singleCancel(String url) {
                LogUtils.d("cancel url = " + url);
            }

            @Override
            public void singlePause(String url) {
                LogUtils.d("pause  url = " + url);
            }

            @Override
            public void singleFail(String url) {
                LogUtils.d("fail  url = " + url);
            }
        });
    }
    @OnClick(R.id.bt_signal)
    public void signalDownload(){
        DownloadFile downloadFile = new DownloadFile("http://ucdl.25pp.com/fs08/2019/10/21/6/106_8934aae6ab171a529d82a04ca36cf2be.apk?cc=3483200688&fname=%E6%90%9C%E4%B9%A6%E5%A4%A7%E5%B8%88&productid=&packageid=800830119&pkg=com.flyersoft.seekbooks&vcode=161302&yingid=pp_wap_ppcn&vh=60888726ac2001ae5a1ce44545ea1e5a&sf=10508148&sh=10&appid=7646511&apprd=7646511&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2019%2F10%2F22%2F9%2F106_ab3245a34078dc339ff949bab011188f_con.png&did=a43c541f5a557fa90ded8fb2582d86b3&md5=a84f58514c580ca89eac2513f501c003", "test.apk", path);
        downloadFile.setNeedMultithreading(true);
        downloadFile.setNeedOverrideFile(true);

        DownloadManager.getInstance().startDownload(downloadFile, new DownloadListener() {
            @Override
            public void start() {
                LogUtils.d("---start---");
            }

            @Override
            public void success(String path) {
                successCount++;
                mHandler.sendEmptyMessage(0);
                LogUtils.d("---success---"+path);
//                signalDownload();
            }

            @Override
            public void progress(float progress) {
                LogUtils.d("---progress---"+progress);
            }

            @Override
            public void pause() {
                LogUtils.d("---pause---");
            }

            @Override
            public void fail() {
                failCount++;
                mHandler.sendEmptyMessage(0);
                LogUtils.d("---fail---");
//                signalDownload();
            }

            @Override
            public void cancel() {
                LogUtils.d("---cancel---");
            }
        });
    }

    @OnClick(R.id.bt_parallel_download)
    public void parallelDownload() {
        DownloadManager.getInstance().add("http://ucdl.25pp.com/fs08/2019/10/21/6/106_8934aae6ab171a529d82a04ca36cf2be.apk?cc=3483200688&fname=%E6%90%9C%E4%B9%A6%E5%A4%A7%E5%B8%88&productid=&packageid=800830119&pkg=com.flyersoft.seekbooks&vcode=161302&yingid=pp_wap_ppcn&vh=60888726ac2001ae5a1ce44545ea1e5a&sf=10508148&sh=10&appid=7646511&apprd=7646511&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2019%2F10%2F22%2F9%2F106_ab3245a34078dc339ff949bab011188f_con.png&did=a43c541f5a557fa90ded8fb2582d86b3&md5=a84f58514c580ca89eac2513f501c003", path, "test.apk", mDownloadListener);
        DownloadManager.getInstance().add("https://zhiyundata.oss-cn-shenzhen.aliyuncs.com/zyplay/share/966896/254911/2019-11-22-16:14:47.mp4", path, "11.mp4", mDownloadListener);
        DownloadManager.getInstance().add("https://zhiyundata.oss-cn-shenzhen.aliyuncs.com/zyplay/share/159929/254921/2019-11-22-16:24:30.mp4", path, "22.mp4", mDownloadListener);

        //        DownloadManager.getInstance().addDownloadFile(mDownloadFileList);
        DownloadManager.getInstance().startParallelDownload();
    }

    @OnClick(R.id.bt_serial_download)
    public void serialDownload() {
        DownloadManager.getInstance().add("http://ucdl.25pp.com/fs08/2019/10/21/6/106_8934aae6ab171a529d82a04ca36cf2be.apk?cc=3483200688&fname=%E6%90%9C%E4%B9%A6%E5%A4%A7%E5%B8%88&productid=&packageid=800830119&pkg=com.flyersoft.seekbooks&vcode=161302&yingid=pp_wap_ppcn&vh=60888726ac2001ae5a1ce44545ea1e5a&sf=10508148&sh=10&appid=7646511&apprd=7646511&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2019%2F10%2F22%2F9%2F106_ab3245a34078dc339ff949bab011188f_con.png&did=a43c541f5a557fa90ded8fb2582d86b3&md5=a84f58514c580ca89eac2513f501c003", path, "test.apk", mDownloadListener);
        DownloadManager.getInstance().add("https://zhiyundata.oss-cn-shenzhen.aliyuncs.com/zyplay/share/966896/254911/2019-11-22-16:14:47.mp4", path, "11.mp4", mDownloadListener);
        DownloadManager.getInstance().add("https://zhiyundata.oss-cn-shenzhen.aliyuncs.com/zyplay/share/159929/254921/2019-11-22-16:24:30.mp4", path, "22.mp4", mDownloadListener);

//        DownloadManager.getInstance().addDownloadFile(mDownloadFileList);
        DownloadManager.getInstance().startSerialDownload();
    }

    @OnClick(R.id.bt_cancel)
    public void cancel(){
        DownloadManager.getInstance().cancelCurrentTask();
    }

    @OnClick(R.id.bt_pause)
    public void pause(){
        DownloadManager.getInstance().pauseCurrentTask();
    }

    @OnClick(R.id.bt_all_cancel)
    public void cancelAll(){
        DownloadManager.getInstance().cancelAllTask();
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
