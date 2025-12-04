package com.mybackup.smbsync.di;

import com.mybackup.smbsync.data.local.AppDatabase;
import com.mybackup.smbsync.data.local.SyncConfigurationDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideSyncConfigurationDaoFactory implements Factory<SyncConfigurationDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideSyncConfigurationDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SyncConfigurationDao get() {
    return provideSyncConfigurationDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideSyncConfigurationDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSyncConfigurationDaoFactory(databaseProvider);
  }

  public static SyncConfigurationDao provideSyncConfigurationDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSyncConfigurationDao(database));
  }
}
