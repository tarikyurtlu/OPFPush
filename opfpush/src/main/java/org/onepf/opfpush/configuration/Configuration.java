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

package org.onepf.opfpush.configuration;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.onepf.opfpush.PushProvider;
import org.onepf.opfpush.listener.EventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Instance of this class is used as an argument of {@link org.onepf.opfpush.OPFPush#init(android.content.Context, Configuration)}
 * method for configuring {@link org.onepf.opfpush.OPFPushHelper} instance.
 *
 * @author Kirill Rozov
 * @author Roman Savin
 * @since 04.09.2014
 */
public final class Configuration {

    @NonNull
    private final List<PushProvider> providers;

    @Nullable
    private final EventListener eventListener;

    private final boolean isSelectSystemPreferred;

    private Configuration(@NonNull final Collection<? extends PushProvider> providers,
                          @Nullable final EventListener eventListener,
                          final boolean selectSystemPreferred) {
        this.providers = Collections.unmodifiableList(new ArrayList<>(providers));
        this.eventListener = eventListener;
        this.isSelectSystemPreferred = selectSystemPreferred;
    }

    /**
     * Returns all available push providers.
     *
     * @return All available push providers.
     */
    @NonNull
    public List<PushProvider> getProviders() {
        return providers;
    }

    /**
     * Returns the instance of {@link org.onepf.opfpush.listener.EventListener}.
     *
     * @return The instance of {@link org.onepf.opfpush.listener.EventListener}.
     */
    @Nullable
    public EventListener getEventListener() {
        return eventListener;
    }

    /**
     * Returns {@code true} if the system push provider is preferred, false otherwise.
     *
     * @return {@code true} if the system push provider is preferred, false otherwise.
     */
    public boolean isSelectSystemPreferred() {
        return isSelectSystemPreferred;
    }

    @Override
    public String toString() {
        return "Configuration {"
                + "providers = " + providers
                + ", isSelectSystemPreferred = " + isSelectSystemPreferred
                + '}';
    }

    /**
     * Builder class that creates an instance of {@code Configuration}.
     */
    public static final class Builder {

        @Nullable
        private Map<String, PushProvider> providersMap;

        @Nullable
        private EventListener eventListener;

        private boolean isSelectSystemPreferred = false;

        /**
         * See {@link #addProviders(java.util.List)}
         *
         * @return The current {@code Builder} instance.
         * @throws java.lang.IllegalArgumentException If a provider was already added
         */
        @NonNull
        public Builder addProviders(@NonNull final PushProvider... providers) {
            if (providers.length == 0) {
                return this;
            } else {
                return addProviders(Arrays.asList(providers));
            }
        }

        /**
         * Add push providers to the configuration. The priority of providers corresponds to the order
         * in which they were added.
         *
         * @return The current {@code Builder} instance.
         * @throws java.lang.IllegalArgumentException If a provider was already added.
         */
        @NonNull
        public Builder addProviders(@NonNull final List<? extends PushProvider> providers) {
            if (providers.isEmpty()) {
                return this;
            }

            for (PushProvider provider : providers) {
                if (provider.isAvailable()) {
                    provider.checkManifest();
                }
            }

            if (this.providersMap == null) {
                this.providersMap = new LinkedHashMap<>();
            }

            for (PushProvider provider : providers) {
                final String providerName = provider.getName();
                if (this.providersMap.containsKey(providerName)) {
                    throw new IllegalArgumentException(
                            String.format("Provider '%s' already added.", provider)
                    );
                } else {
                    this.providersMap.put(providerName, provider);
                }
            }
            return this;
        }

        @NonNull
        public Builder setEventListener(@NonNull final EventListener eventListener) {
            this.eventListener = eventListener;
            return this;
        }

        /**
         * If you set {@code true}, the system push provider will get the highest priority.
         * For Google device this is Google Cloud Messaging, for Kindle device - ADM.
         * False by default.
         *
         * @param isSelectSystemPreferred Does select system preferred store.
         * @return The current {@code Builder}.
         */
        public Builder setSelectSystemPreferred(final boolean isSelectSystemPreferred) {
            this.isSelectSystemPreferred = isSelectSystemPreferred;
            return this;
        }

        /**
         * Create the instance of {@link Configuration}.
         *
         * @return New {@link Configuration} object.
         * @throws java.lang.IllegalArgumentException If there aren't any added providers.
         */
        @NonNull
        public Configuration build() {
            if (providersMap == null) {
                throw new IllegalArgumentException("Need to add at least one push provider.");
            }

            return new Configuration(
                    providersMap.values(),
                    eventListener,
                    isSelectSystemPreferred
            );
        }

        @Override
        public String toString() {
            return "Builder{"
                    + "providersMap="
                    + providersMap
                    + ", systemPushPreferred="
                    + isSelectSystemPreferred
                    + '}';
        }
    }
}
