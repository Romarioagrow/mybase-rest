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
                                        <v-btn color="blue" outlined @click="googleAuth()" height="300" width="300">
                                            Авторизация VK
                                        </v-btn>
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
    export default {
        data () {
            return {
                user: null,
                oldNameText: ''
            }
        },
        created() {
            this.user = this.$store.state.currentUser
            this.oldNameText = this.$store.state.currentUser.name
            console.log(this.$store.state.currentUser)
        },
        methods: {
            googleAuth() {
                this.$store.dispatch("doGoogleAuth")
            },
            instAuth() {
                let url = 'https://api.instagram.com/oauth/authorize?app_id=226365095211205&redirect_uri=localhost:8080/&scope=user_profile,user_media&response_type=code'
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