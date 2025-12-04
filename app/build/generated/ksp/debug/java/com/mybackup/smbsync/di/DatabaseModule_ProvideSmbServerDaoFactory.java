package com.mybackup.smbsync.di;

import com.mybackup.smbsync.data.local.AppDatabase;
import com.mybackup.smbsync.data.local.SmbServerDao;
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
public final class DatabaseModule_ProvideSmbServerDaoFactory implements Factory<SmbServerDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideSmbServerDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SmbServerDao get() {
    return provideSmbServerDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideSmbServerDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSmbServerDaoFactory(databaseProvider);
  }

  public static SmbServerDao provideSmbServerDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSmbServerDao(database));
  }
}
