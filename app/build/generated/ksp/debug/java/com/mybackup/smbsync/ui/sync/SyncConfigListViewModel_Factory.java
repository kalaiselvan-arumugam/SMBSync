package com.mybackup.smbsync.ui.sync;

import androidx.work.WorkManager;
import com.mybackup.smbsync.data.repository.SmbServerRepository;
import com.mybackup.smbsync.data.repository.SyncConfigurationRepository;
import com.mybackup.smbsync.data.repository.SyncLogRepository;
import com.mybackup.smbsync.domain.service.SyncStatusManager;
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
public final class SyncConfigListViewModel_Factory implements Factory<SyncConfigListViewModel> {
  private final Provider<SyncConfigurationRepository> repositoryProvider;

  private final Provider<SmbServerRepository> serverRepoProvider;

  private final Provider<SyncStatusManager> syncStatusManagerProvider;

  private final Provider<WorkManager> workManagerProvider;

  private final Provider<SyncLogRepository> logRepositoryProvider;

  public SyncConfigListViewModel_Factory(Provider<SyncConfigurationRepository> repositoryProvider,
      Provider<SmbServerRepository> serverRepoProvider,
      Provider<SyncStatusManager> syncStatusManagerProvider,
      Provider<WorkManager> workManagerProvider,
      Provider<SyncLogRepository> logRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.serverRepoProvider = serverRepoProvider;
    this.syncStatusManagerProvider = syncStatusManagerProvider;
    this.workManagerProvider = workManagerProvider;
    this.logRepositoryProvider = logRepositoryProvider;
  }

  @Override
  public SyncConfigListViewModel get() {
    return newInstance(repositoryProvider.get(), serverRepoProvider.get(), syncStatusManagerProvider.get(), workManagerProvider.get(), logRepositoryProvider.get());
  }

  public static SyncConfigListViewModel_Factory create(
      Provider<SyncConfigurationRepository> repositoryProvider,
      Provider<SmbServerRepository> serverRepoProvider,
      Provider<SyncStatusManager> syncStatusManagerProvider,
      Provider<WorkManager> workManagerProvider,
      Provider<SyncLogRepository> logRepositoryProvider) {
    return new SyncConfigListViewModel_Factory(repositoryProvider, serverRepoProvider, syncStatusManagerProvider, workManagerProvider, logRepositoryProvider);
  }

  public static SyncConfigListViewModel newInstance(SyncConfigurationRepository repository,
      SmbServerRepository serverRepo, SyncStatusManager syncStatusManager, WorkManager workManager,
      SyncLogRepository logRepository) {
    return new SyncConfigListViewModel(repository, serverRepo, syncStatusManager, workManager, logRepository);
  }
}
