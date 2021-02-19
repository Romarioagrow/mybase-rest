<template>
  <v-content>
    <v-container fluid>
      <v-row>
        <v-col>
          <v-card>

            <v-row>
              <v-col cols="3">
                <v-card-title>
                  User Authorization
                </v-card-title>
              </v-col>

              <v-col>
                <v-alert
                    v-if="has_login_response_alert"
                    :type="get_login_response_type"
                    dense
                    outlined
                >
                  {{ login_response_alert.message }}
                </v-alert>

              </v-col>
            </v-row>

            <!--            -->
            <v-card-actions>
              <v-row>
                <v-col>

                  <user-login-form
                      :setLoginResponseData="setLoginResponseData"
                      :clearLoginResponse="clearLoginResponse"
                  ></user-login-form>

<!--                  <v-form>
                    <v-text-field id="username"
                                  name="username"
                                  color="purple"
                                  prepend-icon="mdi-phone"
                                  v-model="userLogin"
                                  label="Username"
                                  :error-messages="usernameErrors"
                                  required
                    ></v-text-field>
                    &lt;!&ndash;@input="$v.usernameLogin.$touch()"
                                                      @blur="$v.usernameLogin.$touch()"                    &ndash;&gt;
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
                    &lt;!&ndash;                         @input="$v.password.$touch()"
                                                      @blur="$v.password.$touch()" &ndash;&gt;
                  </v-form>
                  &lt;!&ndash; :disabled="!username || !password" block @click="loginUser()"                 &ndash;&gt;
                  <v-btn color="success" @click="loginUser()" block outlined>
                    <span>Log in</span>
                  </v-btn>-->
                </v-col>
                <v-divider vertical/>

                <v-col>
                  <user-registration-form></user-registration-form>
                </v-col>
              </v-row>
            </v-card-actions>
            <v-divider/>

            <v-card-actions>
              <!--v-if="!userAuth"-->
              <v-row>
                <v-col>
                  <v-btn color="blue" outlined @click="facebookAuth()" height="200" width="200">
                    Facebook Authorization
                  </v-btn>
                </v-col>
                <v-col>
                  <v-btn color="red" outlined @click="instAuth()" height="200" width="200">
                    Instagram Authorization
                  </v-btn>
                </v-col>
                <v-col>
                  <v-btn color="purple" outlined @click="googleAuth()" height="200" width="200">
                    Google Authorization
                  </v-btn>
                </v-col>
              </v-row>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>

      <!--AUTHORIZES STORE USERS-->
      <v-row>
        <!--GOOGLE-->
        <v-col v-if="googleUser" cols="3">
          <v-card width="300">
            <div>

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
            </div>
          </v-card>
        </v-col>

        <v-col v-if="instProfile" cols="3">
          <v-card width="300">
            <div>
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
            </div>
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
      return {}
    },
    googleUser() {
      return {}
      //return this.$store.state.currentUser
    },
    userAuth() {
      return {}
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
</style>