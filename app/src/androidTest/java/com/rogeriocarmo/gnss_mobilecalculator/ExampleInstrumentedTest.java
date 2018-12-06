package com.rogeriocarmo.gnss_mobilecalculator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule =
            new IntentsTestRule<>(MainActivity.class);


    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.rogeriocarmo.gnss_mobilecalculator", appContext.getPackageName());
    }

    @Test
    public void abrirActivityResultado() {
        onView(withId(R.id.idVisualizar))
                .perform(click());

        // Verifies that the DisplayMessageActivity received an intent
        // with the correct package name and message.
//        Intents.intended(allOf(
//                hasComponent(hasShortClassName(".Resultado")),
//                toPackage(PACKAGE_NAME),
//                hasExtra("Coord", Ecef2LlaConverter.GeodeticLlaValues.class.getName())));
//
//  Intents.intended(hasComponent(MainActivity.class.getName()));
    }
}
