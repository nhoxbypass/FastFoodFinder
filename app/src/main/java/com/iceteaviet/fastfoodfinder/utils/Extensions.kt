package com.iceteaviet.fastfoodfinder.utils

import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User

suspend fun getCurrentUserHelper(clientAuth: ClientAuth, userRepository: UserRepository): User? {
    val uid = clientAuth.getCurrentUserUid()
    if (isValidUserUid(uid)) {
        return try {
            userRepository.getUser(uid)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    return null
}
