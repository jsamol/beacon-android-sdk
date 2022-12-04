package it.airgap.beaconsdkdemo.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.airgap.tezos.core.Tezos
import it.airgap.tezos.core.crypto.CryptoProvider
import it.airgap.tezos.crypto.bouncycastle.BouncyCastleCryptoProvider
import it.airgap.tezos.http.ktor.KtorHttpClientProvider
import it.airgap.tezos.rpc.RpcModule
import it.airgap.tezos.rpc.http.HttpClientProvider
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val SHARED_PREFERENCES_NAME = "it.airgap.beaconsdkdemo"

    @Provides
    @Reusable
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Provides
    @Reusable
    fun provideJson(): Json = Json {
        classDiscriminator = "__type__"
    }

    @Provides
    @Reusable
    fun provideTezos(cryptoProvider: CryptoProvider, httpClientProvider: HttpClientProvider): Tezos =
        Tezos {
            this.cryptoProvider = cryptoProvider
            use(RpcModule) {
                this.httpClientProvider = httpClientProvider
            }
        }

    @Provides
    @Reusable
    fun provideTezosCryptoProvider(): CryptoProvider = BouncyCastleCryptoProvider()

    @Provides
    @Reusable
    fun provideTezosHttpClientProvider(): HttpClientProvider = KtorHttpClientProvider()
}