package com.deefrent.rnd.jiboostfieldapp.di.helpers.features;

import com.deefrent.rnd.jiboostfieldapp.di.helpers.activities.AddressableActivity;
import com.deefrent.rnd.common.utils.Constants;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class Modules {


    private static final List<FeatureModule> modules;

    public static final Modules INSTANCE;

    public final FeatureModule getModuleFromName(String moduleName) {

        Iterator var4 = modules.iterator();

        FeatureModule it;
        do {
            if (!var4.hasNext()) {
                throw new IllegalArgumentException(moduleName + " is not found");
            }
            Object element$iv = var4.next();
            it = (FeatureModule) element$iv;
        } while (!it.getName().equalsIgnoreCase(moduleName));

        return it;
    }

    private Modules() {
    }

    static {
        Modules var0 = new Modules();
        INSTANCE = var0;
        modules = Arrays.asList(new FeatureModule[]{(FeatureModule) FeatureFieldApp.INSTANCE,
                (FeatureModule) FeatureSupport.INSTANCE}.clone()
        );
    }

    public static final class FeatureFieldApp implements FeatureModule, AddressableActivity {

        private static final String name;

        private static final String injectorName;

        private static final String className;

        public static final FeatureFieldApp INSTANCE;

        public String getName() {
            return name;
        }

        public String getInjectorName() {
            return injectorName;
        }

        private FeatureFieldApp() {
        }

        static {
            FeatureFieldApp var0 = new FeatureFieldApp();
            INSTANCE = var0;
            name = "fieldapp";
            injectorName = Constants.BASE_PACKAGE_NAME+"."+name+".di.FieldAppInjector";
            className = Constants.BASE_PACKAGE_NAME +"."+name+".MainActivity";
        }

        @Override
        public String getClassName() {
            return className;
        }
    }

    public static final class FeatureSupport implements FeatureModule {

        private static final String name;

        private static final String injectorName;

        public static final FeatureSupport INSTANCE;

        public String getName() {
            return name;
        }

        public String getInjectorName() {
            return injectorName;
        }

        private FeatureSupport() {
        }

        static {
            FeatureSupport var0 = new FeatureSupport();
            INSTANCE = var0;
            name = "support";
            injectorName = Constants.BASE_PACKAGE_NAME+".support.di.SupportInjector";
        }
    }

}
