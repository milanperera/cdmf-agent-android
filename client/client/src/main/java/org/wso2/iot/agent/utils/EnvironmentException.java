package org.wso2.iot.agent.utils;

import android.support.annotation.NonNull;

/**
 * Created by romans1 on 09/04/2017.
 * <p>
 * Exception class for more readable and understandable handling environment-related exceptions.
 */
public class EnvironmentException extends IllegalStateException {

    enum EnvironmentExceptionCause {
        BUILD_TYPE("Os build type exception. Build type unexpected or can not be determined. " +
                "Applications will work unstable! Check build configs and reflash the device."),
        ENVIRONMENT("Environment detection exception. Environment is unexpected or can not be determined. " +
                "Applications will work unstable! Check environment configuration file access, internal memory alive, and perform device factory reset.");

        @NonNull
        private final String mMessage;

        EnvironmentExceptionCause(@NonNull String message) {
            this.mMessage = message;
        }

        @NonNull
        public String getMessage() {
            return mMessage;
        }

    }

    EnvironmentException(@NonNull EnvironmentExceptionCause cause) {
        super(cause.getMessage());
    }
}
