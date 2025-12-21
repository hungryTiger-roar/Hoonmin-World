package com.ssafy.hm.ui.state

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssafy.hm.data.local.AuthStore
import com.ssafy.hm.data.network.NetworkModule
import com.ssafy.hm.data.repository.*

class AuthViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val repo = AccountRepository(NetworkModule.api)
    private val store = AuthStore(context.applicationContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repo, store) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

class HomeViewModelFactory : ViewModelProvider.Factory {
    private val homeRepo = HomeRepository(NetworkModule.api)
    private val boardRepo = BoardRepository(NetworkModule.api)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(homeRepo, boardRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

class CatalogViewModelFactory : ViewModelProvider.Factory {
    private val attRepo = AttractionRepository(NetworkModule.api)
    private val itemRepo = ItemRepository(NetworkModule.api)
    private val reviewRepo = ReviewRepository(NetworkModule.api)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            return CatalogViewModel(attRepo, itemRepo, reviewRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

class OrderViewModelFactory : ViewModelProvider.Factory {
    private val orderRepo = OrderRepository(NetworkModule.api)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(orderRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

class AdminOrderViewModelFactory : ViewModelProvider.Factory {
    private val orderRepo = OrderRepository(NetworkModule.api)
    private val accountRepo = AccountRepository(NetworkModule.api)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminOrderViewModel::class.java)) {
            return AdminOrderViewModel(orderRepo, accountRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

class FriendViewModelFactory : ViewModelProvider.Factory {
    private val friendRepo = FriendRepository(NetworkModule.api)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendViewModel::class.java)) {
            return FriendViewModel(friendRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

class LineViewModelFactory : ViewModelProvider.Factory {
    private val lineRepo = LineRepository(NetworkModule.api)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LineViewModel::class.java)) {
            return LineViewModel(lineRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

class AdminAttractionLineViewModelFactory : ViewModelProvider.Factory {
    private val lineRepo = LineRepository(NetworkModule.api)
    private val accountRepo = AccountRepository(NetworkModule.api)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminAttractionLineViewModel::class.java)) {
            return AdminAttractionLineViewModel(lineRepo, accountRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

class UploadViewModelFactory : ViewModelProvider.Factory {
    private val uploadRepo = UploadRepository(NetworkModule.api)
    private val homeRepo = HomeRepository(NetworkModule.api)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(uploadRepo, homeRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
