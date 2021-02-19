<template>
  <v-container>
    <v-form>
      <v-text-field id="username"
                    name="username"
                    color="purple"
                    prepend-icon="mdi-phone"
                    v-model="userLogin"
                    label="Username"
                    :error-messages="usernameErrors"
                    required
      ></v-text-field>
      <!--@input="$v.usernameLogin.$touch()"
                                        @blur="$v.usernameLogin.$touch()"                    -->
      <v-text-field id="password"
                    name="password"
                    color="purple"
                    prepend-icon="mdi-key"
                    type="password"
                    v-model="userPassword"
                    label="Password"
                    :error-messages="passwordErrors"
                    required
      ></v-text-field>
      <!--                         @input="$v.password.$touch()"
                                        @blur="$v.password.$touch()" -->
    </v-form>
    <!-- :disabled="!username || !password" block @click="loginUser()"                 -->
    <v-btn color="success" @click="loginUser()" block outlined>
      <span>Log in</span>
    </v-btn>
  </v-container>
</template>

<script>
import axios from "axios";

export default {
  name: "UserLoginForm",
  props: ["clearLoginResponse", "setLoginResponseData"],
  data() {
    return {
      userLogin: '',
      userPassword: '',
      passwordErrors:[],
      usernameErrors:[],
    }
  },
  methods: {
    loginUser() {
      console.log('loginUser()')
      this.clearLoginResponse()

      // this.loginIncorrect = false

      //this.$v.$touch()
      //if (true/*this.loginValid*/) {

      let auth = new FormData();
      auth.set('username', this.userLogin);
      auth.set('password', this.userPassword);
      console.log('auth', auth)

      const config = {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }

      const loginURL = '/user/login'

      axios.post(loginURL, auth, config).then(response => {
        console.log('authResponse', response)
        this.$store.dispatch('authUser', response.data)
        this.handleSuccessfulLoginResponse(response)
        //this.$store.dispatch('login')
      })
          .catch((error) => {
            console.log('catch login error', error)
            this.handleErrorLoginResponse(error)

            //this.loginIncorrect = true
          })
      //}

    },
    handleSuccessfulLoginResponse(response) {
      console.log('handleSuccessfulLoginResponse', response)
      let responseMessage

      if (response) {
        console.log('handleSuccessfulLoginResponse response', response)

        responseMessage = response.data
        console.log('responseMessage', responseMessage)
        this.setLoginResponseData('success', responseMessage, true)
      } else {
        responseMessage = 'Login error no response'
        this.setLoginResponseData('error', responseMessage, true)
      }
    },
    handleErrorLoginResponse(error) {
      console.log('handleErrorLoginResponse')

      if (error.response) {
        let errorMessage // error.response.data.errorMessage
        const errorData = error.response.data
        const errorStatus = error.response.status
        const errorHeaders = error.response.headers

        console.log('errorMessage', errorData);
        console.log('errorStatus', errorStatus);
        console.log('errorHeaders', errorHeaders);

        switch (errorStatus) {
          case 401: {
            errorMessage = 'Login Incorrect!'
            break
            //const message = response.data
            //this.setLoginResponseData(type, responseMessage, true)
          }
          case 500: {
            errorMessage = 'Server Error!'
            //const message = response.data
            break
          }
          default: {
            errorMessage = 'Default Error!'
          }
        }
        this.setLoginResponseData('error', errorMessage, true)
      }
    },
  }
}
</script>

<style scoped>

</style>