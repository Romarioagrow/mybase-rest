<template>
  <v-content class="magic-auth-stage">
    <div class="magic-auth-sky"></div>
    <v-container fluid class="magic-auth-wrap">
      <v-row align="center" class="magic-auth-hero">
        <v-col cols="12" md="5" class="magic-auth-copy">
          <div class="magic-auth-kicker">MyBase / Access Portal</div>
          <h1 class="magic-auth-title">
            Enter the studio
            <span class="magic-auth-glow">and shape your signal.</span>
          </h1>
          <p class="magic-auth-subtitle">
            Login to continue your rituals, or create a new identity and let your
            analytics bloom.
          </p>
          <div class="magic-auth-orb">
            <div class="orb-core"></div>
            <div class="orb-ring"></div>
            <div class="orb-ring ring-two"></div>
          </div>
        </v-col>
        <v-col cols="12" md="7">
          <v-card class="magic-auth-card" elevation="12">
            <v-card-title class="magic-auth-card-title">
              User Access
              <v-spacer/>
              <v-alert
                  v-if="has_login_response_alert"
                  :type="get_login_response_type"
                  dense
                  outlined
                  class="magic-auth-alert"
              >
                {{ login_response_alert.message }}
              </v-alert>
            </v-card-title>

            <v-card-text>
              <v-row>
                <v-col cols="12">
                  <div class="magic-toggle">
                    <v-btn class="magic-toggle-btn" :outlined="!showLogin" @click="showLogin = true">
                      Sign in
                    </v-btn>
                    <v-btn class="magic-toggle-btn" :outlined="showLogin" @click="showLogin = false">
                      Create account
                    </v-btn>
                  </div>
                </v-col>
                <v-col cols="12" v-if="showLogin">
                  <div class="magic-form-title">Sign in</div>
                  <user-login-form
                      :setLoginResponseData="setLoginResponseData"
                      :clearLoginResponse="clearLoginResponse"
                  ></user-login-form>
                </v-col>
                <v-col cols="12" v-else>
                  <div class="magic-form-title">Create account</div>
                  <user-registration-form></user-registration-form>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>

          <v-card class="magic-social-card" elevation="10">
            <div class="magic-social-title">Social gateways</div>
            <v-row>
              <v-col cols="12" md="4">
                <v-btn class="magic-social-btn facebook" block @click="facebookAuth()">
                  <v-icon left>mdi-facebook</v-icon>
                  Facebook
                </v-btn>
              </v-col>
              <v-col cols="12" md="4">
                <v-btn class="magic-social-btn instagram" block @click="instAuth()">
                  <v-icon left>mdi-instagram</v-icon>
                  Instagram
                </v-btn>
              </v-col>
              <v-col cols="12" md="4">
                <v-btn class="magic-social-btn google" block @click="googleAuth()">
                  <v-icon left>mdi-google</v-icon>
                  Google
                </v-btn>
              </v-col>
            </v-row>
          </v-card>
        </v-col>
      </v-row>

      <!--AUTHORIZES STORE USERS-->
      <v-row>
        <!--GOOGLE-->
        <v-col v-if="googleUser" cols="3">
          <v-card width="300" disabled>
            <v-card-title>
              <div class="regular-1">Google</div>
              <!--                <v-img contain :src="user.user_pic"></v-img>-->
            </v-card-title>
            <v-card-subtitle>
              <!--                <div>{{ user.name }}</div>-->
            </v-card-subtitle>
            <v-card-actions>
              <v-btn disabled small block color="error" @click="logout()">
                Logout
              </v-btn>
            </v-card-actions>
            <v-divider/>
          </v-card>
        </v-col>

        <v-col v-if="instProfile" cols="3">
          <v-card width="300" disabled>
            <v-card-title>
              <div class="regular-1">Instagram Graph API</div>
              <v-img contain :src="instProfile.profile_picture_url"></v-img>
            </v-card-title>
            <v-card-subtitle>
              <div>{{ instProfile.name }}</div>
            </v-card-subtitle>
            <v-card-actions>
              <v-btn disabled small block color="error" @click="logoutInstProfile()">
                Logout
              </v-btn>
            </v-card-actions>
            <v-divider/>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-content>
</template>

<script>
import facebookLogin from 'facebook-login-vuejs';
import axios from "axios";
import UserRegistrationForm from "components/UserRegistrationForm.vue";
import UserLoginForm from "components/UserLoginForm.vue";

export default {
  components: {
    UserLoginForm,
    UserRegistrationForm,
    facebookLogin
  },
  data() {
    return {
      showLogin: true,
      login_response_alert: {
        has_response: false,
        type: '',
        message: ''
      },
      user: {},
      oldNameText: '',
      isConnected: false,
      name: '',
      personalID: '',
      picture: '',
//      userPassword: '',
//      userLogin: '',
      loginIncorrect: false,
//      passwordErrors:[],
//      usernameErrors:[],

    }
  },
  created() {
    //this.user = this.$store.state.currentUser
    //this.oldNameText = this.$store.state.currentUser.name
    //console.log(this.$store.state.currentUser)
  },
  computed: {
    get_login_response_type() {
      return this.login_response_alert.type
    },

    has_login_response_alert() {
      return this.login_response_alert.has_response
    },
    instProfile() {
      // return this.$store.state.instProfile
      return null
    },
    googleUser() {
      return null
      //return this.$store.state.currentUser
    },
    userAuth() {
      return null
      //return this.$store.state.currentUser
    }
  },
  methods: {
    /*loginUser() {
      console.log('loginUser()')
      this.clearLoginResponse()

      // this.loginIncorrect = false

      //this.$v.$touch()
      //if (true/!*this.loginValid*!/) {

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

    },*/

    /* submitRegistration() {
       const sendURL = '/api/user/auth/registration'

       console.log('submitRegistration()')

       let userCredits = {
         username: this.username,
         password: this.password,
         email: this.email
       }

       axios.post(sendURL, userCredits).then(response => {
         console.log('response', response);
         this.user = response.data
       })

     },*/

    logoutInstProfile() {
      this.$store.dispatch('doLogout')
    },
    facebookAuth() {

      FB.login((response) => {
        if (response.authResponse) {
          console.log('Facebook login successful');

          FB.api('/me/accounts', (response) => {
            console.log(response)
            console.log('Facebook name: ' + response.data[0].name);
            let access_tokenFacebook = response.data[0].access_token
          });

          FB.getLoginStatus((response) => {

            if (response.status === 'connected') {
              let uid = response.authResponse.userID;
              let accessToken = response.authResponse.accessToken;
              console.log('connected')
              console.log('accessToken:' + accessToken)
              console.log('uid:' + uid)

              /*В БЭК!*/
              /*getFB_AccountData*/
              FB.api('/me/accounts', (response) => {
                console.log(response)
                let faceBookName = response.data[0].name
                let access_token = response.data[0].access_token
                let facebookID = response.data[0].id

                console.log('faceBookName: ' + faceBookName);
                console.log('facebookID: ' + facebookID);
                console.log('access_token: ' + access_token);

                /*get instID*/
                let getInstagramID = '/' + facebookID + '?fields=instagram_business_account'
                FB.api(getInstagramID, (response) => {
                  let instagramID = response.instagram_business_account.id
                  console.log('instagramID: ' + instagramID)

                  /*get Object INST_USER*/
                  let apiURL = instagramID + '?fields=biography,id,ig_id,followers_count,follows_count,media_count,name,profile_picture_url,username,website'
                  FB.api(apiURL, (instUser) => {
                    //console.log(instUser)
                    this.$store.dispatch('loadInstUserProfile', instUser)
                    window.location.href = 'https://localhost:8080/login'///To Storage
                  })

                  /*let apiURLNewFollowers = instagramID + '/insights?pretty=0&since=1580515200&until=1583020800&metric=follower_count&period=day'
                  FB.api(apiURLNewFollowers, (response) => {
                      let followersObject = response.data[0].values
                      let nextPage = response.paging.next
                      console.log(followersObject)
                      console.log(nextPage)
                      let newFollowersData = new Map()
                      followersObject.forEach((arrayItem) => {
                          if (arrayItem.value!== 0) {
                              console.log(arrayItem.end_time + ': ' + arrayItem.value)
                              newFollowersData.set(arrayItem.end_time, arrayItem.value)
                          }
                      });
                      console.log(newFollowersData)
                      this.$store.dispatch('newFollowersData', newFollowersData)
                  })*/
                })
              });
            } else if (response.status === 'not_authorized') {
              console.log('not_authorized')
            } else {
              console.log('no fb data (else)')
            }
          });
        } else {
          console.log('User cancelled login or did not fully authorize.');
        }
      });
    },


    getUserData() {
      FB.api('/me', 'GET', {fields: 'id,name,email,picture'},
          user => {
            this.personalID = user.id;
            this.email = user.email;
            this.name = user.name;
            this.picture = user.picture.data.url;
          }
      )
    },

    googleAuth() {
      this.$store.dispatch("doGoogleAuth")
    },
    instAuth() {
      let url = 'https://api.instagram.com/oauth/authorize?app_id=226365095211205&redirect_uri=https://localhost:8080/&scope=user_profile,user_media&response_type=code'
      window.location.href = url
    },
    logout() {
      this.$store.dispatch("doLogout")
      this.user = {}
    },
    renameUser(user) {
      let newName = this.oldNameText
      console.log(newName)
    },
    /*handleSuccessfulLoginResponse(response) {
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
    },*/
    /*handleErrorLoginResponse(error) {
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

    },*/
    clearLoginResponse() {
      this.setLoginResponseData('', '', false)
      /*this.login_response_alert.has_response = false
      this.login_response_alert.type = 'info'
      this.login_response_alert.message = ''*/
    },
    setLoginResponseData(type, message, hasResponse) {
      console.log('setLoginResponseData')
      this.login_response_alert.has_response = hasResponse
      this.login_response_alert.type = type
      this.login_response_alert.message = message
    }
  },
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600&family=Space+Grotesk:wght@300;400;600&display=swap');

.magic-auth-stage {
  min-height: calc(100vh - 64px);
  position: relative;
  overflow: hidden;
  background: radial-gradient(1200px 800px at 15% 10%, #2a1a47 0%, #0f1027 45%, #090b15 100%);
}

.magic-auth-sky {
  position: absolute;
  inset: -20% -10%;
  background:
    radial-gradient(2px 2px at 12% 20%, rgba(255,255,255,0.45) 40%, transparent 60%),
    radial-gradient(2px 2px at 78% 30%, rgba(255,255,255,0.35) 40%, transparent 60%),
    radial-gradient(1px 1px at 55% 80%, rgba(255,255,255,0.5) 40%, transparent 60%);
  opacity: 0.5;
  animation: drift 34s linear infinite;
}

.magic-auth-wrap {
  position: relative;
  z-index: 2;
  padding-top: 48px;
  padding-bottom: 64px;
}

.magic-auth-kicker {
  font-family: "Space Grotesk", sans-serif;
  letter-spacing: 0.24em;
  text-transform: uppercase;
  font-size: 0.75rem;
  color: #b9b5ff;
  margin-bottom: 12px;
}

.magic-auth-title {
  font-family: "Cinzel", serif;
  font-size: clamp(2.2rem, 4vw, 3.4rem);
  line-height: 1.1;
  color: #f7f4ff;
  margin: 0 0 16px 0;
}

.magic-auth-glow {
  display: block;
  color: #f6d365;
  text-shadow: 0 0 18px rgba(246, 211, 101, 0.45);
}

.magic-auth-subtitle {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.85);
  font-size: 1.05rem;
  max-width: 420px;
}

.magic-auth-card {
  background: rgba(12, 14, 30, 0.88) !important;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 18px;
  box-shadow: 0 24px 48px rgba(8, 9, 20, 0.6);
}

.magic-auth-card-title {
  font-family: "Cinzel", serif;
  color: #f7f4ff;
}

.magic-auth-alert {
  margin-left: 16px;
  min-width: 180px;
}

.magic-form-title {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.75);
  text-transform: uppercase;
  letter-spacing: 0.2em;
  font-size: 0.7rem;
  margin-bottom: 12px;
}

.magic-toggle {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.magic-toggle-btn {
  border-radius: 999px;
  text-transform: none;
  font-weight: 600;
  color: #f7f4ff !important;
  border: 1px solid rgba(255,255,255,0.16);
  background: rgba(255,255,255,0.06) !important;
}

.magic-social-card {
  margin-top: 18px;
  padding: 16px 18px 8px 18px;
  background: rgba(12, 14, 30, 0.75) !important;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
}

.magic-social-title {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.7);
  text-transform: uppercase;
  letter-spacing: 0.2em;
  font-size: 0.7rem;
  margin-bottom: 12px;
}

.magic-social-btn {
  text-transform: none;
  border-radius: 14px;
  font-weight: 600;
  color: #f7f4ff !important;
  border: 1px solid rgba(255,255,255,0.08);
  background: rgba(255,255,255,0.05) !important;
}

.magic-social-btn.facebook { background: rgba(59, 89, 152, 0.25) !important; }
.magic-social-btn.instagram { background: rgba(214, 41, 118, 0.2) !important; }
.magic-social-btn.google { background: rgba(66, 133, 244, 0.2) !important; }

.magic-auth-orb {
  margin-top: 24px;
  width: 180px;
  height: 180px;
  position: relative;
}

.orb-core {
  position: absolute;
  inset: 32px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(246,211,101,0.9), rgba(246,211,101,0.1));
  box-shadow: 0 0 28px rgba(246,211,101,0.6);
}

.orb-ring {
  position: absolute;
  inset: 8px;
  border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.12);
  animation: spin 18s linear infinite;
}

.ring-two {
  inset: 20px;
  animation-duration: 26s;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes drift {
  0% { transform: translate3d(0, 0, 0); }
  50% { transform: translate3d(4%, -2%, 0); }
  100% { transform: translate3d(0, 0, 0); }
}

@media (max-width: 960px) {
  .magic-auth-wrap { padding-top: 32px; }
  .magic-auth-orb { margin-bottom: 24px; }
}
</style>
