<template>
  <div class="magic-form">
    <v-form>
      <v-text-field id="username"
                    name="username"
                    color="#F6D365"
                    prepend-icon="mdi-account"
                    v-model="userLogin"
                    label="Username"
                    :error-messages="usernameErrors"
                    required
      ></v-text-field>
      <v-text-field id="password"
                    name="password"
                    color="#F6D365"
                    prepend-icon="mdi-key"
                    type="password"
                    v-model="userPassword"
                    label="Password"
                    :error-messages="passwordErrors"
                    required
      ></v-text-field>
    </v-form>
    <v-btn class="magic-primary" @click="loginUser()" block>
      Log in
    </v-btn>
    <div class="magic-form-hint">Need an account? Create one on the right.</div>
  </div>
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
@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;600&display=swap');

.magic-form {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.9);
}

.magic-form ::v-deep .v-input input {
  color: #f7f4ff !important;
}

.magic-form ::v-deep .v-label,
.magic-form ::v-deep .v-icon,
.magic-form ::v-deep .v-messages__message {
  color: rgba(230, 225, 255, 0.75) !important;
}

.magic-form ::v-deep .v-input__slot {
  background: rgba(255, 255, 255, 0.04);
  border-radius: 12px;
  padding: 4px 10px;
}

.magic-form ::v-deep .v-input input {
  padding: 8px 4px !important;
}

.magic-primary {
  background: linear-gradient(120deg, #f6d365, #fda085) !important;
  color: #1b1833 !important;
  border-radius: 14px;
  font-weight: 600;
  text-transform: none;
}

.magic-form-hint {
  margin-top: 10px;
  font-size: 0.8rem;
  color: rgba(230, 225, 255, 0.65);
}
</style>
