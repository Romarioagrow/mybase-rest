<template>
    <v-content>
        <v-container class="fill-height" fluid>
            <v-row>
                <v-col>
                    <v-card height="850">

                        <v-card-title>
                            <v-icon class="mr-3">mdi-instagram</v-icon>
                            <span>Instagram&nbsp;</span>
                            <span v-if="!loading">{{instAccount.username}}</span>
                        </v-card-title>


                        <!--PROFILE-->
                        <div v-if="!loading">
                            <v-card-text>
                                <v-row>
                                    <v-col cols="3">
                                        <v-img contain width="150" height="150" :src="instAccountPic"></v-img>
                                    </v-col>

                                    <v-col>

                                        <v-row>
                                            <v-col>
                                                <div>{{instAccount.fullName}}</div>
                                            </v-col>
                                        </v-row>

                                        <v-row>
                                            <v-col>
                                                <div>Followers: {{instAccount.followersAmount}} <v-icon>mdi-arrow-up</v-icon> </div>
                                            </v-col>
                                        </v-row>

                                        <v-row>
                                            <v-col>
                                                <div>Following: {{instAccount.followingAmount}} <v-icon>mdi-arrow-up</v-icon> </div>
                                            </v-col>
                                        </v-row>
                                    </v-col>

                                    <v-col>
                                        <div class="title">{{instAccount.biography}}</div>
                                    </v-col>
                                </v-row>
                            </v-card-text>
                        </div>
                        <div v-else class="mx-auto">
                            <v-card-actions>

                                <v-progress-circular
                                        style="margin-left: 45%"
                                        :size="70"
                                        :width="7"
                                        color="purple"
                                        indeterminate
                                ></v-progress-circular>
                            </v-card-actions>
                        </div>

                        <!--ВВОД-->
                        <v-card-actions>
                            <v-row>
                                <v-col>
                                    <v-text-field solo dense v-model="instUsername"></v-text-field>
                                </v-col>
                                <v-col>
                                    <v-btn block outlined>GO</v-btn>
                                </v-col>
                            </v-row>

                        </v-card-actions>

                        <v-card-actions>
                            <v-btn @click="instProfile">Inst Profile</v-btn>
                        </v-card-actions>

                        <v-card-actions>
                            <v-btn @click="instFollowers">Inst followers</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>
    </v-content>
</template>

<script>
    const config = {
        headers: {
            'Content-Type': 'application/json'
        }
    }
    import axios from "axios";
    export default {
        data() {
            return {
                instAccount: {},
                instUsername: 'romarioagrow',
                instAccountPic: '',
                loading: true
            }
        },
        created() {
            this.loadInstAccount()
        },
        methods: {
            loadInstAccount() {
                let instUsername = this.instUsername
                const uri = '/api/profile/instagram/loadInstProfile'

                /*axios.get(uri, {
                    params: {
                        'instUsername': instUsername
                    }
                })
                    .then(function (response) {
                        this.instAccount = response.data
                        console.log(this.instAccount)
                    })
                    .catch(function (error) {
                        console.log(error);
                    })
                    /!*.then(function () {
                        // always executed
                    });*!/*/


                axios.post(uri, instUsername, config).then(response => {
                    this.instAccount = response.data
                    this.instAccountPic = response.data.pic
                    this.loading = false
                    console.log(this.instAccount)
                })
            },
            instProfile() {
                axios.post('/api/data/instProfile').then(response => {
                    //this.loadSpendingData(response.data)
                })
            },
            instFollowers() {
                axios.post('/api/data/instFollowers').then(response => {
                    //this.loadSpendingData(response.data)
                })
            }
        }
    }
</script>

<style scoped>
</style>