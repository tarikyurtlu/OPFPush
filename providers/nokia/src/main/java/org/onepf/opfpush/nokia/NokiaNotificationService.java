/*
 * Copyright 2012-2014 One Platform Foundation
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

package org.onepf.opfpush.nokia;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.nokia.push.PushBaseIntentService;
import com.nokia.push.PushConstants;

import org.onepf.opfpush.Error;
import org.onepf.opfpush.OPFPushException;
import org.onepf.opfpush.OPFPushHelper;
import org.onepf.opfpush.Result;

/**
 * Service for handling communication from Nokia Push Notifications service.
 *
 * @author Kirill Rozov
 * @since 05.09.2014
 */
public class NokiaNotificationService extends PushBaseIntentService {

    public NokiaNotificationService() {
        super("Nokia Push Client"); //Passed name will use as Thread name;
    }

    /**
     * Called when a Push Notification message has been received.
     *
     * @param appContext Application's context.
     * @param intent     Intent containing the message payload as extras.
     */
    @Override
    protected void onMessage(@NonNull Context appContext, Intent intent) {
        OPFPushHelper.getInstance(this).getProviderCallback().onMessage(NokiaNotificationProvider.NAME, intent.getExtras());
    }

    /**
     * Called on registration or unregistration error.
     *
     * @param appContext Application's context.
     * @param errorId    Error id returned by the Push Notifications service.
     */
    @Override
    protected void onError(@NonNull Context appContext, String errorId) {
        OPFPushHelper.getInstance(this).getProviderCallback()
                .onResult(
                        Result.error(NokiaNotificationProvider.NAME,
                                convertError(errorId),
                                Result.Type.UNKNOWN)
                );
    }

    /**
     * Called when the Push Notifications server tells pending messages
     * have been deleted because the device was idle.
     *
     * @param appContext Application's context.
     * @param total      Total number of collapsed messages.
     */
    @Override
    protected void onDeletedMessages(@NonNull Context appContext, int total) {
        OPFPushHelper.getInstance(this).getProviderCallback()
                .onDeletedMessages(NokiaNotificationProvider.NAME, total);
    }

    /**
     * Called on a registration error that could be retried.
     * By default, it does nothing and returns true,
     * but could be overridden to change that behavior and/or display the error.
     *
     * @param appContext Application's context.
     * @param errorId    Error id returned by the Push Notifications service.
     * @return If true, failed operation will be retried (using exponential backoff).
     */
    @Override
    protected boolean onRecoverableError(@NonNull Context appContext,
                                         @NonNull @NokiaNotificationError String errorId) {
        Error error = convertError(errorId);
        OPFPushHelper.getInstance(this).getProviderCallback()
                .onResult(
                        Result.error(NokiaNotificationProvider.NAME,
                                error,
                                Result.Type.REGISTRATION)
                );
        return false;
    }

    @NonNull
    private static Error convertError(@NonNull @NokiaNotificationError String errorId) {
        if (PushConstants.ERROR_INVALID_PARAMETERS.equals(errorId)) {
            return Error.INVALID_PARAMETERS;
        } else if (PushConstants.ERROR_SERVICE_NOT_AVAILABLE.equals(errorId)) {
            return Error.INVALID_PARAMETERS;
        } else if (PushConstants.ERROR_SERVICE_NOT_AVAILABLE.equals(errorId)) {
            return Error.SERVICE_NOT_AVAILABLE;
        } else {
            throw new OPFPushException(String.format("Unknown error '%s'.", errorId));
        }
    }

    /**
     * Called after a device has been registered.
     *
     * @param appContext        Application's context.
     * @param registrationToken The registration id returned by the Push Notifications service.
     */
    @Override
    protected void onRegistered(@NonNull Context appContext,
                                @NonNull String registrationToken) {
        OPFPushHelper.getInstance(this).getProviderCallback().onResult(
                Result.success(NokiaNotificationProvider.NAME, registrationToken, Result.Type.REGISTRATION));
    }

    /**
     * Called after a device has been unregistered.
     *
     * @param appContext           Application's context.
     * @param oldRegistrationToken the registration id that was previously registered.
     */
    @Override
    protected void onUnregistered(@NonNull Context appContext,
                                  @NonNull String oldRegistrationToken) {
        OPFPushHelper.getInstance(this).getProviderCallback().onResult(
                Result.success(NokiaNotificationProvider.NAME, oldRegistrationToken, Result.Type.UNREGISTRATION));
    }
}