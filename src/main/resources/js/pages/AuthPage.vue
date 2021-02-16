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
                      v-model:="login_response_alert"
                      dense
                      type="info"
                      outlined
                  >

                    KOOOOOS
                  </v-alert>

              </v-col>

            </v-row>





            <!--            -->
            <v-card-actions>
              <v-row>
                <v-col>
                  <v-form>
                    <v-text-field id="username"
                                  name="username"
                                  color="purple"
                                  prepend-icon="mdi-phone"
                                  v-model="usernameLogin"
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
                                  v-model="password"
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

                </v-col>

                <v-divider vertical/>

                <v-col>
                  <v-card-text>
                    <v-row justify="center">
                      <span>First time here?</span>
                    </v-row>
                  </v-card-text>

                  <!--                  -->
                  <v-card-actions>
                    <v-dialog v-model="registrationDialog" persistent max-width="500">
                      <template v-slot:activator="{ on }">
                        <v-btn color="purple" outlined block dark v-on="on" @click="registrationError = false">
                          <span>Sign up</span>
                        </v-btn>
                      </template>
                      <v-card>

                        <v-card-title class="headline">
                          <v-icon>
                            mdi-account-plus
                          </v-icon>
                          <span class="ml-3">Registration</span>
                        </v-card-title>
                        <v-card-text>Sign up for your account</v-card-text>

                        <v-form>
                          <v-text-field id="username"
                                        color="purple"
                                        label="Username"
                                        prepend-icon="mdi-user"
                                        v-model="username"
                                        :error-messages="usernameErrors"
                                        required
                          ></v-text-field>
<!--@input="$v.username.$touch()"
                                        @blur="$v.username.$touch()"                          -->
                          <v-row>
                            <v-col>
                              <v-text-field id="password"
                                            name="password"
                                            prepend-icon="mdi-key"
                                            type="password"
                                            v-model="password"
                                            label="Password"
                                            :error-messages="passwordErrors"
                                            required
                                            color="purple"
                              ></v-text-field>
<!--                                                                          @input="$v.password.$touch()"
                                            @blur="$v.password.$touch()"-->
                            </v-col>

                            <v-col>
                              <v-text-field id="passwordConfirm"
                                            name="passwordConfirm"
                                            type="password"
                                            v-model="registerPassConfirm"
                                            label="Confirm password"
                                            :error-messages="passwordConfirmErrors"
                                            color="purple"
                              ></v-text-field>
                            </v-col>

<!--       @input="$v.registerPassConfirm.$touch()"
                                            @blur="$v.registerPassConfirm.$touch()"                            -->
                          </v-row>

                          <v-row>
                            <v-col>
                              <v-text-field id="lastName"
                                            name="lastName"
                                            prepend-icon="mdi-account"
                                            type="text"
                                            v-model="lastName"
                                            label="Last Name"
                                            :error-messages="lastNameErrors"
                                            color="purple"
                              ></v-text-field>
<!--        @input="$v.lastName.$touch()"
                                            @blur="$v.lastName.$touch()"                              -->
                            </v-col>

                            <v-col>
                              <v-text-field id="firstName"
                                            name="firstName"
                                            type="text"
                                            v-model="firstName"
                                            label="First Name"
                                            :error-messages="firstNameErrors"
                                            color="purple"

                              ></v-text-field>
<!--                         @input="$v.firstName.$touch()"
                                            @blur="$v.firstName.$touch()"                          -->
                            </v-col>

                            <v-col>
                              <v-text-field id="patronymic"
                                            name="patronymic"
                                            type="text"
                                            v-model="patronymic"
                                            label="Middle Name"
                                            :error-messages="patronymicErrors"
                                            color="purple"
                              ></v-text-field>
<!--                                          @input="$v.patronymic.$touch()"
                                            @blur="$v.patronymic.$touch()"                              -->
                            </v-col>
                          </v-row>

                          <v-text-field id="email"
                                        name="email"
                                        prepend-icon="mdi-email"
                                        type="email"
                                        v-model="email"
                                        color="purple"
                                        label="E-mail"
                          ></v-text-field>
                        </v-form>

                        <v-card-actions>
                          <div class="flex-grow-1"></div>
                          <v-btn color="green darken-1" text @click="submitRegistration()">Sign up</v-btn>
                          <v-btn color="red darken-1" text @click="registrationDialog = false">Cancel</v-btn>
                        </v-card-actions>
                      </v-card>
                    </v-dialog>
                  </v-card-actions>
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

export default {
  components: {
    facebookLogin
  },
  data() {
    return {

      login_response_alert: ['login_response_alert'] ,
      registrationDialog: false,
      user: {},
      oldNameText: '',
      isConnected: false,
      name: '',
      email: '',
      personalID: '',
      picture: '',
      username: '',
      password: '',
      usernameLogin: '',
      loginIncorrect: false,
      patronymic: '',
      patronymicErrors: [],
      firstName: '',
      firstNameErrors:[],
      lastNameErrors:[],
      lastName:'',
      passwordConfirmErrors:[],
      registerPassConfirm:[],
      passwordErrors:[],
      usernameErrors:[],

    }
  },
  created() {
    //this.user = this.$store.state.currentUser
    //this.oldNameText = this.$store.state.currentUser.name
    //console.log(this.$store.state.currentUser)
  },
  computed: {
    has_login_response_alert() {
      return this.login_response_alert
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
    loginUser() {
      console.log('loginUser()')

      this.loginIncorrect = false

      //this.$v.$touch()
      //if (true/*this.loginValid*/) {

        let auth = new FormData();
        auth.set('username', this.usernameLogin);
        auth.set('password', this.password);
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
          this.setLoginResponse(response)
          //this.$store.dispatch('login')
        })
            .catch((error) => {
              console.log('loginIncorrect', error)
              this.loginIncorrect = true
            })
      //}

    },

    submitRegistration() {
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

    },

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
    setLoginResponse(response) {
      console.log('setLoginResponse', response)

      let responseMessage

      if (response) {
        responseMessage = response.data
      }
      else {
        responseMessage = 'Ошибка отправки'
      }

      this.login_response_alert = responseMessage

    }
  },
}
</script>

<style scoped>
</style>