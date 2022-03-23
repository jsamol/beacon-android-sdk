package it.airgap.beaconsdk.blockchain.tezos.internal.creator

import it.airgap.beaconsdk.core.blockchain.Blockchain
import it.airgap.beaconsdk.core.internal.blockchain.creator.DataBlockchainCreator
import it.airgap.beaconsdk.core.internal.blockchain.creator.V1BeaconMessageBlockchainCreator
import it.airgap.beaconsdk.core.internal.blockchain.creator.V2BeaconMessageBlockchainCreator
import it.airgap.beaconsdk.core.internal.blockchain.creator.V3BeaconMessageBlockchainCreator

internal class TezosCreator(
    // -- data --
    override val data: DataBlockchainCreator,

    // -- VersionedBeaconMessage --
    override val v1: V1BeaconMessageBlockchainCreator,
    override val v2: V2BeaconMessageBlockchainCreator,
    override val v3: V3BeaconMessageBlockchainCreator,
) : Blockchain.Creator
