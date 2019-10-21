package com.xy.rxjavademo;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xy.common.base.BaseActivity;
import com.xy.common.utils.LogUtils;
import com.xy.rxjavademo.adapter.InputContentAdapter;
import com.xy.rxjavademo.adapter.RxJavaOperatorAdapter;
import com.xy.rxjavademo.data.CreateOperatorData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xieying on 2019/10/18.
 * Description：
 */
public class CreateOperatorActivity extends BaseActivity {

    @BindView(R.id.rv_create_operator)
    RecyclerView mRvCreateOperator;

    @BindView(R.id.rv_input_content)
    RecyclerView mRvInputContent;

    private int colum;

    private List<CreateOperatorData> mCreateOperatorDataList;

    private List<String> mContentList;

    private RxJavaOperatorAdapter mRxJavaOperatorAdapter;

    private InputContentAdapter mInputContentAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_operator;
    }

    @Override
    protected void initData() {

        mRvCreateOperator.setHasFixedSize(true);
        mRvCreateOperator.setItemAnimator(new DefaultItemAnimator());//设置默认动画
        mRvCreateOperator.setLayoutManager(new GridLayoutManager(this, 3));

        mRvInputContent.setHasFixedSize(true);
        mRvInputContent.setItemAnimator(new DefaultItemAnimator());//设置默认动画
        mRvInputContent.setLayoutManager(new LinearLayoutManager(this));

        mCreateOperatorDataList = new ArrayList<>();
        mContentList = new ArrayList<>();

        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.CREATE,"create"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.JUST,"just"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.FROM_ARRAY,"from_array"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.FROM_ITERABLE,"from_iterable"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.EMPTY,"empty"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.ERROR,"error"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.NEVER,"complete"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.DEFER,"defer"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.TIMER,"timer"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.INTERVAL,"interval"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.INTERVAL_RANGE,"interval_range"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.RANGE,"range"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.RANGE_LONG,"range_long"));

        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.MERGE,"merge"));
        mCreateOperatorDataList.add(new CreateOperatorData(CreateOperatorData.CreateOperator.MERGE_ARRAY,"merge_array"));


        mRxJavaOperatorAdapter = new RxJavaOperatorAdapter(mCreateOperatorDataList);

        mInputContentAdapter = new InputContentAdapter(mContentList);

        mRvCreateOperator.setAdapter(mRxJavaOperatorAdapter);

        mRvInputContent.setAdapter(mInputContentAdapter);

    }

    @Override
    protected void initEvent() {

        mRxJavaOperatorAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CreateOperatorData createOperatorData = (CreateOperatorData) adapter.getData().get(position);
                operatorItem(createOperatorData.getCreateOperator());
            }
        });
    }

    private void operatorItem(CreateOperatorData.CreateOperator createOperator) {

        switch (createOperator) {
            case CREATE:
                create();
                break;
            case JUST:
                just();
                break;
            case FROM_ARRAY:
                fromArray();
                break;
            case FROM_ITERABLE:
                fromIterable();
                break;
            case EMPTY:
                empty();
                break;
            case ERROR:
                error();
                break;
            case NEVER:
                never();
                break;
            case DEFER:
                defer();
                break;
            case TIMER:
                timer();
                break;
            case INTERVAL:
                interval();
                break;
            case INTERVAL_RANGE:
                intervalRange();
                break;
            case RANGE:
                range();
                break;
            case RANGE_LONG:
                rangeLong();
                break;
            case MERGE:
                merge();
                break;
            case MERGE_ARRAY:
                mergeArray();
                break;
        }
    }

    public void create() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();

            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                log("onSubscribe");
            }

            @Override
            public void onNext(Integer integer) {
                log("value = " + integer);
            }

            @Override
            public void onError(Throwable e) {
                log("onError");
            }

            @Override
            public void onComplete() {
                log("onComplete");
            }
        });
    }

    /**
     * 快速创建一个被观察这对象，直接发送传入的数据
     * 注意：最多只能发送十个数据
     */
    private void just(){
        Observable.just(1,2,3,4)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log("value = " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError");
                    }

                    @Override
                    public void onComplete() {
                        log("onComplete");
                    }
                });
    }

    /**
     * 快速创建一个被观察这对象，直接发送传入的数组数据
     */
    private void fromArray(){
        Integer[] args = {1, 2, 3, 4, 5};
        Observable.fromArray(args)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log("value = " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError");
                    }

                    @Override
                    public void onComplete() {
                        log("onComplete");
                    }
                });
    }

    /**
     * 快速创建一个被观察这对象，直接发送传入的集合List数据
     */
    private void fromIterable(){
        List<Integer> args = new ArrayList<>();
        args.add(1);
        args.add(2);
        args.add(3);
        args.add(4);
        args.add(5);

        Observable.fromIterable(args)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log("value = " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError");
                    }

                    @Override
                    public void onComplete() {
                        log("onComplete");
                    }
                });
    }

    /**
     * 快速创建一个空的被观察者，只会发送onComplete事件
     */
    private void empty(){
        Observable.empty()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        log("value = " + o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError");
                    }

                    @Override
                    public void onComplete() {
                        log("onComplete");
                    }
                });
    }

    /**
     * 快速创建一个发送error的被观察者，只会发送onError事件
     */
    private void error(){
        Observable.error(new RuntimeException())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        log("value = " + o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError");
                    }

                    @Override
                    public void onComplete() {
                        log("onComplete");
                    }
                });
    }

    private void never(){
        Observable.never()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        log("value = " + o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError");
                    }

                    @Override
                    public void onComplete() {
                        log("onComplete");
                    }
                });
    }

    private Integer value = 10;

    private void defer(){
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() {
                return Observable.just(value);
            }
        });
        value = 20;
        observable.subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                log("onSubscribe");
            }

            @Override
            public void onNext(Object o) {
                log("value = " + o);
            }

            @Override
            public void onError(Throwable e) {
                log("onError");
            }

            @Override
            public void onComplete() {
                log("onComplete");
            }
        });
    }

    public void timer() {
        Observable.timer(5, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        log("value = " + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError");
                    }

                    @Override
                    public void onComplete() {
                        log("onComplete");
                    }
                });
    }

    public void interval() {
        Observable.interval(1, TimeUnit.SECONDS)
                .take(20)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        log("value = " + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError");
                    }

                    @Override
                    public void onComplete() {
                        log("onComplete");
                    }
                });
    }

    public void intervalRange() {
        Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        log("value = " + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        log("onSubscribe");
                    }
                });
    }

    public void range() {
        Observable.range(0, 10)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log("value = " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        log("onSubscribe");
                    }
                });
    }

    public void rangeLong() {
        Observable.rangeLong(1, 10)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        log("onSubscribe");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        log("value = " + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError");
                    }

                    @Override
                    public void onComplete() {
                        log("onComplete");
                    }
                });
    }

    private void merge(){
        Observable.merge(Observable.intervalRange(0,4,1,1,TimeUnit.SECONDS),
                Observable.intervalRange(6,4,1,1,TimeUnit.SECONDS),
                Observable.intervalRange(10,4,1,1,TimeUnit.SECONDS))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        log("merge value = "+aLong);
                    }
                });
    }

    private void mergeArray(){
        Observable.mergeArray(Observable.intervalRange(0,4,1,1,TimeUnit.SECONDS),
                Observable.intervalRange(6,4,1,1,TimeUnit.SECONDS),
                Observable.intervalRange(10,4,1,1,TimeUnit.SECONDS),
                Observable.intervalRange(14,4,1,1,TimeUnit.SECONDS),
                Observable.intervalRange(18,4,1,1,TimeUnit.SECONDS))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        log("mergeArray value = "+aLong);
                    }
                });
    }


    private void log(String msg) {
        LogUtils.d(msg);
        mContentList.add(msg);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mInputContentAdapter.notifyDataSetChanged();
                mRvInputContent.scrollToPosition(mInputContentAdapter.getData().size()-1);
            }
        });
    }
}
