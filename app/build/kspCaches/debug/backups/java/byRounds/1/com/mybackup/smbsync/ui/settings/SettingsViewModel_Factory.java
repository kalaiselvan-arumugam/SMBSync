package com.mybackup.smbsync.ui.settings;

import com.mybackup.smbsync.util.BatteryOptimizationHelper;
import com.mybackup.smbsync.util.PreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<BatteryOptimizationHelper> batteryHelperProvider;

  public SettingsViewModel_Factory(Provider<PreferencesManager> preferencesManagerProvider,
      Provider<BatteryOptimizationHelper> batteryHelperProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.batteryHelperProvider = batteryHelperProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(preferencesManagerProvider.get(), batteryHelperProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<BatteryOptimizationHelper> batteryHelperProvider) {
    return new SettingsViewModel_Factory(preferencesManagerProvider, batteryHelperProvider);
  }

  public static SettingsViewModel newInstance(PreferencesManager preferencesManager,
      BatteryOptimizationHelper batteryHelper) {
    return new SettingsViewModel(preferencesManager, batteryHelper);
  }
}
