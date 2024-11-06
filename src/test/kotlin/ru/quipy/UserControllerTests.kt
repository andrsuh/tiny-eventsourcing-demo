package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.controller.UserController

@SpringBootTest
class UserControllerTests {

    @Autowired
    private lateinit var userController: UserController

    @Test
    fun createUser_UserCreatedWithCorrectFields() {
        val response = userController.createUser(
                "nickname",
                "password",
                "name"
        )
        var r = response.userId
        Assertions.assertEquals("nickname", response.nickname)
        Assertions.assertEquals("name", response.uname)
        Assertions.assertEquals("password", response.password)
        Assertions.assertEquals(1, response.version)
    }

    @Test
    fun getUser_GotRightUserFromMany() {
        userController.createUser(
                "nickname1",
                "password1",
                "name1"
        )
        val user2 = userController.createUser(
                "nickname2",
                "password2",
                "name2"
        )
        val response = userController.getUser(user2.userId)
        Assertions.assertNotNull(response)
        Assertions.assertEquals("nickname2", response!!.getNickname())
        Assertions.assertEquals("name2", response.getName())
        Assertions.assertEquals("password2", response.getPassword())
    }
}