<template>
    <v-content>
        <v-container class="fill-height" fluid>
            <v-row>
                <v-col>
                    <v-card wight="850">
                        <!--ВВОД-->
                        <v-card-title>
                            Facebook Graph API
                        </v-card-title>

                        <v-card-title>
                            <v-icon class="mr-3">mdi-instagram</v-icon>
                            <span>Instagram&nbsp;</span>
                            <span>{{instProfile.username}}</span>
                        </v-card-title>
                        <v-card-actions v-if="instProfile">
                            <v-row>
                                <v-col cols="3">
                                    <v-card width="150" height="150" @click.stop="dialog = true" hover>
                                        <v-img contain :src="instProfile.profile_picture_url"></v-img>
                                    </v-card>
                                    <v-dialog v-model="dialog" max-width="800">
                                        <v-card>
                                            <v-img contain :src="instProfile.profile_picture_url"></v-img>
                                            <v-card-actions>
                                                <v-btn color="error" block outlined @click="dialog = false">
                                                    Save
                                                </v-btn>
                                            </v-card-actions>
                                        </v-card>
                                    </v-dialog>
                                </v-col>
                                <v-col cols="4">
                                    <v-row>
                                        <v-col>
                                            <div class="headline font-weight-thin">{{instProfile.name}}</div>
                                        </v-col>
                                    </v-row>
                                    <v-row>
                                        <v-col>
                                            <div>Followers: {{instProfile.followers_count}}</div>
                                        </v-col>
                                    </v-row>
                                    <v-row>
                                        <v-col>
                                            <div>Following: {{instProfile.follows_count}}</div>
                                        </v-col>
                                    </v-row>
                                </v-col>
                                <v-col>
                                    <v-row>
                                        <v-col>
                                            <div class="title font-weight-light">{{instProfile.biography}}</div>
                                        </v-col>
                                    </v-row>
                                    <v-row>
                                        <v-col>
                                            <v-btn outlined small>{{instProfile.website}}</v-btn>
                                            <!--<div class="subtitle-2">{{instProfile.website}}</div>-->
                                        </v-col>
                                    </v-row>
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col>
                                    <v-btn @click.stop="followersDialog = true" hover>
                                        Followers info
                                    </v-btn>
                                    <v-dialog v-model="followersDialog" max-width="800">
                                        <v-card>
                                            <v-card-actions>
                                                <v-btn color="error" block outlined @click="followersDialog = false">
                                                    Save
                                                </v-btn>
                                            </v-card-actions>
                                        </v-card>
                                    </v-dialog>
                                </v-col>
                            </v-row>
                        </v-card-actions>
                        <v-divider/>
                        <v-card-actions>
                            <v-card-subtitle>
                                Insights
                            </v-card-subtitle>
                            <!--<v-card-actions>
                                <div>New followers: +<b>{{newFollowersDataAmount}}</b> (For February)</div>
                            </v-card-actions>-->
                        </v-card-actions>
                        <v-divider/>
                        <v-card-actions>
                            <v-card-subtitle>
                                Stories
                            </v-card-subtitle>
                        </v-card-actions>
                        <v-divider/>
                        <v-card-actions>
                            <v-card-subtitle>
                                Highlights
                            </v-card-subtitle>
                        </v-card-actions>
                        <v-divider/>
                        <v-card-actions>
                            <v-card-subtitle>
                                Posts
                            </v-card-subtitle>
                        </v-card-actions>
                    </v-card>
                </v-col>
            </v-row>

            <v-spacer></v-spacer>

            <!--OPEN API-->
            <v-row>
                <v-col>
                    <v-card min-height="850">
                        <v-card-title>Instagram Open API</v-card-title>
                        <v-card-actions>
                            <v-row>
                                <v-col>
                                    <v-text-field solo dense v-model="scrapperInstUsername"></v-text-field>
                                </v-col>
                                <v-col>
                                    <v-btn block color="error" @click="loadInstDataFreeAPI()">GO</v-btn>
                                </v-col>
                            </v-row>
                        </v-card-actions>
                        <v-divider></v-divider>
                        <v-card-title v-if="!loading">
                            <span >{{scrapperInstAccount.username}}</span>
                        </v-card-title>
                        <!--PROFILE-->
                        <div v-if="!loading">
                            <v-card-text>
                                <v-row>
                                    <v-col cols="3">
                                        <v-card width="150" height="150" @click.stop="dialog = true" hover>
                                            <v-img contain :src="instAccountPicFull"></v-img>
                                        </v-card>
                                        <v-dialog v-model="dialog" max-width="800">
                                            <v-card>
                                                <v-img contain :src="instAccountPicFull"></v-img>
                                                <v-card-actions>
                                                    <v-btn color="error" block outlined @click="dialog = false">
                                                        Save
                                                    </v-btn>
                                                </v-card-actions>
                                            </v-card>
                                        </v-dialog>
                                    </v-col>
                                    <v-col cols="4">
                                        <v-row>
                                            <v-col>
                                                <div class="headline font-weight-thin">{{scrapperInstAccount.fullName}}</div>
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col >
                                                <v-btn :disabled="false" outlined>Followers:
                                                    {{scrapperInstAccount.followedBy}}
                                                    <v-icon v-if="!followsLoading">mdi-arrow-up</v-icon>
                                                </v-btn>
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col>
                                                <v-btn :disabled="false" outlined>Following:
                                                    {{scrapperInstAccount.follows}}
                                                    <v-icon v-if="!followsLoading">mdi-arrow-up</v-icon>
                                                </v-btn>
                                            </v-col>
                                        </v-row>
                                    </v-col>
                                    <v-col>
                                        <v-row>
                                            <v-col>
                                                <div class="title font-weight-light">{{scrapperInstAccount.biography}}</div>
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col>
                                                <div class="subtitle-2">{{scrapperInstAccount.externalUrl}}</div>
                                            </v-col>
                                        </v-row>
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
                        <v-divider></v-divider>
                        <!--POSTS-->
                        <v-container>
                            <v-row>
                                <v-col>Всего постов: {{totalPosts}} </v-col>
                            </v-row>
                            <v-row>
                                <v-col v-for="instPost in instPosts" :key="instPost.id">
                                    <v-card class="d-inline-block mx-auto">
                                        <v-container>
                                            <v-row>
                                                <v-col>
                                                    <v-img height="200" width="200" :src="instPost.displayUrl"></v-img>
                                                    <!--<v-img contain :src="instAccountPicFull"></v-img>-->
                                                    <!-- <v-img height="200" width="200" :src="instPost.displayUrl" @click="postDialog = true"></v-img>-->
                                                    <!--<v-dialog v-model="postDialog" max-width="800">
                                                        <v-card>
                                                            <v-img contain :src="instPost.displayUrl"></v-img>
                                                            <v-card-actions>
                                                                <v-btn color="error" block outlined @click="postDialog = false">
                                                                    Save
                                                                </v-btn>
                                                            </v-card-actions>
                                                        </v-card>
                                                    </v-dialog>-->
                                                </v-col>
                                                <v-col>
                                                    <v-row>
                                                        <v-col>
                                                            <v-btn icon>
                                                                <v-icon>mdi-heart</v-icon>
                                                                {{instPost.likeCount}}
                                                            </v-btn>
                                                        </v-col>
                                                        <v-col>
                                                            <v-btn icon>
                                                                <v-icon>mdi-bookmark</v-icon>
                                                            </v-btn>
                                                        </v-col>
                                                        <v-col>
                                                            <v-btn icon>
                                                                <v-icon>mdi-share-variant</v-icon>
                                                            </v-btn>
                                                        </v-col>
                                                    </v-row>
                                                </v-col>
                                            </v-row>
                                        </v-container>
                                    </v-card>
                                </v-col>
                            </v-row>
                        </v-container>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>
    </v-content>
</template>

<script>
    const configJson = {
        headers: {
            'Content-Type': 'application/json'
        }
    }
    import axios from "axios";
    const api = axios.create({
        withCredentials: true
    });
    export default {
        data() {
            return {
                scrapperInstAccount: {},
                instUsername: 'romarioagrow',
                scrapperInstUsername: 'romarioagrow',
                sessionid: 'sessionid=1038252798%3AAmldaSBRLWz8tw%3A3;',
                instAccountPic: '',
                instAccountPicFull: '',
                loading: true,
                instPosts: [],
                dialog: false,
                postsLoading: true,
                totalPosts: '',
                followers: [],
                following: [],
                followsLoading: true,
                postDialog: false,
                followersDialog: false
            }
        },
        created() {
            //this.loadInstDataFreeAPI()
            this.loadFollowersAndFollowsLists()
            this.loadInstAccountGraphAPI()
        },
        computed: {
            newFollowersDataAmount() {
                return this.$store.state.newFollowersData.size
            },
            instProfile() {
                return this.$store.state.instProfile
            },
            newFollowersData() {
                return this.$store.state.newFollowersData
            }

        },
        methods: {
            loadInstAccountGraphAPI() {
                this.scrapperInstAccount = this.$store.state.instProfile
            },
            async loadFollowersAndFollowsLists() {
                /*SEND DATA TO SERVER AND GET FOLLOWERS LIST*/
                let dataToServer = {
                    'username': this.instUsername,
                    'sessionid': this.sessionid
                }
                axios.post('/api/social/instagram/graph/processFollowers', dataToServer, configJson).then(response => {
                    console.log(response.data)

                    let followersArray = response.data[1]
                    let followers = []
                    let followersOK = []

                    /**/
                    for (let index in followersArray) {
                        let stringObject = {}
                        stringObject = JSON.parse(followersArray[index])
                        followers.push(stringObject)
                    }
                    console.log(followers)
                    followers.forEach(array => {
                        for (let index in array) {
                            followersOK.push(array[index].node)
                        }
                    })
                    console.log(followersOK)
                })
            },
            loadInstDataFreeAPI() {
                this.loadCrapperInstAccount()
            },
            loadCrapperInstAccount() {
                console.log('loadInstAccount')
                this.loading = true
                let instUsername = this.scrapperInstUsername
                const uri = '/api/social/instagram/scraper/loadInstProfile'

                axios.post(uri, instUsername, configJson).then(response => {
                    console.log(response)
                    console.log(response.data)

                    this.scrapperInstAccount = response.data
                    this.instAccountPic = response.data.profilePicUrlHd
                    this.instAccountPicFull = response.data.profilePicUrlHd
                    this.totalPosts = response.data.media.count
                    this.instPosts = response.data.media.nodes
                    this.postsLoading = false
                    this.loading = false
                })
            },
        }
    }
</script>

<style scoped>
</style>