package com.xy.rxjavademo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xieying on 2019/10/18.
 * Description：
 */
public class RxJavaTest {

    public static void main(String ars[]){

        RxJavaTest rxJavaTest = new RxJavaTest();
//        rxJavaTest.create();
//        rxJavaTest.just();
//        rxJavaTest.fromArray();
//        rxJavaTest.fromIterable();
//        rxJavaTest.empty();
//        rxJavaTest.error();
//        rxJavaTest.defer();
//        rxJavaTest.timer();
//        rxJavaTest.interval();
//        rxJavaTest.map();
//        rxJavaTest.concatMap();
//        rxJavaTest.buffer();
//        rxJavaTest.concat();
//        rxJavaTest.concatArray();
        rxJavaTest.merge();
    }

    /**
     * 创建一个被观察者对象
     */
    private void create() {
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
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("value = " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
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
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("value = " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
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
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("value = " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
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
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("value = " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
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
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println("value = " + o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
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
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println("value = " + o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
    }

    private void never(){
        Observable.error(new RuntimeException())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println("value = " + o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
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
                System.out.println("onSubscribe");
            }

            @Override
            public void onNext(Object o) {
                System.out.println("value = " + o);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }

    private void timer(){
        Observable.timer(5, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        System.out.println("value = " + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
    }

    private void interval(){
        Observable.interval(1,TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        print("onSubscribe");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        print("value = " + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        print("onError");
                    }

                    @Override
                    public void onComplete() {
                        print("onComplete");
                    }
                });
    }


    private void map(){
        Observable.just(1,2,3,4,5)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return "this is "+integer;
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        print("onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        print("value = "+s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        print("onError");
                    }

                    @Override
                    public void onComplete() {
                        print("onComplete");
                    }
                });
    }

    private void flatMap(){
        Observable.just(1,2,3,4,5)
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        List<String> dataList = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            dataList.add("flatMap new value = "+integer);
                        }
                        Thread.sleep(1000);
                        return Observable.fromIterable(dataList);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                       print(s);
                    }
                });
    }

    private void concatMap(){
        Observable.just(1,2,3,4,5)
                .concatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        List<String> dataList = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            dataList.add("concatMap new value = "+integer);
                        }
                        Thread.sleep(1000);
                        return Observable.fromIterable(dataList);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        print(s);
                    }
                });
    }

    private void buffer(){
        Observable.just(1,2,3,4,5,6)
                .buffer(3,3)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        print("size = "+integers.size());
                        for (Integer value:integers) {
                            print("value = "+value);
                        }
                    }
                });
    }

    private void concat(){
       Observable.concat(Observable.just(1,2,3,4),
               Observable.just(5,6,7,8),
               Observable.just(9,10,11,12),
               Observable.just(13,14,15,16))
               .subscribe(new Consumer<Integer>() {
                   @Override
                   public void accept(Integer integer) throws Exception {
                       print("concat value = "+integer);
                   }
               });
    }

    private void concatArray(){
        Observable.concatArray(Observable.just(1,2,3,4),
                Observable.just(5,6,7,8),
                Observable.just(9,10,11,12),
                Observable.just(13,14,15,16),
                Observable.just(17,18,19,20))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        print("concatArray value = "+integer);
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
                        print("merge value = "+aLong);
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
                        print("mergeArray value = "+aLong);
                    }
                });
    }


    private void print(String msg){
        System.out.println(msg);
    }


}
