package com.mybackup.smbsync.data.repository;

import com.mybackup.smbsync.data.local.SyncLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SyncLogRepository_Factory implements Factory<SyncLogRepository> {
  private final Provider<SyncLogDao> syncLogDaoProvider;

  public SyncLogRepository_Factory(Provider<SyncLogDao> syncLogDaoProvider) {
    this.syncLogDaoProvider = syncLogDaoProvider;
  }

  @Override
  public SyncLogRepository get() {
    return newInstance(syncLogDaoProvider.get());
  }

  public static SyncLogRepository_Factory create(Provider<SyncLogDao> syncLogDaoProvider) {
    return new SyncLogRepository_Factory(syncLogDaoProvider);
  }

  public static SyncLogRepository newInstance(SyncLogDao syncLogDao) {
    return new SyncLogRepository(syncLogDao);
  }
}
