<template>
    <v-content>
        <v-container fluid>
            <v-row>
                <v-col>
                    <v-card height="850">
                        <div v-if="user">
                            <v-card-title>
                                <div>Hey, {{user.name}}</div>
                            </v-card-title>
                            <v-divider/>
                        </div>

                        <v-card-actions>
                            <v-btn outlined block color="primary" @click="googleAuth()" v-if="!userAuth">
                                Авторизация
                            </v-btn>
                            <v-btn outlined block color="primary" @click="logout()" v-else>
                                Logout
                            </v-btn>
                        </v-card-actions>

                    </v-card>
                </v-col>
            </v-row>


            <!--<v-row>
                <v-col>
                    <h1 v-if="user">Hey, {{user.name}}</h1>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-card>
                        <v-card-title>
                            Google Auth
                        </v-card-title>
                        <v-card-actions>
                            <v-btn outlined block color="primary" @click="googleAuth()" v-if="!userAuth">
                                Авторизация
                            </v-btn>
                            <v-btn outlined block color="primary" @click="logout()" v-else>
                                Logout
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-col>
            </v-row>-->
        </v-container>
    </v-content>
</template>

<script>
    import axios from "axios";
    export default {
        data () {
            return {
                user: null
            }
        },
        created() {
            this.user = this.$store.state.currentUser
        },
        methods: {
            googleAuth() {
                this.$store.dispatch("doGoogleAuth")
            },
            logout() {
                this.$store.dispatch("doLogout")
                this.user = null
            }
        },
        computed: {
            userAuth() {
                return this.$store.state.currentUser
            }
        }
    }
</script>

<style scoped>
</style>