package com.example.sotdprototype;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

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
        assertEquals("com.example.sotdprototype", appContext.getPackageName());
    }

    @Test
    public void testMainActivityState() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        assertEquals(Lifecycle.State.RESUMED, scenario.getState());
        scenario.close();
    }
}