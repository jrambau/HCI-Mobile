package network.Repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import model.Card
import network.WalletRemoteDataSource
import network.model.NetworkCard
import network.model.NetworkInterest
import network.model.NetworkInvestInfo
import network.model.NetworkWalletInfo

class WalletRepository(
    private val remoteDataSource: WalletRemoteDataSource
) {
    private val mutex = Mutex()
    private var balance:Double? = null
    private var investment:Double? = null
    private var cards: Array<NetworkCard>? = null
    private var currentCard: NetworkCard? = null
    suspend fun getWalletBalance() : NetworkWalletInfo {
       val response = remoteDataSource.getWalletBalance()
        mutex.withLock {
            this.balance = response.balance
        }
        return response
    }
    suspend fun rechargeWallet(amount: Double) : NetworkWalletInfo {
        val response = remoteDataSource.rechargeWallet(amount)
        mutex.withLock {
            this.balance = response.newBalance
        }
        return response
    }
    suspend fun getInvestment() : NetworkInvestInfo {
       val response = remoteDataSource.getInvestment()
        mutex.withLock {
            this.investment = response.investment
        }
        return response
    }
    suspend fun invest(amount: Double) : NetworkWalletInfo {
        return remoteDataSource.invest(amount)
    }
    suspend fun divest(amount: Double) : NetworkWalletInfo {
        return remoteDataSource.divest(amount)
    }
    suspend fun getWalletDetails() : NetworkWalletInfo {
        return remoteDataSource.getWalletDetails()
    }
    suspend fun getCards() : Array<NetworkCard> {
        val response = remoteDataSource.getCards()
        mutex.withLock {
            this.cards = response
        }
        return response
    }
    suspend fun addCard(card: NetworkCard) : NetworkCard {
        val response = remoteDataSource.addCard(card)
        mutex.withLock {
            this.currentCard = response
        }
        return response
    }
    suspend fun deleteCard(cardId: Int) {
        remoteDataSource.deleteCard(cardId)
    }
    suspend fun getDailyReturns(page: Int) : Array<NetworkInvestInfo> {
        return remoteDataSource.getDailyReturns(page)
    }
    suspend fun getDailyInterest() : NetworkInterest {
        return remoteDataSource.getDailyInterest()
    }

}