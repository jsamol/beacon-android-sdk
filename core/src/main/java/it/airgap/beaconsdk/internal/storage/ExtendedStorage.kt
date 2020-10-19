package it.airgap.beaconsdk.internal.storage

import it.airgap.beaconsdk.data.account.AccountInfo
import it.airgap.beaconsdk.data.p2p.P2pPairingRequest
import it.airgap.beaconsdk.data.permission.PermissionInfo
import it.airgap.beaconsdk.data.sdk.AppMetadata
import it.airgap.beaconsdk.storage.BeaconStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private typealias StorageSelectCollection<T> = suspend BeaconStorage.() -> List<T>
private typealias StorageInsertCollection<T> = suspend BeaconStorage.(List<T>) -> Unit

//@ExperimentalCoroutinesApi
internal class ExtendedStorage(private val storage: BeaconStorage) : BeaconStorage by storage {
    private val _accounts: MutableSharedFlow<AccountInfo> by lazy { resourceFlow() }
    val accounts: Flow<AccountInfo>
        get() = _accounts.onStart { emitAll(getAccounts().asFlow()) }

    private val _activeAccountIdentifier: MutableSharedFlow<String?> by lazy {
        resourceFlow(
            bufferCapacity = 1
        )
    }
    val activeAccountIdentifier: Flow<String?>
        get() = _activeAccountIdentifier.onStart { emit(getActiveAccountIdentifier()) }

    private val _appMetadata: MutableSharedFlow<AppMetadata> by lazy { resourceFlow() }
    val appMetadata: Flow<AppMetadata>
        get() = _appMetadata.onStart { emitAll(getAppsMetadata().asFlow()) }

    private val _permissions: MutableSharedFlow<PermissionInfo> by lazy { resourceFlow() }
    val permissions: Flow<PermissionInfo>
        get() = _permissions.onStart { emitAll(getPermissions().asFlow()) }

    private val _p2pPeers: MutableSharedFlow<P2pPairingRequest> by lazy { resourceFlow() }
    val p2pPeers: Flow<P2pPairingRequest>
        get() = _p2pPeers.onStart { emitAll(getP2pPeers().asFlow()) }

    private val activeJobs: MutableSet<Job> = mutableSetOf()

    override suspend fun setActiveAccountIdentifier(activeAccountIdentifier: String) {
        storage.setActiveAccountIdentifier(activeAccountIdentifier)
        notifyScope { _activeAccountIdentifier.tryEmit(activeAccountIdentifier) }
    }

    suspend fun findAccount(predicate: (AccountInfo) -> Boolean): AccountInfo? =
        selectFirst(BeaconStorage::getAccounts, predicate)

    suspend fun addAccounts(
        vararg accounts: AccountInfo,
        overwrite: Boolean = true,
        compare: (AccountInfo, AccountInfo) -> Boolean = { first, second -> first == second }
    ) {
        addAccounts(accounts.toList(), overwrite, compare)
    }

    suspend fun addAccounts(
        accounts: List<AccountInfo>,
        overwrite: Boolean = true,
        compare: (AccountInfo, AccountInfo) -> Boolean = { first, second -> first == second }
    ) {
        add(
            BeaconStorage::getAccounts,
            BeaconStorage::setAccounts,
            _accounts,
            accounts,
            overwrite,
            compare
        )
    }

    suspend fun removeAccounts(predicate: ((AccountInfo) -> Boolean)? = null) {
        if (predicate != null) remove(
            BeaconStorage::getAccounts,
            BeaconStorage::setAccounts,
            predicate
        )
        else removeAll(BeaconStorage::setAccounts)
    }

    suspend fun findAppMetadata(predicate: (AppMetadata) -> Boolean): AppMetadata? =
        selectFirst(BeaconStorage::getAppsMetadata, predicate)

    suspend fun addAppsMetadata(
        vararg appsMetadata: AppMetadata,
        overwrite: Boolean = true,
        compare: (AppMetadata, AppMetadata) -> Boolean = { first, second -> first == second }
    ) {
        addAppsMetadata(appsMetadata.toList(), overwrite, compare)
    }

    suspend fun addAppsMetadata(
        appsMetadata: List<AppMetadata>,
        overwrite: Boolean = true,
        compare: (AppMetadata, AppMetadata) -> Boolean = { first, second -> first == second }
    ) {
        add(
            BeaconStorage::getAppsMetadata,
            BeaconStorage::setAppsMetadata,
            _appMetadata,
            appsMetadata,
            overwrite,
            compare
        )
    }

    suspend fun removeAppsMetadata(predicate: ((AppMetadata) -> Boolean)? = null) {
        if (predicate != null) remove(
            BeaconStorage::getAppsMetadata,
            BeaconStorage::setAppsMetadata,
            predicate
        )
        else removeAll(BeaconStorage::setAppsMetadata)
    }

    suspend fun findPermission(predicate: (PermissionInfo) -> Boolean): PermissionInfo? =
        selectFirst(BeaconStorage::getPermissions, predicate)

    suspend fun addPermissions(
        vararg permissions: PermissionInfo,
        overwrite: Boolean = true,
        compare: (PermissionInfo, PermissionInfo) -> Boolean = { first, second -> first == second }
    ) {
        addPermissions(permissions.toList(), overwrite, compare)
    }

    suspend fun addPermissions(
        permissions: List<PermissionInfo>,
        overwrite: Boolean = true,
        compare: (PermissionInfo, PermissionInfo) -> Boolean = { first, second -> first == second }
    ) {
        add(
            BeaconStorage::getPermissions,
            BeaconStorage::setPermissions,
            _permissions,
            permissions,
            overwrite,
            compare
        )
    }

    suspend fun removePermissions(predicate: ((PermissionInfo) -> Boolean)? = null) {
        if (predicate != null) remove(
            BeaconStorage::getPermissions,
            BeaconStorage::setPermissions,
            predicate
        )
        else removeAll(BeaconStorage::setPermissions)
    }

    suspend fun findP2pPeer(predicate: (P2pPairingRequest) -> Boolean): P2pPairingRequest? =
        selectFirst(BeaconStorage::getP2pPeers, predicate)

    suspend fun addP2pPeers(
        vararg peers: P2pPairingRequest,
        overwrite: Boolean = true,
        compare: (P2pPairingRequest, P2pPairingRequest) -> Boolean = { first, second -> first == second }
    ) {
        addP2pPeers(peers.toList(), overwrite, compare)
    }

    suspend fun addP2pPeers(
        peers: List<P2pPairingRequest>,
        overwrite: Boolean = true,
        compare: (P2pPairingRequest, P2pPairingRequest) -> Boolean = { first, second -> first == second }
    ) {
        add(
            BeaconStorage::getP2pPeers,
            BeaconStorage::setP2pPeers,
            _p2pPeers,
            peers,
            overwrite,
            compare
        )
    }

    suspend fun removeP2pPeers(predicate: ((P2pPairingRequest) -> Boolean)? = null) {
        if (predicate != null) remove(
            BeaconStorage::getP2pPeers,
            BeaconStorage::setP2pPeers,
            predicate
        )
        else removeAll(BeaconStorage::setP2pPeers)
    }

    private suspend fun <T> selectFirst(
        select: StorageSelectCollection<T>,
        predicate: (T) -> Boolean
    ): T? = select(this).find(predicate)

    private suspend fun <T> add(
        select: StorageSelectCollection<T>,
        insert: StorageInsertCollection<T>,
        subscribeFlow: MutableSharedFlow<T>,
        elements: List<T>,
        overwrite: Boolean,
        compare: (T, T) -> Boolean
    ) {
        val entities = select(this).toMutableList()
        val updatedEntities = mutableListOf<T>()

        val (newEntities, existingElements) = elements.partition { toInsert ->
            !entities.map {
                compare(
                    toInsert,
                    it
                )
            }.fold(false, Boolean::or)
        }
        if (overwrite) {
            existingElements
                .map { toInsert -> entities.indexOfFirst { compare(toInsert, it) } to toInsert }
                .forEach { (index, toInsert) ->
                    entities[index] = toInsert
                    updatedEntities.add(toInsert)
                }
        }

        notifyScope {
            (updatedEntities + newEntities).forEach { subscribeFlow.tryEmit(it) }
        }

        insert(this, entities + newEntities)
    }

    private suspend fun <T> remove(
        select: StorageSelectCollection<T>,
        insert: StorageInsertCollection<T>,
        predicate: (T) -> Boolean
    ) {
        insert(this, select(this).filterNot(predicate))
    }

    private suspend fun <T> removeAll(insert: StorageInsertCollection<T>) {
        insert(this, emptyList())
    }

    private fun <T> resourceFlow(bufferCapacity: Int = 64): MutableSharedFlow<T> =
        MutableSharedFlow(extraBufferCapacity = bufferCapacity)

    private fun notifyScope(block: () -> Unit) {
        val job = Job()
        activeJobs.add(job)
        CoroutineScope(job).launch {
            block()
            job.complete()
            activeJobs.remove(job)
        }
    }
}