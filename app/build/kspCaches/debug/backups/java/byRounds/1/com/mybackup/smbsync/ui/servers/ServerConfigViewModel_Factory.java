package com.mybackup.smbsync.ui.servers;

import com.mybackup.smbsync.data.repository.SmbServerRepository;
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
public final class ServerConfigViewModel_Factory implements Factory<ServerConfigViewModel> {
  private final Provider<SmbServerRepository> repositoryProvider;

  private final Provider<CredentialEncryption> credentialEncryptionProvider;

  public ServerConfigViewModel_Factory(Provider<SmbServerRepository> repositoryProvider,
      Provider<CredentialEncryption> credentialEncryptionProvider) {
    this.repositoryProvider = repositoryProvider;
    this.credentialEncryptionProvider = credentialEncryptionProvider;
  }

  @Override
  public ServerConfigViewModel get() {
    return newInstance(repositoryProvider.get(), credentialEncryptionProvider.get());
  }

  public static ServerConfigViewModel_Factory create(
      Provider<SmbServerRepository> repositoryProvider,
      Provider<CredentialEncryption> credentialEncryptionProvider) {
    return new ServerConfigViewModel_Factory(repositoryProvider, credentialEncryptionProvider);
  }

  public static ServerConfigViewModel newInstance(SmbServerRepository repository,
      CredentialEncryption credentialEncryption) {
    return new ServerConfigViewModel(repository, credentialEncryption);
  }
}
