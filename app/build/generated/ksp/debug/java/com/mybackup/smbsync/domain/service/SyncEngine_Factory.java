package com.mybackup.smbsync.domain.service;

import android.content.Context;
import com.mybackup.smbsync.data.repository.SmbServerRepository;
import com.mybackup.smbsync.data.repository.SyncLogRepository;
import com.mybackup.smbsync.util.CredentialEncryption;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SyncEngine_Factory implements Factory<SyncEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<SmbServerRepository> serverRepositoryProvider;

  private final Provider<SyncLogRepository> logRepositoryProvider;

  private final Provider<CredentialEncryption> credentialEncryptionProvider;

  public SyncEngine_Factory(Provider<Context> contextProvider,
      Provider<SmbServerRepository> serverRepositoryProvider,
      Provider<SyncLogRepository> logRepositoryProvider,
      Provider<CredentialEncryption> credentialEncryptionProvider) {
    this.contextProvider = contextProvider;
    this.serverRepositoryProvider = serverRepositoryProvider;
    this.logRepositoryProvider = logRepositoryProvider;
    this.credentialEncryptionProvider = credentialEncryptionProvider;
  }

  @Override
  public SyncEngine get() {
    return newInstance(contextProvider.get(), serverRepositoryProvider.get(), logRepositoryProvider.get(), credentialEncryptionProvider.get());
  }

  public static SyncEngine_Factory create(Provider<Context> contextProvider,
      Provider<SmbServerRepository> serverRepositoryProvider,
      Provider<SyncLogRepository> logRepositoryProvider,
      Provider<CredentialEncryption> credentialEncryptionProvider) {
    return new SyncEngine_Factory(contextProvider, serverRepositoryProvider, logRepositoryProvider, credentialEncryptionProvider);
  }

  public static SyncEngine newInstance(Context context, SmbServerRepository serverRepository,
      SyncLogRepository logRepository, CredentialEncryption credentialEncryption) {
    return new SyncEngine(context, serverRepository, logRepository, credentialEncryption);
  }
}
