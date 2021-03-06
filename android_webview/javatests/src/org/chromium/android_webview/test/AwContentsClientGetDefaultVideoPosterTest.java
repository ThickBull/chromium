// Copyright 2013 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.android_webview.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import org.chromium.android_webview.DefaultVideoPosterRequestHandler;
import org.chromium.android_webview.InterceptedRequestData;
import org.chromium.base.test.util.Feature;
import org.chromium.content.browser.test.util.CallbackHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

/**
 * Tests for AwContentClient.GetDefaultVideoPoster.
 */
public class AwContentsClientGetDefaultVideoPosterTest extends AwTestBase {
    private static final String TAG = "AwContentsClientGetDefaultVideoPosterTest";

    private static class DefaultVideoPosterClient extends TestAwContentsClient {
        private CallbackHelper mVideoPosterCallbackHelper = new CallbackHelper();
        private Bitmap mPoster;
        private Context mContext;

        public DefaultVideoPosterClient(Context context) {
            mContext = context;
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            mVideoPosterCallbackHelper.notifyCalled();
            return getPoster();
        }

        public void waitForGetDefaultVideoPosterCalled() throws InterruptedException,
                TimeoutException {
            mVideoPosterCallbackHelper.waitForCallback(0);
        }

        public Bitmap getPoster() {
            if (mPoster == null) {
                try {
                    mPoster = BitmapFactory.decodeStream(
                            mContext.getAssets().open("asset_icon.png"));
                } catch (IOException e) {
                    Log.e(TAG, null, e);
                }
            }
            return mPoster;
        }
    }

    @Feature({"AndroidWebView"})
    @SmallTest
    public void testGetDefaultVideoPoster() throws Throwable {
        DefaultVideoPosterClient contentsClient =
                new DefaultVideoPosterClient(getInstrumentation().getContext());
        AwTestContainerView testContainerView =
                createAwTestContainerViewOnMainSync(contentsClient);
        String data = "<html><head><body><video id='video' control src='' /> </body></html>";
        loadDataAsync(testContainerView.getAwContents(), data, "text/html", false);
        contentsClient.waitForGetDefaultVideoPosterCalled();
    }

    @Feature({"AndroidWebView"})
    @SmallTest
    public void testInterceptDefaultVidoePosterURL() throws Throwable {
        DefaultVideoPosterClient contentsClient =
                new DefaultVideoPosterClient(getInstrumentation().getTargetContext());
        DefaultVideoPosterRequestHandler handler =
                new DefaultVideoPosterRequestHandler(contentsClient);
        InterceptedRequestData requestData =
                handler.shouldInterceptRequest(handler.getDefaultVideoPosterURL());
        assertTrue(requestData.getMimeType().equals("image/png"));
        Bitmap bitmap = BitmapFactory.decodeStream(requestData.getData());
        Bitmap poster = contentsClient.getPoster();
        assertEquals("poster.getHeight() not equal to bitmap.getHeight()",
                poster.getHeight(), bitmap.getHeight());
        assertEquals("poster.getWidth() not equal to bitmap.getWidth()",
                poster.getWidth(), bitmap.getWidth());
    }

    @Feature({"AndroidWebView"})
    @SmallTest
    public void testNoDefaultVideoPoster() throws Throwable {
        NullContentsClient contentsClient = new NullContentsClient();
        DefaultVideoPosterRequestHandler handler =
                new DefaultVideoPosterRequestHandler(contentsClient);
        InterceptedRequestData requestData =
                handler.shouldInterceptRequest(handler.getDefaultVideoPosterURL());
        assertTrue(requestData.getMimeType().equals("image/png"));
        InputStream in = requestData.getData();
        assertEquals("Should get -1", in.read(), -1);
    }
}
