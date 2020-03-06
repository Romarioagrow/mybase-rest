<template>
    <v-content>
        <v-container fluid>
            <v-row>
                <v-col>
                    <v-card height="850">
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
                                                class="w-25"
                                                :value="oldName"
                                                v-model="oldNameText"
                                        ></v-text-field>
                                        <v-btn outlined color="warning" @click="renameUser(user)">Rename</v-btn>
                                    </v-col>
                                </v-row>

                            </v-card-title>
                            <v-divider/>
                        </div>

                        <v-card-actions>
                            <div v-if="!userAuth">
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

                                        <facebook-login class="button"
                                                        appId="547193729487262"
                                                        @login="getUserData"
                                                        @logout="onLogout"
                                                        @get-initial-status="getUserData">
                                        </facebook-login>

                                        <v-btn @click="fbTest">fb test</v-btn>
                                    </v-col>
                                </v-row>
                            </div>

                            <v-btn outlined color="error" @click="logout()" v-else>
                                Logout
                            </v-btn>
                        </v-card-actions>

                    </v-card>
                </v-col>
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
                //FB: undefined
            }
        },
        created() {
            this.user = this.$store.state.currentUser
            this.oldNameText = this.$store.state.currentUser.name
            console.log(this.$store.state.currentUser)
        },
        methods: {
            async fbTest() {
                /// ОСНОВНОЙ АЛГОРИТМ ПОЛУЧЕНИЯ ДАННЫХ INSTAGRAM API
                /*
                * 1.X Авторизация на фейсбук
                * 2.X Получение фейсбук id и access-token
                * 3.X Получение инстаграм-business-id
                * 4. Получение информации о профиле и данные
                *
                * */

                'use strict'
                console.log('FB Test')

                 FB.getLoginStatus(function(response)
                 {
                    if (response.status === 'connected')
                    {
                        let uid = response.authResponse.userID;
                        let accessToken = response.authResponse.accessToken;
                        console.log('connected')
                        console.log('accessToken:' + accessToken)
                        console.log('uid:' + uid)

                        FB.api('/me/accounts', (response) =>
                        {
                            console.log(response)
                            let faceBookName = response.data[0].name
                            let access_token = response.data[0].access_token
                            let facebookID = response.data[0].id

                            console.log('faceBookName: ' + faceBookName);
                            console.log('facebookID: ' + facebookID);
                            console.log('access_token: ' + access_token);

                            let getInstagramID = '/' + facebookID + '?fields=instagram_business_account'
                            FB.api(getInstagramID, (response) =>
                            {
                                let instagramID = response.instagram_business_account.id
                                console.log('instagramID: ' + instagramID)



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
            },

            /*facebookAuth() {
                FB.getLoginStatus(function(response) {
                    statusChangeCallback(response);
                });
            },*/
            facebookAuth() {






                FB.login((response) =>
                {
                    if (response.authResponse) {
                        console.log('Facebook login successful');

                        FB.api('/me/accounts', (response) =>  {
                            console.log(response)
                            console.log('Facebook name: ' + response.data[0].name);


                            let access_tokenFacebook = response.data[0].access_token
                            console.log('access_token: ' + access_tokenFacebook);

                            //console.log(response.authResponse.accessToken)
                        });


                    }
                    else {
                        console.log('User cancelled login or did not fully authorize.');
                    }
                });


                /*FB.getLoginStatus(function(response) {
                    if (response.status === 'connected') {
                        // The user is logged in and has authenticated your
                        // app, and response.authResponse supplies
                        // the user's ID, a valid access token, a signed
                        // request, and the time the access token
                        // and signed request each expire.
                        var uid = response.authResponse.userID;
                        var accessToken = response.authResponse.accessToken;
                        console.log('connected')
                        console.log(accessToken)
                        console.log(uid)


                    } else if (response.status === 'not_authorized') {

                        FB.login(function(response) {
                            if (response.authResponse) {
                                console.log('Welcome!  Fetching your information.... ');
                                FB.api('/me', function(response) {
                                    console.log('Good to see you, ' + response.name + '.');
                                    console.log(response.authResponse.accessToken)
                                });

                            } else {
                                console.log('User cancelled login or did not fully authorize.');
                            }
                        });

                        // The user hasn't authorized your application.  They
                        // must click the Login button, or you must call FB.login
                        // in response to a user gesture, to launch a login dialog.
                    } else {
                        // The user isn't logged in to Facebook. You can launch a
                        // login dialog with a user gesture, but the user may have
                        // to log in to Facebook before authorizing your application.
                    }
                });*/


                /* FB.login(function(response) {
                     if (response.authResponse) {
                         console.log('Welcome!  Fetching your information.... ');
                         FB.api('/me', function(response) {
                             console.log('Good to see you, ' + response.name + '.');
                             console.log(response)
                         });

                         FB.getLoginStatus(function(response) {
                             if (response.status === 'connected') {
                                 var accessToken = response.authResponse.accessToken;
                             }
                         } );
                     } else {
                         console.log('User cancelled login or did not fully authorize.');
                     }
                 });*/

                /*FB.ui({
                    method: 'share_open_graph',
                    action_type: 'og.likes',
                    action_properties: JSON.stringify({
                        object:'https://developers.facebook.com/docs/javascript/examples',
                    })
                }, function(response){
                    // Debug response (optional)
                    console.log(response);
                });*/
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
            sdkLoaded(payload) {
                this.isConnected = payload.isConnected
                //FB = payload.FB
                if (this.isConnected) this.getUserData()
            },
            onLogin() {
                this.isConnected = true
                this.getUserData()
            },
            onLogout() {
                this.isConnected = false;
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
        computed: {
            userAuth() {
                return this.$store.state.currentUser
            },
            oldName() {
                return this.$store.state.currentUser.name
            }
        }
    }
</script>

<style scoped>
</style>