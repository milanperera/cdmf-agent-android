package org.wso2.iot.agent.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;

import com.verifone.utilities.Log;

import org.wso2.iot.agent.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;


/**
 * Created by romans1 on 06/28/2017.
 */

public final class VResources {

    private static final String TAG = VResources.class.getSimpleName();

    public enum BuildType {
        UNDEFINED("undefined"),
        ENGINEERING("engineering"),
        PRODUCTION("prod"),
        SBOX("sbox");

        private String buildTypeAlias;

        BuildType(String buildTypeAlias) {
            this.buildTypeAlias = buildTypeAlias;
        }

        public String getAlias() {
            return buildTypeAlias;
        }

        public static BuildType getByAlias(@Nullable String osEnvironmentString) {
            for (BuildType buildType : values()) {
                if (Objects.equals(buildType.getAlias(), osEnvironmentString)) return buildType;
            }
            return UNDEFINED;
        }
    }


    @Nullable
    private static VResources sInstance;

    @NonNull
    public static VResources getInstance(@NonNull Context context) {
        Context applicationContext = context.getApplicationContext();
        if (sInstance == null || !sInstance.isValid(applicationContext)) {
            sInstance = new VResources(applicationContext);
        }
        return sInstance;
    }

    @NonNull
    private final Resources mResources;
    @Nullable
    private String mCurrentEnvironment;

    private VResources(Context applicationContext) {
        mResources = applicationContext.getResources();
        try {
            mCurrentEnvironment = getEnvironment();
        } catch (EnvironmentException e) {
            Log.d(TAG, "Environment not configured yet.");
        }

        Log.d(TAG, "Created new instance for package: " + applicationContext.getPackageName() + "; current environment is: " + mCurrentEnvironment);
    }

    /**
     * Check resources is the same object to avoid memory leaks.
     */
    private boolean isValid(Context applicationContext) {
        return Objects.equals(mResources, applicationContext.getResources());
    }

    /**
     * Obtaining environment-related resource id
     *
     * @param id stub/default resource id
     * @return environment-dependent resource id
     * @throws Resources.NotFoundException if environment-dependent resource id and stub/default id not found
     */
    private int obtainEnvironmentId(int id) throws Resources.NotFoundException {
        try {
            String name = mResources.getResourceName(id) + "_" + mCurrentEnvironment;
            return mResources.getIdentifier(name, null, null);
        } catch (Resources.NotFoundException e) {
            //will be using default value if exists
            return id;
        }
    }

    /**
     * @param id stub/default resource id
     * @return environment-dependent resource string
     * @throws Resources.NotFoundException if environment-dependent resource id and stub/default id not found
     */
    @NonNull
    public String getString(@StringRes final int id) throws Resources.NotFoundException {
        try {
            return getStringImpl(obtainEnvironmentId(id));
        } catch (Resources.NotFoundException e) {
            return getStringImpl(id);
        }
    }

    @NonNull
    private String getStringImpl(@StringRes int environmentId) throws Resources.NotFoundException {
        return mResources.getString(environmentId);
    }

    /**
     * @param id stub/default raw file id
     * @return environment-dependent raw file input stream
     * @throws Resources.NotFoundException if environment-dependent raw file id and stub/default id not found
     */
    @NonNull
    public InputStream openRawResource(@RawRes final int id) throws Resources.NotFoundException {
        try {
            return openRawResourceImpl(obtainEnvironmentId(id));
        } catch (Resources.NotFoundException e) {
            return openRawResourceImpl(id);
        }
    }

    @NonNull
    private InputStream openRawResourceImpl(int environmentId) throws Resources.NotFoundException {
        return mResources.openRawResource(environmentId);
    }

    /**
     * @param id stub/default boolean resource id
     * @return environment-dependent resource boolean
     * @throws Resources.NotFoundException if environment-dependent resource id and stub/default id not found
     */
    public boolean getBoolean(@BoolRes final int id) throws Resources.NotFoundException {
        try {
            return getBooleanImpl(obtainEnvironmentId(id));
        } catch (Resources.NotFoundException e) {
            return getBooleanImpl(id);
        }
    }

    private boolean getBooleanImpl(int environmentId) throws Resources.NotFoundException {
        return mResources.getBoolean(environmentId);
    }


    /**
     * @return environment name based on os build type.
     * throws {@link EnvironmentException}
     */
    @NonNull
    public String getEnvironment() throws EnvironmentException {
        String environmentName = mResources.getString(R.string.qa);
        // TODO: Uncomment this when going for production
//        BuildType osBuildType = getOSBuildType();
//        switch (osBuildType) {
//            case ENGINEERING:
//                environmentName = mResources.getString(R.string.qa);
//                break;
//            case PRODUCTION:
//                environmentName = mResources.getString(R.string.production);
//                break;
//            case SBOX:
//                environmentName = mResources.getString(R.string.sbox);
//                break;
//            case UNDEFINED:
//            default:
//                Log.w(TAG, "Unable to define environment: Os type is undefined.");
//                throw new EnvironmentException(EnvironmentException.EnvironmentExceptionCause.BUILD_TYPE);
//        }

//        Log.i(TAG, "Os type is: " + osBuildType.getAlias() + "; defined environment is: " + environmentName);

        return environmentName;
    }

    /**
     * @return OS build type, this is available in build prop generated at the OS compile time.
     */
    @NonNull
    private BuildType getOSBuildType() {
        String osBuildType = null;
        Process ifc = null;
        BufferedReader bis = null;

        try {
            ifc = Runtime.getRuntime().exec("getprop ro.vfi.env.id");
            bis = new BufferedReader(new InputStreamReader(ifc.getInputStream()));
            osBuildType = bis.readLine();
        } catch (java.io.IOException e) {
            Log.e(TAG, "IOException", e);
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (ifc != null) {
                ifc.destroy();
            }
        }

        BuildType buildType = BuildType.getByAlias(osBuildType);
        Log.d(TAG, "OS Build Type is = " + buildType.getAlias());
        return buildType;
    }

}