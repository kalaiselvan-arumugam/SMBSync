package com.mybackup.smbsync;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MyBackupApplication_MembersInjector implements MembersInjector<MyBackupApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public MyBackupApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<MyBackupApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new MyBackupApplication_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(MyBackupApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.mybackup.smbsync.MyBackupApplication.workerFactory")
  public static void injectWorkerFactory(MyBackupApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
