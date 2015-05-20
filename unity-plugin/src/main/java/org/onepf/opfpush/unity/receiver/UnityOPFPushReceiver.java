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

package org.onepf.opfpush.unity.receiver;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.unity3d.player.UnityPlayer;
import org.onepf.opfpush.model.UnrecoverablePushError;
import org.onepf.opfpush.receiver.OPFPushReceiver;
import org.onepf.opfutils.OPFLog;
import org.onepf.opfutils.OPFUtils;

import java.util.Map;

/**
 * @author Roman Savin
 * @since 25.12.14
 */
public class UnityOPFPushReceiver extends OPFPushReceiver {

    private static final String EVENT_RECEIVER = "OPFPush";
    private static final String MESSAGE_CALLBACK = "OnMessage";
    private static final String DELETED_MESSAGES_CALLBACK = "OnDeletedMessages";
    private static final String REGISTERED_CALLBACK = "OnRegistered";
    private static final String UNREGISTERED_CALLBACK = "OnUnregistered";
    private static final String NO_AVAILABLE_PROVIDER_CALLBACK = "OnNoAvailableProvider";

    @Override
    public void onMessage(@NonNull final Context context,
                          @NonNull final String providerName,
                          @Nullable final Bundle extras) {
        OPFLog.logMethod(context, providerName, OPFUtils.toString(extras));
        //TODO: send real message and providerName
        UnityPlayer.UnitySendMessage(EVENT_RECEIVER, MESSAGE_CALLBACK, "message");
    }

    @Override
    public void onDeletedMessages(@NonNull final Context context,
                                  @NonNull final String providerName,
                                  final int messagesCount) {
        OPFLog.logMethod(providerName, messagesCount);
        //TODO: send providerName
        UnityPlayer.UnitySendMessage(EVENT_RECEIVER, DELETED_MESSAGES_CALLBACK, String.valueOf(messagesCount));
    }

    @Override
    public void onRegistered(@NonNull final Context context,
                             @NonNull final String providerName,
                             @NonNull final String registrationId) {
        OPFLog.logMethod(providerName, registrationId);
        //TODO: send providerName
        UnityPlayer.UnitySendMessage(EVENT_RECEIVER, REGISTERED_CALLBACK, registrationId);
    }

    @Override
    public void onUnregistered(@NonNull final Context context,
                               @NonNull final String providerName,
                               @Nullable final String oldRegistrationId) {
        OPFLog.logMethod(providerName, oldRegistrationId);
        //TODO: send provider name
        UnityPlayer.UnitySendMessage(EVENT_RECEIVER, UNREGISTERED_CALLBACK, oldRegistrationId);
    }

    @Override
    public void onNoAvailableProvider(@NonNull final Context context,
                                      @NonNull final Map<String, UnrecoverablePushError> pushErrors) {
        OPFLog.logMethod(context, pushErrors);
        //TODO: send pushErrors
        UnityPlayer.UnitySendMessage(EVENT_RECEIVER, NO_AVAILABLE_PROVIDER_CALLBACK, "errors");
    }
}
