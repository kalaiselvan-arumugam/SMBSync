package com.mybackup.smbsync.domain.service;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.mybackup.smbsync.data.repository.SyncConfigurationRepository;
import dagger.internal.DaggerGenerated;
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
public final class SyncWorker_Factory {
  private final Provider<SyncEngine> syncEngineProvider;

  private final Provider<SyncConfigurationRepository> syncRepoProvider;

  private final Provider<SyncStatusManager> syncStatusManagerProvider;

  public SyncWorker_Factory(Provider<SyncEngine> syncEngineProvider,
      Provider<SyncConfigurationRepository> syncRepoProvider,
      Provider<SyncStatusManager> syncStatusManagerProvider) {
    this.syncEngineProvider = syncEngineProvider;
    this.syncRepoProvider = syncRepoProvider;
    this.syncStatusManagerProvider = syncStatusManagerProvider;
  }

  public SyncWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, syncEngineProvider.get(), syncRepoProvider.get(), syncStatusManagerProvider.get());
  }

  public static SyncWorker_Factory create(Provider<SyncEngine> syncEngineProvider,
      Provider<SyncConfigurationRepository> syncRepoProvider,
      Provider<SyncStatusManager> syncStatusManagerProvider) {
    return new SyncWorker_Factory(syncEngineProvider, syncRepoProvider, syncStatusManagerProvider);
  }

  public static SyncWorker newInstance(Context appContext, WorkerParameters workerParams,
      SyncEngine syncEngine, SyncConfigurationRepository syncRepo,
      SyncStatusManager syncStatusManager) {
    return new SyncWorker(appContext, workerParams, syncEngine, syncRepo, syncStatusManager);
  }
}
