/*
 * Copyright 2012-2015 One Platform Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onepf.opfpush.unity.listener;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.onepf.opfpush.listener.EventListener;
import org.onepf.opfpush.model.UnrecoverablePushError;
import org.onepf.opfpush.unity.R;
import org.onepf.opfpush.unity.model.MessageEvent;
import org.onepf.opfpush.unity.model.NoAvailableProviderEvent;
import org.onepf.opfpush.unity.model.RegisteredEvent;
import org.onepf.opfpush.unity.model.UnregisteredEvent;
import org.onepf.opfpush.unity.util.NotificationUtils;
import org.onepf.opfutils.OPFLog;
import org.onepf.opfutils.OPFUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static org.onepf.opfpush.unity.util.Constants.MESSAGE_EXTRA_KEY;
import static org.onepf.opfpush.unity.util.Constants.PAYLOAD_EXTRA_KEY;

/**
 * @author Roman Savin
 * @since 09.12.14
 */
public class UnityEventListener implements EventListener {

    @NonNull
    private Context appContext;

    public UnityEventListener(@NonNull final Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public void onMessage(@NonNull final Context context,
                          @NonNull final String providerName,
                          @Nullable final Bundle extras) {
        OPFLog.logMethod(providerName, OPFUtils.toString(extras));
        if (extras == null) {
            return;
        }

        String message = null;
        if (extras.containsKey(MESSAGE_EXTRA_KEY)) {
            message = extras.getString(MESSAGE_EXTRA_KEY);
        } else if (extras.containsKey(PAYLOAD_EXTRA_KEY)) {
            message = extras.getString(PAYLOAD_EXTRA_KEY);
        }

        if (message != null) {
            try {
                NotificationUtils.showNotification(
                        appContext,
                        appContext.getString(R.string.message_notification_title),
                        message
                );
                EventBus.getDefault().postSticky(new MessageEvent(URLDecoder.decode(message, "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                OPFLog.e(e.getCause().toString());
            }
        }
    }

    @Override
    public void onDeletedMessages(@NonNull final Context context,
                                  @NonNull final String providerName,
                                  final int messagesCount) {
        OPFLog.logMethod(providerName, messagesCount);
    }

    @Override
    public void onRegistered(@NonNull final Context context,
                             @NonNull final String providerName,
                             @NonNull final String registrationId) {
        OPFLog.logMethod(providerName, registrationId);
        EventBus.getDefault().postSticky(new RegisteredEvent(registrationId));
    }

    @Override
    public void onUnregistered(@NonNull final Context context,
                               @NonNull final String providerName,
                               @Nullable final String registrationId) {
        OPFLog.logMethod(providerName, registrationId);
        EventBus.getDefault().postSticky(new UnregisteredEvent(registrationId));
    }

    @Override
    public void onNoAvailableProvider(@NonNull final Context context,
                                      @NonNull final Map<String, UnrecoverablePushError> pushErrors) {
        OPFLog.logMethod(context, pushErrors);
        EventBus.getDefault().postSticky(new NoAvailableProviderEvent(pushErrors));
    }
}
