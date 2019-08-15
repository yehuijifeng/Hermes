/**
 *
 * Copyright 2016 Xiaofei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.library.hermes.util;

/**
 * Created by Xiaofei on 16/4/15.
 */
public class ErrorCodes {

    //TODO change the right value into integers before release!

    public static final int SUCCESS = 0;

    public static final int REMOTE_EXCEPTION = SUCCESS + 1;

    public static final int SERVICE_UNAVAILABLE = REMOTE_EXCEPTION + 1;

    public static final int NULL_POINTER_EXCEPTION = SERVICE_UNAVAILABLE + 1;

    public static final int ILLEGAL_PARAMETER_EXCEPTION = NULL_POINTER_EXCEPTION + 1;

    public static final int ACCESS_DENIED = ILLEGAL_PARAMETER_EXCEPTION + 1;

    public static final int GSON_ENCODE_EXCEPTION = ACCESS_DENIED + 1;

    public static final int GSON_DECODE_EXCEPTION = GSON_ENCODE_EXCEPTION + 1;

    public static final int TOO_MANY_MATCHING_METHODS = GSON_DECODE_EXCEPTION + 1;

    public static final int METHOD_PARAMETER_NOT_MATCHING = TOO_MANY_MATCHING_METHODS + 1;

    public static final int METHOD_RETURN_TYPE_NOT_MATCHING = METHOD_PARAMETER_NOT_MATCHING + 1;

    public static final int TOO_MANY_MATCHING_METHODS_FOR_GETTING_INSTANCE = METHOD_RETURN_TYPE_NOT_MATCHING + 1;

    public static final int GETTING_INSTANCE_RETURN_TYPE_ERROR = TOO_MANY_MATCHING_METHODS_FOR_GETTING_INSTANCE + 1;

    public static final int GETTING_INSTANCE_METHOD_NOT_FOUND = GETTING_INSTANCE_RETURN_TYPE_ERROR + 1;

    public static final int TOO_MANY_MATCHING_CONSTRUCTORS_FOR_CREATING_INSTANCE = GETTING_INSTANCE_METHOD_NOT_FOUND + 1;

    public static final int CONSTRUCTOR_NOT_FOUND = TOO_MANY_MATCHING_CONSTRUCTORS_FOR_CREATING_INSTANCE + 1;

    public static final int CLASS_NOT_FOUND = CONSTRUCTOR_NOT_FOUND + 1;

    public static final int METHOD_NOT_FOUND = CLASS_NOT_FOUND + 1;

    public static final int METHOD_INVOCATION_EXCEPTION = METHOD_NOT_FOUND + 1;

    public static final int CLASS_WITH_PROCESS = METHOD_INVOCATION_EXCEPTION + 1;

    public static final int METHOD_WITH_PROCESS = CLASS_WITH_PROCESS + 1;

    public static final int METHOD_GET_INSTANCE_NOT_STATIC = METHOD_WITH_PROCESS + 1;

    public static final int CALLBACK_NOT_ALIVE = METHOD_GET_INSTANCE_NOT_STATIC + 1;

    private ErrorCodes() {

    }
}
