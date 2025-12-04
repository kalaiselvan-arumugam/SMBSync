package com.mybackup.smbsync.data.repository;

import com.mybackup.smbsync.data.local.SmbServerDao;
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
public final class SmbServerRepository_Factory implements Factory<SmbServerRepository> {
  private final Provider<SmbServerDao> smbServerDaoProvider;

  public SmbServerRepository_Factory(Provider<SmbServerDao> smbServerDaoProvider) {
    this.smbServerDaoProvider = smbServerDaoProvider;
  }

  @Override
  public SmbServerRepository get() {
    return newInstance(smbServerDaoProvider.get());
  }

  public static SmbServerRepository_Factory create(Provider<SmbServerDao> smbServerDaoProvider) {
    return new SmbServerRepository_Factory(smbServerDaoProvider);
  }

  public static SmbServerRepository newInstance(SmbServerDao smbServerDao) {
    return new SmbServerRepository(smbServerDao);
  }
}
