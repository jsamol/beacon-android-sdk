package it.airgap.beaconsdk.transport.p2p.matrix.internal

internal object BeaconP2pMatrixConfiguration {
    const val MATRIX_API_BASE: String = "/_matrix/client"
    const val MATRIX_API_VERSION: String = "r0"

    const val MATRIX_ROOM_VERSION: String = "5"

    const val MATRIX_MAX_SYNC_RETRIES: Int = 3

    const val JOIN_DELAY_MS: Long = 200
    const val MAX_JOIN_RETRIES: Int = 10

    val defaultNodes: List<String> = listOf(
        "beacon-node-1.sky.papers.tech",
        "beacon-node-0.papers.tech:8448",
    )
}