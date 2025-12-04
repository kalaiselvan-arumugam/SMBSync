package com.mybackup.smbsync.ui.auth;

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
public final class AppLockViewModel_Factory implements Factory<AppLockViewModel> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  public AppLockViewModel_Factory(Provider<PreferencesManager> preferencesManagerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public AppLockViewModel get() {
    return newInstance(preferencesManagerProvider.get());
  }

  public static AppLockViewModel_Factory create(
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new AppLockViewModel_Factory(preferencesManagerProvider);
  }

  public static AppLockViewModel newInstance(PreferencesManager preferencesManager) {
    return new AppLockViewModel(preferencesManager);
  }
}
