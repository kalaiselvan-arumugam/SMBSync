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
public final class PinConfirmViewModel_Factory implements Factory<PinConfirmViewModel> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  public PinConfirmViewModel_Factory(Provider<PreferencesManager> preferencesManagerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public PinConfirmViewModel get() {
    return newInstance(preferencesManagerProvider.get());
  }

  public static PinConfirmViewModel_Factory create(
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new PinConfirmViewModel_Factory(preferencesManagerProvider);
  }

  public static PinConfirmViewModel newInstance(PreferencesManager preferencesManager) {
    return new PinConfirmViewModel(preferencesManager);
  }
}
