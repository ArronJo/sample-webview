package com.snc.zero.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.snc.sample.webview.BuildConfig;
import com.snc.zero.dialog.DialogBuilder;
import com.snc.zero.util.EnvUtil;
import com.snc.zero.util.IntentUtil;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;

import androidx.annotation.RequiresApi;
import androidx.webkit.WebViewAssetLoader;
import timber.log.Timber;

/**
 * Custom WebView Client
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class CSWebViewClient extends WebViewClient {
    private final WebViewAssetLoader assetLoader;

    public CSWebViewClient(Context context) {
        if (BuildConfig.FEATURE_WEBVIEW_ASSET_LOADER) {
            this.assetLoader = new WebViewAssetLoader.Builder()
                    .setDomain(BuildConfig.ASSET_BASE_DOMAIN)
                    .addPathHandler(BuildConfig.RES_PATH, new WebViewAssetLoader.ResourcesPathHandler(context))
                    .addPathHandler(BuildConfig.ASSET_PATH, new WebViewAssetLoader.AssetsPathHandler(context))
                    .addPathHandler(BuildConfig.INTERNAL_PATH, new WebViewAssetLoader.InternalStoragePathHandler(context, EnvUtil.getInternalFilesDir(context, "public")))
                    .build();
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings({"unused", "RedundantSuppression"})   // use the old one for compatibility with all API levels.
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        Timber.d("[WEBVIEW] shouldInterceptRequest(API 20 below):  url[" + url + "]");

        Uri uri = Uri.parse(url);

        // CORS
        if ("uploadimage".equalsIgnoreCase(uri.getScheme())) {
            return executeCustomScheme(view.getContext(), uri);
        }

        if (BuildConfig.FEATURE_WEBVIEW_ASSET_LOADER) {
            return this.assetLoader.shouldInterceptRequest(Uri.parse(url));
        }
        return super.shouldInterceptRequest(view, url);
    }

    @Override
    @RequiresApi(21)
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Timber.d("[WEBVIEW] shouldInterceptRequest(API 21 after):  url[" + request.getUrl() + "]");

        Uri uri = request.getUrl();

        // CORS
        if ("uploadimage".equalsIgnoreCase(uri.getScheme())) {
            return executeCustomScheme(view.getContext(), uri);
        }

        if (BuildConfig.FEATURE_WEBVIEW_ASSET_LOADER) {
            return this.assetLoader.shouldInterceptRequest(request.getUrl());
        }
        return super.shouldInterceptRequest(view, request);
    }

    private WebResourceResponse executeCustomScheme(Context context, Uri uri) {
        try {
            File folder = EnvUtil.getInternalFilesDir(context, uri.getAuthority());
            File file = new File(folder, uri.getPath());
            if (!file.exists()) {
                Timber.e("[WEBVIEW] executeCustomScheme: not exist file = %s", file);
                return null;
            }
            WebResourceResponse res = new WebResourceResponse(
                    "image/jpeg",
                    "utf-8",
                    new FileInputStream(file)
            );
            Timber.i("[WEBVIEW] executeCustomScheme: new WebResourceResponse = %s", res);
            return res;
        } catch (Exception e) {
            Timber.e(e, "Exception");
        }
        return null;
    }

    //@Override
    //public void onLoadResource(WebView view, final String url) {
    //    Timber.d("[WEBVIEW] onLoadResource():  url[" + url + "]");
    //    super.onLoadResource(view, url);
    //}

    @Deprecated
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Timber.i("[WEBVIEW] shouldOverrideUrlLoading() API 23 below: %s", url);

        if (url.startsWith("http://") || url.startsWith("https://")) {
            view.loadUrl(url);
            return true;
        }
        return intentProcessing(view, url);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Timber.i("[WEBVIEW] shouldOverrideUrlLoading() API 24 after: %s", request.getUrl());

        String url = Uri.decode(request.getUrl().toString());

        if (url.startsWith("http://") || url.startsWith("https://")) {
            if (request.isRedirect()) {
                view.loadUrl(url);
                return true;
            }
            return false;
        }
        return intentProcessing(view, url);
    }

    private boolean intentProcessing(WebView view, String urlString) {
        String url = Uri.decode(urlString);

        if (url.startsWith("uploadimage:")) {
            return false;
        }

        if (url.startsWith("intent:")) {
            try {
                IntentUtil.intentScheme(view.getContext(), url);
                return true;
            } catch (URISyntaxException e) {
                Timber.e(e);
            } catch (ActivityNotFoundException e) {
                Timber.e(e);
            }
        }

        try {
            IntentUtil.view(view.getContext(), Uri.parse(url));
            return true;
        } catch (ActivityNotFoundException e) {
            Timber.e(e);
        }

        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Timber.i("[WEBVIEW] onPageStarted(): %s", url);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Timber.i("[WEBVIEW] onPageFinished(): %s", url);
        super.onPageFinished(view, url);
    }

    @SuppressLint("WebViewClientOnReceivedSslError")
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        Timber.e("[WEBVIEW] onReceivedSslError(): url[" + view.getUrl() + "],  handler[" + handler + "],  error[" + error + "]");

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
            DialogBuilder.with(view.getContext())
                    .setMessage("ssl certificate invalid.\\nDo you want to proceed?")
                    .setPositiveButton("yes", (dialog, which) -> handler.proceed())
                    .setNegativeButton("no", (dialog, which) -> handler.cancel())
                    .show();
        }

        //super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        String url = view.getUrl();

        StringBuilder buff = new StringBuilder();
        buff.append("\n  encoding[").append(errorResponse.getEncoding()).append("]  ");
        buff.append("\n  mimeType[").append(errorResponse.getMimeType()).append("]  ");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            url = request.getUrl().toString();

            buff.append("\n  method[").append(request.getMethod()).append("]  ");
            buff.append("\n  statusCode[").append(errorResponse.getStatusCode()).append("]  ");
            buff.append("\n  responseHeaders[").append(errorResponse.getResponseHeaders()).append("]  ");
            buff.append("\n  reasonPhrase[").append(errorResponse.getReasonPhrase()).append("]  ");
        }

        Timber.e("onReceivedHttpError : url[" + url + "],  errorResponse[" + buff + "]");
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Deprecated
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Timber.e("[WEBVIEW] onReceivedError(): url[" + view.getUrl() + "],  errorCode[" + errorCode + "],  description[" + description + "],  failingUrl[" + failingUrl + "]");

        boolean forwardErrorPage = ERROR_BAD_URL == errorCode || ERROR_FILE == errorCode;
        onReceivedError(view, errorCode, description, failingUrl, forwardErrorPage);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        Timber.e("[WEBVIEW] onReceivedError(VERSION=M): url[" + view.getUrl() + "],  errorCode[" + error.getErrorCode() + "],  description[" + error.getDescription() + "]");

        int errorCode = error.getErrorCode();
        String description = error.getDescription().toString();
        boolean forwardErrorPage = ERROR_BAD_URL == errorCode || ERROR_FILE == errorCode;
        onReceivedError(view, errorCode, description, request.getUrl().toString(), forwardErrorPage);
    }

    private void onReceivedError(WebView view, int errorCode, String description, String failingUrl, boolean forwardErrorPage) {
        if (WebViewClient.ERROR_UNSUPPORTED_SCHEME == errorCode) {
            if ("about:blank".equals(failingUrl)) {
                return;
            }
        }
        if (WebViewClient.ERROR_TOO_MANY_REQUESTS == errorCode) {
            return;
        }
        if (WebViewClient.ERROR_FAILED_SSL_HANDSHAKE == errorCode) {
            return;
        }
        if (WebViewClient.ERROR_HOST_LOOKUP == errorCode
                || WebViewClient.ERROR_TIMEOUT == errorCode
                || WebViewClient.ERROR_UNKNOWN == errorCode
        ) {
            final String[] except = new String[] {
                    ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tif", ".tiff", ".ico",
                    ".eot", ".ttf", ".otf", ".eot", ".woff", ".woff2",
                    ".pdf",
            };
            for (String str : except) {
                if (failingUrl.endsWith(str)) {
                    return;
                }
            }
        }
        if (forwardErrorPage) {
            String errorPageUrl = "file:///android_asset/docs/error/net_error.html";
            String qs = "errorCode=" + errorCode + "&description=" + description + "&failingUrl=" + failingUrl;
            view.loadUrl(errorPageUrl + "?" + qs);
        }
    }

}
