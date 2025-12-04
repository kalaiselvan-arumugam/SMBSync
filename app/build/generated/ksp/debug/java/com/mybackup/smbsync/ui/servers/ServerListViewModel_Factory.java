package com.mybackup.smbsync.ui.servers;

import com.mybackup.smbsync.data.remote.SmbDiscoveryService;
import com.mybackup.smbsync.data.repository.SmbServerRepository;
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
public final class ServerListViewModel_Factory implements Factory<ServerListViewModel> {
  private final Provider<SmbServerRepository> repositoryProvider;

  private final Provider<SmbDiscoveryService> discoveryServiceProvider;

  public ServerListViewModel_Factory(Provider<SmbServerRepository> repositoryProvider,
      Provider<SmbDiscoveryService> discoveryServiceProvider) {
    this.repositoryProvider = repositoryProvider;
    this.discoveryServiceProvider = discoveryServiceProvider;
  }

  @Override
  public ServerListViewModel get() {
    return newInstance(repositoryProvider.get(), discoveryServiceProvider.get());
  }

  public static ServerListViewModel_Factory create(Provider<SmbServerRepository> repositoryProvider,
      Provider<SmbDiscoveryService> discoveryServiceProvider) {
    return new ServerListViewModel_Factory(repositoryProvider, discoveryServiceProvider);
  }

  public static ServerListViewModel newInstance(SmbServerRepository repository,
      SmbDiscoveryService discoveryService) {
    return new ServerListViewModel(repository, discoveryService);
  }
}
