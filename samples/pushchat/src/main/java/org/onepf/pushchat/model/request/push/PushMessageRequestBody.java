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

package org.onepf.pushchat.model.request.push;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Savin
 * @since 19.04.2015
 */
public final class PushMessageRequestBody {

    @SerializedName("uuids")
    public final List<String> uuids;

    @SerializedName("message")
    @NonNull
    public final String message;

    public PushMessageRequestBody(@NonNull final String uuid,
                                  @NonNull final String message) {
        this.uuids = new ArrayList<String>() {{
            add(uuid);
        }};
        this.message = message;
    }
}
