/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javayh.advanced.redis;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-17 4:26 PM
 */
public abstract class RedisKey {
    static final String UID = "javayh:";
    static final String LINK = ":";

    static String followers(String uid) {
        return UID + uid + ":followers";
    }

    public static String key(String name) {
        return UID + name;
    }

    static String auth(String uid) {
        return UID + uid + ":auth";
    }

    public static String route(String name) {
        return UID + name + ":route";
    }

    public static String user(String name) {
        return UID + name + ":user";
    }

    public static String login(String name) {
        return UID + name + ":login";
    }

    public static String other(String name) {
        return UID + name + ":other";
    }
}
