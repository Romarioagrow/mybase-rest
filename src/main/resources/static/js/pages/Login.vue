<template>
    <v-content>
        <v-container fluid>
            <v-row>
                <v-col>
                    <v-card>
                        <v-card-actions>
                            <div > <!--v-if="!userAuth"-->
                                <v-card-title>
                                    User Authorization
                                </v-card-title>
                                <v-row >
                                    <v-col>
                                        <v-btn color="primary" outlined @click="googleAuth()" height="300" width="300">
                                            Авторизация Google
                                        </v-btn>
                                    </v-col>
                                    <v-col>
                                        <v-btn color="red" outlined @click="instAuth()" height="300" width="300">
                                            Авторизация Instagram
                                        </v-btn>
                                    </v-col>
                                    <v-col>
                                        <v-btn color="blue" outlined @click="facebookAuth()" height="300" width="300">
                                            Авторизация Facebook
                                        </v-btn>
                                        <!--<facebook-login class="button"
                                                        appId="547193729487262"
                                                        @login="getUserData"
                                                        @logout="onLogout"
                                                        @get-initial-status="getUserData">
                                        </facebook-login>-->
                                        <!--<v-btn @click="fbTest">fb test</v-btn>-->
                                    </v-col>
                                </v-row>
                            </div>
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
                                <div>{{user.name}}</div>
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
                                <div>{{instProfile.name}}</div>
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

                <!--<v-card>
                    <div v-if="user">
                        <v-card-title>
                            <v-row>
                                <v-col cols="3">
                                    <v-avatar class="profile" color="grey" size="164" tile>
                                        <v-img :src="user.user_pic"></v-img>
                                    </v-avatar>
                                </v-col>
                                <v-col>
                                    <div>Hey, {{user.name}}</div>
                                    <v-text-field
                                            label="Username"
                                            solo
                                            disabled
                                            class="w-25"
                                            :value="oldName"
                                            v-model="oldNameText"
                                    ></v-text-field>
                                    <v-btn outlined disabled color="warning" @click="renameUser(user)">Rename</v-btn>
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col>
                                    <v-btn outlined block color="error" @click="logout()"> &lt;!&ndash; v-else&ndash;&gt;
                                        Logout
                                    </v-btn>
                                </v-col>
                            </v-row>
                        </v-card-title>
                        <v-divider/>
                    </div>
                </v-card>-->
            </v-row>
        </v-container>
    </v-content>
</template>

<script>
    import axios from "axios";
    import facebookLogin from 'facebook-login-vuejs';
    export default {
        components: {
            facebookLogin
        },
        data () {
            return {
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
            },
            oldName() {
                return this.$store.state.currentUser.name
            }
        },
        methods: {
            async fbTest() {
            },

            facebookAuth() {

                /// ОСНОВНОЙ АЛГОРИТМ ПОЛУЧЕНИЯ ДАННЫХ INSTAGRAM API
                /*
                * 1.X Авторизация на фейсбук
                * 2.X Получение фейсбук id и access-token
                * 3.X Получение инстаграм-business-id
                * 4.X Получение информации о профиле и базовые данные
                * 5. Получение постов и медиа
                * 6. Получение охватов и инсайдов
                * 7. Получение стори и хайлайтс
                * 8. ПОЛУЧЕНИЕ СПИСКА ПОДПИСЧИКОВ!!!
                *
                * */

                FB.login((response) =>
                {
                    if (response.authResponse) {
                        console.log('Facebook login successful');

                        FB.api('/me/accounts', (response) =>  {
                            console.log(response)
                            console.log('Facebook name: ' + response.data[0].name);
                            let access_tokenFacebook = response.data[0].access_token
                        });

                        FB.getLoginStatus((response) => {

                            if (response.status === 'connected')
                            {
                                let uid = response.authResponse.userID;
                                let accessToken = response.authResponse.accessToken;
                                console.log('connected')
                                console.log('accessToken:' + accessToken)
                                console.log('uid:' + uid)

                                /*!!!!!ВСЕ В БЭК!!!!!*/
                                /*getFB_AccountData*/
                                FB.api('/me/accounts', (response) =>
                                {
                                    console.log(response)
                                    let faceBookName = response.data[0].name
                                    let access_token = response.data[0].access_token
                                    let facebookID = response.data[0].id

                                    console.log('faceBookName: ' + faceBookName);
                                    console.log('facebookID: ' + facebookID);
                                    console.log('access_token: ' + access_token);

                                    /*get instID*/
                                    let getInstagramID = '/' + facebookID + '?fields=instagram_business_account'
                                    FB.api(getInstagramID, (response) =>
                                    {
                                        let instagramID = response.instagram_business_account.id
                                        console.log('instagramID: ' + instagramID)

                                        /*get Object INST_USER*/
                                        let apiURL = instagramID + '?fields=biography,id,ig_id,followers_count,follows_count,media_count,name,profile_picture_url,username,website'
                                        FB.api(apiURL, (instUser) => {
                                            console.log(instUser)
                                            this.$store.dispatch('loadInstUserProfile', instUser) ///To Storage
                                        })

                                        let apiURLNewFollowers = instagramID + '/insights?pretty=0&since=1580515200&until=1583020800&metric=follower_count&period=day'
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
                                        })
                                    })
                                });
                            }
                            else if (response.status === 'not_authorized') {
                                console.log('not_authorized')
                            }
                            else {
                                console.log('no fb data (else)')
                            }
                        });
                    }
                    else {
                        console.log('User cancelled login or did not fully authorize.');
                    }
                });
            },


            getUserData() {
                FB.api('/me', 'GET', { fields: 'id,name,email,picture' },
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