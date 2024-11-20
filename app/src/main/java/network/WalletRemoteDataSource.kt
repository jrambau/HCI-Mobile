package network

import SessionManager
import model.User
import model.asNetworkModel
import network.api.UserApiService
import network.api.WalletApiService
import network.model.asModel
import network.model.NetworkUserResponse

class WalletRemoteDataSource (
    private val walletApiService: WalletApiService,
    private val sessionManager: SessionManager
): RemoteDataSource(){

}
