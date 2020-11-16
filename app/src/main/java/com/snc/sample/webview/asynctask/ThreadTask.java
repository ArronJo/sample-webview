package com.snc.sample.webview.asynctask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.snc.zero.log.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AsyncTask
 *
 * @author mcharima5@gmail.com
 * @since 2020
 */
public abstract class ThreadTask<Params, Progress, Result> implements Runnable {

    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;

    private Thread mnThread;

    private Params[] mArgument;

    private Result mResult;

    private final AtomicBoolean mCancelled = new AtomicBoolean();

    private final Handler mHandler;

    public ThreadTask() {
        mHandler = new InternalHandler(Looper.getMainLooper());
    }

    @SafeVarargs
    final public void execute(final Params... arg) {
        mArgument = arg;

        onPreExecute();

        mnThread = new Thread(this);
        mnThread.start();

        try {
            mnThread.join();
        }
        catch (InterruptedException e) {
            mCancelled.set(true);
            Logger.e(this.getClass().getSimpleName(), e);
            postResult(null);
            return;
        }

        postResult(mResult);
    }

    @SafeVarargs
    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            getHandler().obtainMessage(MESSAGE_POST_PROGRESS,
                    new ThreadTaskResult<>(this, values)).sendToTarget();
        }
    }

    public final boolean isCancelled() {
        return mCancelled.get();
    }

    public final void cancel() {
        mCancelled.set(true);
        mnThread.interrupt();
    }

    @Override
    public void run() {
        mResult = doInBackground(mArgument);
    }

    private Handler getHandler() {
        return mHandler;
    }

    private void postResult(Result result) {
        if (isCancelled()) {
            return;
        }

        @SuppressWarnings("unchecked")
        Message message = getHandler().obtainMessage(MESSAGE_POST_RESULT,
                new ThreadTaskResult<>(this, result));
        message.sendToTarget();
    }

    private void finish(Result result) {
        if (!isCancelled()) {
            onPostExecute(result);
        }
    }

    protected void onPreExecute() {

    }

    @SuppressWarnings("unchecked")
    protected abstract Result doInBackground(Params... arg);

    @SuppressWarnings("unchecked")
    protected void onProgressUpdate(Progress... progress) {

    }

    protected void onPostExecute(Result result) {

    }

    private class ThreadTaskResult<Data> {
        final ThreadTask<Params, Progress, Result> mTask;
        final Data[] mData;

        @SafeVarargs
        public ThreadTaskResult(ThreadTask<Params, Progress, Result> task, Data... data) {
            mTask = task;
            mData = data;
        }
    }

    private class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public void handleMessage(Message msg) {
            ThreadTaskResult<?> result = (ThreadTaskResult<?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish((Result) result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate((Progress) result.mData);
                    break;
            }
        }
    }
}
