package com.example.demo.buisnessUsage.users.structure

import com.example.demo.backendUsage.health.NotifierService
import com.example.demo.backendUsage.log
import com.example.demo.buisnessUsage.users.errorHandlers.decideWhatToThrowUser
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UserService(private val userRepository: UserRepository,
    private val notifierService: NotifierService) {

    fun createUser(uid: String): User? {
        if (doesUserExist(uid)) {
            return null
        }

        var res: User? = null
        try {
            if (uid.isBlank() || !uid.matches(Regex("^[A-Za-z0-9+/_-]{28}$"))) {
                throw IllegalArgumentException()
            }
            res = userRepository.save(User(uid = uid))

        } catch (e: Exception) {
            log.error(e.message)
            notifierService.sendAlert("User Service Error", e.message + "")

            decideWhatToThrowUser(e)
        }

        if (res == null) throw NullPointerException()
        return res
    }

    fun doesUserExist(uid: String): Boolean {
        var res: Boolean? = null
        try {
            if (uid.isBlank() || !uid.matches(Regex("^[A-Za-z0-9+/_-]{28}$")))
                throw IllegalArgumentException()

            res = userRepository.findByUid(uid) != null

        } catch (e: Exception) {
            log.error(e.message)
            notifierService.sendAlert("User Service Error", e.message + "")

            decideWhatToThrowUser(e)
        }

        if (res == null) throw NullPointerException()
        return res
    }

    fun getUser(uid: String): Optional<User> {
        var res: Optional<User>? = null
        try {
            if (uid.isBlank() || !uid.matches(Regex("^[A-Za-z0-9+/_-]{28}$")))
                throw IllegalArgumentException()

            res = Optional.ofNullable(userRepository.findByUid(uid))
        } catch (e: Exception) {
            log.error(e.message)
            notifierService.sendAlert("User Service Error", e.message + "")

            decideWhatToThrowUser(e)
        }

        if (res == null) throw NullPointerException()
        return res
    }

    fun removeUser(uid: String) {
        try {
            if (uid.isBlank() || !uid.matches(Regex("^[A-Za-z0-9+/_-]{28}$")))
                throw IllegalArgumentException()

            userRepository.deleteById(uid)
        } catch (e: EmptyResultDataAccessException) {
            log.error(e.message)
            notifierService.sendAlert("User Service Error", e.message + "")

            decideWhatToThrowUser(e)
        }
    }
}