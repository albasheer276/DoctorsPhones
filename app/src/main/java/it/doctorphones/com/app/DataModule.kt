package it.doctorphones.com.app

import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.doctorphones.com.utils.DOCTORS_TABLE
import it.doctorphones.com.utils.FORUMS_TABLE
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideDatabaseReference(): DatabaseReference {
        Firebase.database.setPersistenceEnabled(true)
        Firebase.database.setPersistenceCacheSizeBytes(50 * 1000 * 1000);
        Firebase.database.reference.keepSynced(true)
        return Firebase.database.reference
    }

    @Provides
    @Singleton
    fun provideStorageReference(): StorageReference = Firebase.storage.reference
}