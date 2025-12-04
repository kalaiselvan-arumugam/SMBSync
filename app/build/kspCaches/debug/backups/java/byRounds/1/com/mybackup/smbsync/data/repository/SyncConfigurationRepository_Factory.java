package com.mybackup.smbsync.data.repository;

import com.mybackup.smbsync.data.local.SyncConfigurationDao;
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
public final class SyncConfigurationRepository_Factory implements Factory<SyncConfigurationRepository> {
  private final Provider<SyncConfigurationDao> syncConfigurationDaoProvider;

  public SyncConfigurationRepository_Factory(
      Provider<SyncConfigurationDao> syncConfigurationDaoProvider) {
    this.syncConfigurationDaoProvider = syncConfigurationDaoProvider;
  }

  @Override
  public SyncConfigurationRepository get() {
    return newInstance(syncConfigurationDaoProvider.get());
  }

  public static SyncConfigurationRepository_Factory create(
      Provider<SyncConfigurationDao> syncConfigurationDaoProvider) {
    return new SyncConfigurationRepository_Factory(syncConfigurationDaoProvider);
  }

  public static SyncConfigurationRepository newInstance(SyncConfigurationDao syncConfigurationDao) {
    return new SyncConfigurationRepository(syncConfigurationDao);
  }
}
