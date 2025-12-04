package com.mybackup.smbsync.util;

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
public final class CredentialEncryption_Factory implements Factory<CredentialEncryption> {
  @Override
  public CredentialEncryption get() {
    return newInstance();
  }

  public static CredentialEncryption_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CredentialEncryption newInstance() {
    return new CredentialEncryption();
  }

  private static final class InstanceHolder {
    private static final CredentialEncryption_Factory INSTANCE = new CredentialEncryption_Factory();
  }
}
