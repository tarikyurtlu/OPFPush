package org.onepf.opfpush;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onepf.opfpush.mock.MockNamePushProvider;
import org.onepf.opfpush.model.State;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.Random;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;

/**
 * Created by antonpp on 24.02.15.
 */
@Config(emulateSdk = JELLY_BEAN_MR2, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
@SuppressWarnings({"checkstyle:methodname", "PMD.MethodNamingConventions"})
public class SettingsTest extends Assert {

    private static final String LOG_TAG = SettingsTest.class.getSimpleName();

    private static final String KEY_LAST_PROVIDER_NAME = "last_provider_name";
    private static final String KEY_STATE = "state";
    private static final String KEY_LAST_ANDROID_ID = "android_id";
    private static final int NUM_TESTS = 100;
    private static final int NUM_PROVIDERS = 100;
    private static final int RANDOM_STRING_LENGTH = 16;

    private static final Random RND = new Random();

    private MockNamePushProvider[] pushProviders;
    private SharedPreferences sharedPreferences;
    private Settings settings;

    @Before
    public void setup() {
        Context ctx = Robolectric.application.getApplicationContext();
        sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_MULTI_PROCESS);
        settings = Settings.getInstance(ctx);
        pushProviders = new MockNamePushProvider[NUM_PROVIDERS];
        for (int i = 0; i < NUM_PROVIDERS; ++i) {
            pushProviders[i] = new MockNamePushProvider(String.format("provider%d", i + 1));
        }
    }

    @After
    public void eraseSettingsInstance() {
        Field instanceField;
        synchronized (Settings.class) {
            try {
                instanceField = Settings.class.getDeclaredField("instance");
                instanceField.setAccessible(true);
                instanceField.set(null, null);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    }

    @Test
    public void getState_commonSituation() {
        // first is always UNREGISTERED
        assertEquals(State.UNREGISTERED, settings.getState());

        // check all states get correctly
        for (State state : State.values()) {
            sharedPreferences.edit().putInt(KEY_STATE, state.getValue()).apply();
            assertEquals(state, settings.getState());
        }
    }

    @Test
    public void getState_nullIsTreatedAsUnregistered() {
        sharedPreferences.edit().putInt(KEY_STATE, -1).apply();
        assertEquals(State.UNREGISTERED, settings.getState());
    }

    @Test
    public void saveState() {
        // check all states set correctly
        State actualState;
        State expectedState;
        final int numStates = State.values().length;
        for (int i = 0; i < NUM_TESTS; ++i) {
            expectedState = State.values()[RND.nextInt(numStates)];
            settings.saveState(expectedState);
            assertTrue(sharedPreferences.contains(KEY_STATE));
            actualState = State.fromValue(sharedPreferences.getInt(KEY_STATE, numStates));
            assertEquals(expectedState, actualState);
        }
    }

    @Test
    public void saveState_and_getState() {
        // check saveState/getState consistency
        for (State state : State.values()) {
            settings.saveState(state);
            assertEquals(settings.getState(), state);
        }
    }

    @Test
    public void saveLastProviderName() {
        PushProvider expected;
        for (int i = 0; i < NUM_TESTS; ++i) {
            expected = pushProviders[RND.nextInt(NUM_PROVIDERS)];
            settings.saveLastProvider(expected);
            assertEquals(expected.getName(), sharedPreferences.getString(KEY_LAST_PROVIDER_NAME, null));
        }
    }

    @Test
    public void getLastProviderName() {
        String expected;
        for (int i = 0; i < NUM_TESTS; ++i) {
            expected = pushProviders[RND.nextInt(NUM_PROVIDERS)].getName();
            sharedPreferences.edit().putString(KEY_LAST_PROVIDER_NAME, expected).apply();
            assertEquals(expected, settings.getLastProviderName());
        }
    }

    @Test
    public void saveLastProviderName_and_getLastProviderName() {
        PushProvider expected;
        for (int i = 0; i < NUM_TESTS; ++i) {
            expected = pushProviders[RND.nextInt(NUM_PROVIDERS)];
            settings.saveLastProvider(expected);
            assertEquals(expected.getName(), settings.getLastProviderName());
        }
    }

    private String[] getRandomStrings(int n, int len) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        String[] strings = new String[n];
        for (int i = 0; i < n; ++i) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; j++) {
                char c = chars[RND.nextInt(chars.length)];
                sb.append(c);
            }
            strings[i] = sb.toString();
        }
        return strings;
    }

    @Test
    public void saveLastAndroidId_commonSituation() {
        String[] randomStrings = getRandomStrings(NUM_TESTS, RANDOM_STRING_LENGTH);
        for (int i = 0; i < NUM_TESTS; ++i) {
            settings.saveLastAndroidId(randomStrings[i]);
            assertEquals(randomStrings[i], sharedPreferences.getString(KEY_LAST_ANDROID_ID, null));
        }
    }

    @Test
    public void saveLastAndroidId_passingNullClearsPreferences() {
        // test null case
        sharedPreferences.edit().putString(KEY_LAST_ANDROID_ID, "notNull").apply();
        settings.saveLastAndroidId(null);
        assertFalse(sharedPreferences.contains(KEY_LAST_ANDROID_ID));
    }

    @Test
    public void getLastAndroidId() {
        String[] randomStrings = getRandomStrings(NUM_TESTS, RANDOM_STRING_LENGTH);
        for (int i = 0; i < NUM_TESTS; ++i) {
            sharedPreferences.edit().putString(KEY_LAST_ANDROID_ID, randomStrings[i]).apply();
            assertEquals(randomStrings[i], settings.getLastAndroidId());
        }
    }

    @Test
    public void getLastAndroidId_and_saveLastAndroidId() {
        String[] randomStrings = getRandomStrings(NUM_TESTS, RANDOM_STRING_LENGTH);
        for (int i = 0; i < NUM_TESTS; ++i) {
            settings.saveLastAndroidId(randomStrings[i]);
            assertEquals(randomStrings[i], settings.getLastAndroidId());
        }
    }
}