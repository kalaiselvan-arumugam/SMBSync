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
public final class PinViewModel_Factory implements Factory<PinViewModel> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  public PinViewModel_Factory(Provider<PreferencesManager> preferencesManagerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public PinViewModel get() {
    return newInstance(preferencesManagerProvider.get());
  }

  public static PinViewModel_Factory create(
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new PinViewModel_Factory(preferencesManagerProvider);
  }

  public static PinViewModel newInstance(PreferencesManager preferencesManager) {
    return new PinViewModel(preferencesManager);
  }
}
