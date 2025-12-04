package com.mybackup.smbsync.di;

import android.content.Context;
import com.mybackup.smbsync.util.BatteryOptimizationHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class UtilModule_ProvideBatteryOptimizationHelperFactory implements Factory<BatteryOptimizationHelper> {
  private final Provider<Context> contextProvider;

  public UtilModule_ProvideBatteryOptimizationHelperFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BatteryOptimizationHelper get() {
    return provideBatteryOptimizationHelper(contextProvider.get());
  }

  public static UtilModule_ProvideBatteryOptimizationHelperFactory create(
      Provider<Context> contextProvider) {
    return new UtilModule_ProvideBatteryOptimizationHelperFactory(contextProvider);
  }

  public static BatteryOptimizationHelper provideBatteryOptimizationHelper(Context context) {
    return Preconditions.checkNotNullFromProvides(UtilModule.INSTANCE.provideBatteryOptimizationHelper(context));
  }
}
