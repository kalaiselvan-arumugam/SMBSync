package com.mybackup.smbsync.ui.sync;

import androidx.work.WorkManager;
import com.mybackup.smbsync.data.repository.SmbServerRepository;
import com.mybackup.smbsync.data.repository.SyncConfigurationRepository;
import com.mybackup.smbsync.util.CredentialEncryption;
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
public final class SyncConfigViewModel_Factory implements Factory<SyncConfigViewModel> {
  private final Provider<SyncConfigurationRepository> syncRepoProvider;

  private final Provider<SmbServerRepository> serverRepoProvider;

  private final Provider<CredentialEncryption> credentialEncryptionProvider;

  private final Provider<WorkManager> workManagerProvider;

  public SyncConfigViewModel_Factory(Provider<SyncConfigurationRepository> syncRepoProvider,
      Provider<SmbServerRepository> serverRepoProvider,
      Provider<CredentialEncryption> credentialEncryptionProvider,
      Provider<WorkManager> workManagerProvider) {
    this.syncRepoProvider = syncRepoProvider;
    this.serverRepoProvider = serverRepoProvider;
    this.credentialEncryptionProvider = credentialEncryptionProvider;
    this.workManagerProvider = workManagerProvider;
  }

  @Override
  public SyncConfigViewModel get() {
    return newInstance(syncRepoProvider.get(), serverRepoProvider.get(), credentialEncryptionProvider.get(), workManagerProvider.get());
  }

  public static SyncConfigViewModel_Factory create(
      Provider<SyncConfigurationRepository> syncRepoProvider,
      Provider<SmbServerRepository> serverRepoProvider,
      Provider<CredentialEncryption> credentialEncryptionProvider,
      Provider<WorkManager> workManagerProvider) {
    return new SyncConfigViewModel_Factory(syncRepoProvider, serverRepoProvider, credentialEncryptionProvider, workManagerProvider);
  }

  public static SyncConfigViewModel newInstance(SyncConfigurationRepository syncRepo,
      SmbServerRepository serverRepo, CredentialEncryption credentialEncryption,
      WorkManager workManager) {
    return new SyncConfigViewModel(syncRepo, serverRepo, credentialEncryption, workManager);
  }
}
