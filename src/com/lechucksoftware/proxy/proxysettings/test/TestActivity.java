package com.lechucksoftware.proxy.proxysettings.test;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.analytics.tracking.android.GAServiceManager;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;
import com.lechucksoftware.proxy.proxysettings.exception.ProxyException;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.log.LogWrapper;

import java.util.List;

/**
 * Created by marco on 10/10/13.
 */
public class TestActivity extends Activity
{
    public static final String TAG = "TestActivity";
    public LinearLayout testDBContainer;

    public enum TestAction
    {
        ADD_PROXY,
        ADD_EXAMPLE_PROXIES,
        ADD_TAGS,
        CLEAR_IN_USE,
        TEST_VALIDATION,
        UPDATE_PROXY,
        UPDATE_TAGS,
        LIST_TAGS,
        CLEAR_DB,
        TOGGLE_DEMO_MODE,
        ASSIGN_PROXY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);   // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden
        LogWrapper.d(TAG, "Creating TestActivity");

        setContentView(R.layout.test_layout);

        testDBContainer = (LinearLayout) findViewById(R.id.testDBContainer);
    }

    public void addDBClicked(View caller)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.ADD_PROXY);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void addExampleProxyClicked(View caller)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.ADD_EXAMPLE_PROXIES);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void toggleDemoModeClicked(View caller)
    {
        AsyncTest toggleDemoMode = new AsyncTest(this, TestAction.TOGGLE_DEMO_MODE);
        toggleDemoMode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void addTagsDBClicked(View caller)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.ADD_TAGS);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void assignProxy(View view)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.ASSIGN_PROXY);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void testProxyValidations(View view)
    {
        AsyncTest testValidation = new AsyncTest(this, TestAction.TEST_VALIDATION);
        testValidation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void clearProxyInUse(View view)
    {
        AsyncTest clearInUseAsync = new AsyncTest(this, TestAction.CLEAR_IN_USE);
        clearInUseAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void testBugReporting(View caller)
    {
        EventReportingUtils.sendException(new Exception("EXCEPTION ONLY FOR TEST"));
        EventReportingUtils.sendException(new ProxyException(ApplicationGlobals.getProxyManager().getSortedConfigurationsList()));
        EventReportingUtils.sendEvent("EVENT ONLY FOR TEST");

        GAServiceManager.getInstance().dispatchLocalHits();
    }

    public void listDBProxies(View caller)
    {
        TextView textViewTest = new TextView(this);
        testDBContainer.addView(textViewTest);
        List<ProxyEntity> list = ApplicationGlobals.getCacheManager().getAllProxiesList();
        for (ProxyEntity p : list)
        {
            textViewTest.append(p.toString() + "\n\n");
        }
    }

    public void listDBTags(View caller)
    {
        TextView textViewTest = new TextView(this);
        testDBContainer.addView(textViewTest);
        List<TagEntity> list = ApplicationGlobals.getDBManager().getAllTags();
        for (TagEntity t : list)
        {
            textViewTest.append(t.toString() + "\n\n");
        }
    }

    public void clearOutput(View caller)
    {
        testDBContainer.removeAllViews();
    }

    public void updateDBClicked(View caller)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.UPDATE_PROXY);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void clearDBClicked(View caller)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.CLEAR_DB);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class AsyncTest extends AsyncTask<Void, String, Void>
    {
        TestActivity _testActivity;
        TextView textViewTest;
        TestAction _action;

        public AsyncTest(TestActivity testActivity, TestAction action)
        {
            _testActivity = testActivity;
            _action = action;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            _testActivity.testDBContainer.removeView(textViewTest);
        }

        @Override
        protected void onPreExecute()
        {
            textViewTest = new TextView(_testActivity);
            textViewTest.setText("Started AsyncTestAction: " + _action);
            _testActivity.testDBContainer.addView(textViewTest);
        }

        @Override
        protected void onProgressUpdate(String... progress)
        {
            textViewTest.setText(_action + " " + progress[0]);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            if (_action == TestAction.CLEAR_DB)
            {
                ApplicationGlobals.getDBManager().resetDB();
            }
            else if (_action == TestAction.CLEAR_IN_USE)
            {
                TestUtils.clearInUse();
            }
            else if (_action == TestAction.ADD_EXAMPLE_PROXIES)
            {
                TestUtils.addProxyExamples(_testActivity);
            }
            else if (_action == TestAction.TOGGLE_DEMO_MODE)
            {
                // TODO: improve handling of preference cache
                Utils.checkDemoMode(_testActivity);
                Utils.setDemoMode(_testActivity, !ApplicationGlobals.getInstance().demoMode);
                Utils.checkDemoMode(_testActivity);

                for (ProxyConfiguration conf : ApplicationGlobals.getProxyManager().getSortedConfigurationsList())
                {
                    if (ApplicationGlobals.getInstance().demoMode)
                        conf.setAPDescription(UIUtils.getRandomCodeName().toString());
                    else
                        conf.setAPDescription(null);
                }
            }
            else if (_action == TestAction.TEST_VALIDATION)
            {
                TestUtils.testValidation();
            }
            else if(_action == TestAction.ASSIGN_PROXY)
            {
                try
                {
                    ProxyConfiguration conf = ApplicationGlobals.getProxyManager().getCurrentConfiguration();
                    List<ProxyEntity> proxies = ApplicationGlobals.getCacheManager().getAllProxiesList();
                    for (ProxyEntity p : proxies)
                    {
                        TestUtils.assignProxies(conf, p);
                        publishProgress(p.toString());
                    }
                }
                catch (Exception e)
                {
                    publishProgress(e.toString());
                }
            }
            else
            {
                for (int i = 0; i < 10; i++)
                {
                    switch (_action)
                    {
                        case ADD_PROXY:
                            TestUtils.addProxy();
                            break;
                        case UPDATE_PROXY:
                            TestUtils.updateProxy();
                            break;
                        case ADD_TAGS:
                            TestUtils.addTags();
                            break;
                        case UPDATE_TAGS:
//                            TestUtils.addProxy();
                            break;
                    }

                    publishProgress(String.valueOf(i));
                }
            }


            return null;
        }

    }
}
