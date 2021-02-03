<template>
  <v-content>
    <v-container fluid>
      <v-row>
        <v-col>
          <v-card>
            <v-card-title>
              User Authorization
            </v-card-title>


            <v-card-actions>

              <v-row>

                <v-col>
                  <v-form>
                    <v-text-field id="username"
                                  name="username"
                                  color="purple"
                                  prepend-icon="mdi-phone"
                                  type="text"
                                  v-mask="'7-###-###-##-##'"
                                  v-model="username"
                                  label="Номер телефона +7"
                                  :error-messages="usernameErrors"
                                  required
                                  @input="$v.username.$touch()"
                                  @blur="$v.username.$touch()"
                    ></v-text-field>
                    <v-text-field id="password"
                                  name="password"
                                  color="purple"
                                  prepend-icon="mdi-key"
                                  type="password"
                                  v-model="password"
                                  label="Пароль"
                                  :error-messages="passwordErrors"
                                  required
                                  @input="$v.password.$touch()"
                                  @blur="$v.password.$touch()"
                    ></v-text-field>
                  </v-form>
                </v-col>

                <v-divider vertical/>

                <v-col>
                  <v-card-text>
                    <v-row justify="center">
                      Зашли впервые?
                    </v-row>
                  </v-card-text>
                  <v-card-actions>
                    <v-dialog v-model="registrationDialog" persistent max-width="500">
                      <template v-slot:activator="{ on }">
                        <v-btn color="purple" outlined block dark v-on="on" @click="registrationError = false">
                          Зарегистрируйтесь
                        </v-btn>
                      </template>
                      <v-card>

                        <v-card-title class="headline">
                          <v-icon>
                            mdi-account-plus
                          </v-icon>
                          <span class="ml-3">Регистрация</span>
                        </v-card-title>
                        <v-card-text>Введите ваши контактные данные</v-card-text>
                        <v-card-text>
                          <v-form>
                            <v-text-field id="username"
                                          color="purple"
                                          label="Номер телефона"
                                          name="username"
                                          prepend-icon="mdi-phone"
                                          type="text"
                                          v-mask="'7-###-###-##-##'"
                                          v-model="username"
                                          :error-messages="usernameErrors"
                                          required
                                          @input="$v.username.$touch()"
                                          @blur="$v.username.$touch()"
                            ></v-text-field>

                            <v-row>
                              <v-col>
                                <v-text-field id="password"
                                              name="password"
                                              prepend-icon="mdi-key"
                                              type="password"
                                              v-model="password"
                                              label="Пароль"
                                              :error-messages="passwordErrors"
                                              required
                                              color="purple"
                                              @input="$v.password.$touch()"
                                              @blur="$v.password.$touch()"
                                ></v-text-field>
                              </v-col>

                              <v-col>
                                <v-text-field id="passwordConfirm"
                                              name="passwordConfirm"
                                              type="password"
                                              v-model="registerPassConfirm"
                                              label="Подтвердите пароль"
                                              :error-messages="passwordConfirmErrors"
                                              required
                                              color="purple"
                                              @input="$v.registerPassConfirm.$touch()"
                                              @blur="$v.registerPassConfirm.$touch()"
                                ></v-text-field>
                              </v-col>
                            </v-row>

                            <v-row>
                              <v-col>
                                <v-text-field id="lastName"
                                              name="lastName"
                                              prepend-icon="mdi-account"
                                              type="text"
                                              v-model="lastName"
                                              label="Фамилия"
                                              :error-messages="lastNameErrors"
                                              required
                                              color="purple"
                                              @input="$v.lastName.$touch()"
                                              @blur="$v.lastName.$touch()"
                                ></v-text-field>
                              </v-col>

                              <v-col>
                                <v-text-field id="firstName"
                                              name="firstName"
                                              type="text"
                                              v-model="firstName"
                                              label="Имя"
                                              :error-messages="firstNameErrors"
                                              required
                                              color="purple"
                                              @input="$v.firstName.$touch()"
                                              @blur="$v.firstName.$touch()"
                                ></v-text-field>
                              </v-col>

                              <v-col>
                                <v-text-field id="patronymic"
                                              name="patronymic"
                                              type="text"
                                              v-model="patronymic"
                                              label="Отчество"
                                              :error-messages="patronymicErrors"
                                              required
                                              color="purple"
                                              @input="$v.patronymic.$touch()"
                                              @blur="$v.patronymic.$touch()"
                                ></v-text-field>
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
                        </v-card-text>
                        <v-card-actions>
                          <div class="flex-grow-1"></div>
                          <v-btn color="green darken-1" text @click="submitRegistration()">Регистрация</v-btn>
                          <v-btn color="red darken-1" text @click="registrationDialog = false">Отмена</v-btn>
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
                  <v-btn disabled color="blue" outlined @click="facebookAuth()" height="200" width="200">
                    Авторизация Facebook
                  </v-btn>
                </v-col>
                <v-col>
                  <v-btn disabled color="red" outlined @click="instAuth()" height="200" width="200">
                    Авторизация Instagram
                  </v-btn>
                </v-col>
                <v-col>
                  <v-btn disabled color="primary" outlined @click="googleAuth()" height="200" width="200">
                    Авторизация Google
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
                <v-img contain :src="user.user_pic"></v-img>
              </v-card-title>
              <v-card-subtitle>
                <div>{{ user.name }}</div>
              </v-card-subtitle>
              <v-card-actions>
                <v-btn small block color="error" @click="logout()">
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
                <v-btn small block color="error" @click="logoutInstProfile()">
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

export default {
  components: {
    facebookLogin
  },
  data() {
    return {
      registrationDialog: false,
      user: null,
      oldNameText: '',
      isConnected: false,
      name: '',
      email: '',
      personalID: '',
      picture: '',
    }
  },
  created() {
    this.user = this.$store.state.currentUser
    this.oldNameText = this.$store.state.currentUser.name
    console.log(this.$store.state.currentUser)
  },
  computed: {
    instProfile() {
      return this.$store.state.instProfile
    },
    googleUser() {
      return this.$store.state.currentUser
    },
    userAuth() {
      return this.$store.state.currentUser
    }
  },
  methods: {
    submitRegistration() {
      console.log('submitRegistration()')
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
      this.user = null
    },
    renameUser(user) {
      let newName = this.oldNameText
      console.log(newName)
    }
  },
}
</script>

<style scoped>
</style>