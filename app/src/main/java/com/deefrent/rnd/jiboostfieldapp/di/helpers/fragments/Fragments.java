package com.deefrent.rnd.jiboostfieldapp.di.helpers.fragments;


import com.deefrent.rnd.common.utils.Constants;

public final class Fragments {

    public static final Fragments INSTANCE;

    private Fragments() {
    }

    static {
        Fragments var0 = new Fragments();
        INSTANCE = var0;
    }
    public static final class FeatureIdentity implements AddressableFragment {

        private static final String className;

        public static final FeatureIdentity INSTANCE;


        public String getClassName() {
            return className;
        }

        private FeatureIdentity() {
        }

        static {
            FeatureIdentity var0 = new FeatureIdentity();
            INSTANCE = var0;
            className = Constants.BASE_PACKAGE_NAME +".identity.ui.LoginDialogFragment";
        }
    }
}

