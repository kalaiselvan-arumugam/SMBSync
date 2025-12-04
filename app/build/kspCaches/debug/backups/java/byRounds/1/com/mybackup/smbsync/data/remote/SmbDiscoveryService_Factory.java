package com.mybackup.smbsync.data.remote;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class SmbDiscoveryService_Factory implements Factory<SmbDiscoveryService> {
  @Override
  public SmbDiscoveryService get() {
    return newInstance();
  }

  public static SmbDiscoveryService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SmbDiscoveryService newInstance() {
    return new SmbDiscoveryService();
  }

  private static final class InstanceHolder {
    private static final SmbDiscoveryService_Factory INSTANCE = new SmbDiscoveryService_Factory();
  }
}
