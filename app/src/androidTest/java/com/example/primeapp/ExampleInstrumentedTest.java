package com.example.primeapp;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.primeapp.http_client.SSLHelper;
import com.example.primeapp.login.TestLoginResponseListener;
import com.example.primeapp.login.TestNotifier;
import com.example.primeapp.models.Observation;
import com.google.gson.Gson;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.primeapp", appContext.getPackageName());
    }


    @Test
    public void test_isCorrect() throws InterruptedException {

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals(4, 2 + 2);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final RequestQueue requestQueue = Volley.newRequestQueue(appContext);

        SSLHelper.disableSSLCertificateChecking();
        TestLoginResponseListener listener=new TestLoginResponseListener();
        StringRequest request= LoginActivity.userLoginRequest("https://195.251.192.85:441/token","f.kanelos@PRIME","123456",listener,null);
        requestQueue.add(request);
        countDownLatch.await(5, TimeUnit.SECONDS);
        assertTrue(listener.isResponseOk());



    }

    @Test
    public void post_isCorrect()  {

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals(4, 2 + 2);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final RequestQueue requestQueue = Volley.newRequestQueue(appContext);

        SSLHelper.disableSSLCertificateChecking();
        TestLoginResponseListener listener=new TestLoginResponseListener();
        StringRequest request= LoginActivity.userLoginRequest("https://195.251.192.85:441/token","f.kanelos@PRIME","123456",listener,null);
        requestQueue.add(request);
        try {
            countDownLatch.await(5, TimeUnit.SECONDS);
            assertTrue(listener.isResponseOk());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Observation observation=new Observation("GAIT",1.0,listener.getPatientId());
        Gson gson=new Gson();
        String json =gson.toJson(observation);
        JSONObject mJSONObject = null;
        try {
            mJSONObject = new JSONObject(json);
            TestNotifier notifier=new TestNotifier();
            ResultsFragment.postResults(requestQueue,"https://195.251.192.85:441/api/measurement/post",  mJSONObject,listener,notifier);

            countDownLatch.await(15, TimeUnit.SECONDS);
            assertTrue(notifier.isSuccess());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }


}