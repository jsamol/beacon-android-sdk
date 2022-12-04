package it.airgap.beaconsdkdemo.di

import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import it.airgap.beaconsdkdemo.controller.account.blockchain.BlockchainController
import it.airgap.beaconsdkdemo.controller.account.blockchain.SubstrateController
import it.airgap.beaconsdkdemo.controller.account.blockchain.TezosController

@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    fun provideBlockchainControllers(
        tezosController: TezosController,
        substrateController: SubstrateController,
    ): List<BlockchainController<*>> = listOf(tezosController, substrateController)
}