package network

import SessionManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import model.User
import model.asNetworkModel
import network.api.UserApiService
import network.api.WalletApiService
import network.model.NetworkCard
import network.model.NetworkInterest
import network.model.NetworkInvestInfo
import network.model.asModel
import network.model.NetworkUserResponse
import network.model.NetworkWalletInfo

class WalletRemoteDataSource (
    private val walletApiService: WalletApiService,
    private val sessionManager: SessionManager
): RemoteDataSource(){
suspend fun getWalletBalance() : NetworkWalletInfo {
    return handleApiResponse { walletApiService.getWalletBalance() }
}
suspend fun rechargeWallet(amount: Double) : NetworkWalletInfo {
    return handleApiResponse { walletApiService.rechargeWallet(NetworkWalletInfo(amount=amount))}
}
suspend fun getInvestment() : NetworkInvestInfo {
    return handleApiResponse { walletApiService.getInvestment() }
}
    suspend fun invest(amount: Double) : NetworkWalletInfo {
        return handleApiResponse { walletApiService.invest(NetworkWalletInfo(amount=amount))}
    }
    suspend fun divest(amount: Double) : NetworkWalletInfo {
        return handleApiResponse { walletApiService.divest(NetworkWalletInfo(amount=amount))}
    }
    suspend fun getWalletDetails() : NetworkWalletInfo {
        return handleApiResponse { walletApiService.getWalletDetails() }
    }
    suspend fun getCards() : Array<NetworkCard> {
        return handleApiResponse { walletApiService.getCards() }
    }
    suspend fun addCard(card: NetworkCard) : NetworkCard {
        return handleApiResponse { walletApiService.addCard(card) }
    }
    suspend fun deleteCard(cardId: Int) {
        handleApiResponse { walletApiService.deleteCard(cardId) }
    }
    suspend fun getDailyReturns(page: Int) : Array<NetworkInvestInfo> {
        return handleApiResponse { walletApiService.getDailyReturns(page) }
    }
    suspend fun getDailyInterest() : NetworkInterest {
        return handleApiResponse { walletApiService.getDailyInterest() }
    }
}
