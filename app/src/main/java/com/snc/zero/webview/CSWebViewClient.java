package com.snc.zero.webview;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.snc.zero.log.Logger;
import com.snc.zero.util.IntentUtil;

import java.net.URISyntaxException;

/**
 * Custom WebView Client
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class CSWebViewClient extends WebViewClient {
    private static final String TAG = CSWebViewClient.class.getSimpleName();
    private static final String PREFIX = "[WEBVIEW] ";

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Logger.i(TAG, PREFIX + "shouldOverrideUrlLoading() 1: " + url);

        if (url.startsWith("http://") || url.startsWith("https://")) {
            view.loadUrl(url);
            return true;
        }
        intentProcessing(view, url);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Logger.i(TAG, PREFIX + "shouldOverrideUrlLoading() 2: " + request.getUrl());

        String url = Uri.decode(request.getUrl().toString());

        if (url.startsWith("http://") || url.startsWith("https://")) {
            if (request.isRedirect()) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        }
        intentProcessing(view, request.getUrl().toString());
        return true;
    }

    private void intentProcessing(WebView view, String urlString) {
        String url = Uri.decode(urlString);

        if (url.startsWith("intent:")) {
            try {
                IntentUtil.intentScheme(view.getContext(), url);
            } catch (URISyntaxException e) {
                Logger.e(TAG, e);
            } catch (ActivityNotFoundException e) {
                Logger.e(TAG, e);
            }
            return;
        }

        try {
            IntentUtil.view(view.getContext(), Uri.parse(url));
        } catch (ActivityNotFoundException e) {
            Logger.e(TAG, e);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Logger.i(TAG, PREFIX + "onPageStarted(): " + url);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Logger.i(TAG, PREFIX + "onPageFinished(): " + url);
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        Logger.e(TAG, PREFIX + "onReceivedSslError(): url[" + view.getUrl() + "],  handler[" + handler + "],  error[" + error + "]");

        if (SslError.SSL_NOTYETVALID == error.getPrimaryError()) {
            handler.proceed();
        } else if (SslError.SSL_EXPIRED == error.getPrimaryError()) {
            handler.proceed();
        } else if (SslError.SSL_IDMISMATCH == error.getPrimaryError()) {
            handler.proceed();
        } else if (SslError.SSL_UNTRUSTED == error.getPrimaryError()) {
            handler.proceed();
        } else if (SslError.SSL_DATE_INVALID == error.getPrimaryError()) {
            handler.proceed();
        } else if (SslError.SSL_INVALID == error.getPrimaryError()) {
            handler.proceed();
        } else {
            handler.proceed();
        }

        //super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        Logger.e(TAG, PREFIX + "onReceivedHttpError(): url[" + view.getUrl() + "]");
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Logger.e(TAG, "onReceivedError(): url[" + view.getUrl() + "],  errorCode[" + errorCode + "],  description[" + description + "],  failingUrl[" + failingUrl + "]");
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        Logger.e(TAG, "onReceivedError(VERSION=M): url[" + view.getUrl() + "],  errorCode[" + error.getErrorCode() + "],  description[" + error.getDescription() + "]");
        super.onReceivedError(view, request, error);
    }

}
