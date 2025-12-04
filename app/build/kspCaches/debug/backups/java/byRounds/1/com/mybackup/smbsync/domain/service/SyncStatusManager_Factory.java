package com.mybackup.smbsync.domain.service;

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
public final class SyncStatusManager_Factory implements Factory<SyncStatusManager> {
  @Override
  public SyncStatusManager get() {
    return newInstance();
  }

  public static SyncStatusManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SyncStatusManager newInstance() {
    return new SyncStatusManager();
  }

  private static final class InstanceHolder {
    private static final SyncStatusManager_Factory INSTANCE = new SyncStatusManager_Factory();
  }
}
