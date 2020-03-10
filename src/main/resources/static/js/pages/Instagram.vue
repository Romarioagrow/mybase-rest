<template>
    <v-content>
        <v-container class="fill-height" fluid>

            <!--<v-row>
                <v-col>
                    <v-card-actions>
                        <v-btn @click="test">Test</v-btn>
                    </v-card-actions>
                </v-col>
            </v-row>-->

            <v-row>
                <v-col>
                    <v-card wight="850">
                        <!--ВВОД-->
                        <v-card-title>
                            Facebook Graph API
                        </v-card-title>

                        <!--<v-card-actions>
                            <v-btn @click="test">Test</v-btn>
                        </v-card-actions>-->

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
                            <v-card-actions>
                                <div>New followers: +<b>{{newFollowersDataAmount}}</b> (For February)</div>
                            </v-card-actions>
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
                                    <v-text-field solo dense v-model="instUsername"></v-text-field>
                                </v-col>
                                <v-col>
                                    <v-btn block color="error" @click="loadInstData()">GO</v-btn>
                                </v-col>
                            </v-row>
                        </v-card-actions>
                        <v-divider></v-divider>
                        <v-card-title v-if="!loading">
                            <span >{{instAccount.username}}</span>
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
                                                <div class="headline font-weight-thin">{{instAccount.fullName}}</div>
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col >
                                                <v-btn :disabled="followsLoading" outlined>Followers: {{instAccount.followedBy}}
                                                    <v-icon v-if="!followsLoading">mdi-arrow-up</v-icon>
                                                    <v-progress-circular v-else
                                                                         indeterminate
                                                                         size="20"
                                                                         width="1"
                                                                         color="primary"
                                                                         class="ml-2"
                                                    ></v-progress-circular>
                                                </v-btn>
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col>
                                                <v-btn :disabled="followsLoading" outlined>Following: {{instAccount.follows}}
                                                    <v-icon v-if="!followsLoading">mdi-arrow-up</v-icon>
                                                    <v-progress-circular v-else
                                                                         indeterminate
                                                                         size="20"
                                                                         width="1"
                                                                         color="primary"
                                                                         class="ml-2"
                                                    ></v-progress-circular>
                                                </v-btn>
                                            </v-col>
                                        </v-row>
                                    </v-col>
                                    <v-col>
                                        <v-row>
                                            <v-col>
                                                <div class="title font-weight-light">{{instAccount.biography}}</div>
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col>
                                                <div class="subtitle-2">{{instAccount.externalUrl}}</div>
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
    export default {
        data() {
            return {
                instAccount: {},
                instUsername: 'pollytwist',//'romarioagrow',
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
                //instProfile: {}
            }
        },
        created() {
            /*this.loadInstAccount()
            this.loadInstPosts()
            this.loadInstFollows()*/

            this.restRequests()

            this.loadFollowersList()
            this.instAccount = this.$store.state.instProfile
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
            test() {

                let options = {
                    url: url,
                    headers: {
                        'Content-Type': 'application/json',
                        Cookie: "Cookie value which i copied from my logged in instagram browser window"
                    }
                };
            },

            restRequests() {
                axios.post('/api/profile/instagram/restRequests', configJson).then(response => {

                    console.log('response: ' + response)
                })
            },


            async loadFollowersList() {
                //let instUsername = 'romarioagrow'
                let config = {
                    followers: {
                        hash: 'c76146de99bb02f6415203be841dd25a',
                        path: 'edge_followed_by'
                    },
                    followings: {
                        hash: 'd04b0a864b4b54837c0d870b0e77e076',
                        path: 'edge_follow'
                    }
                };


                let url = 'https://www.instagram.com/'+this.instUsername+'/?__a=1'
                let data = {followers: [], followings: []};
                let userID = 201512132

                let after = await axios.get(url, this.instUsername).then(response => {
                    console.log(response.data)
                    return response.data.graphql.user.edge_owner_to_timeline_media.page_info.end_cursor
                })

                console.log('after: ' + after)

                let followersURL = `https://www.instagram.com/graphql/query/?query_hash=${config['followers'].hash}&variables=${encodeURIComponent(JSON.stringify({
                    "id": userID,
                    "include_reel": true,
                    "fetch_mutual": true,
                    "first": 50,
                    "after": after
                }))}`
                console.log('followersURL: ' + followersURL)


                axios.get(followersURL, {
                    headers: {
                        Cookie: "sessionid=1038252798:4VmrnL0LfPY3xY:20;"
                    }
                }).then(response => {

                    console.log('response: ' + response)
                    console.log('response.data: ' + response.data)
                    console.log('response.data stringify: ' + JSON.stringify(response.data))
                    console.log('response.data.user: ' + response.data.user)


                    let stringResponse = JSON.stringify(response.data)

                    if (obj.hasOwnProperty("id")) {
                        console.log(obj.data);
                    }

                    data[list].push(...response.data.user[config[list].path].edges);

                    if (response.data.user[config[list].path].page_info.has_next_page) {
                        setTimeout(function () {
                            getFollows(user, list, response.data.user[config[list].path].page_info.end_cursor);
                        }, 1000);
                    }
                    else if (list === 'followers') {
                        getFollows(user, 'followings');
                    }
                    else {
                        alert('DONE!');
                        console.log(followers);
                        console.log(followings);
                    }
                })

            },

            loadInstData() {
                this.loadInstAccount()
                this.loadInstPosts()
                this.loadInstFollows()
            },

            loadInstAccount() {

                console.log('loadInstAccount')
                this.loading = true
                let instUsername = this.instUsername
                const uri = '/api/profile/instagram/loadInstProfile'

                axios.post(uri, instUsername, configJson).then(response => {
                    this.instAccount = response.data
                    this.instAccountPic = response.data.profilePicUrlHd
                    this.instAccountPicFull = response.data.profilePicUrlHd
                    this.loading = false
                    //console.log( this.instAccount)
                })
            },

            loadInstPosts() {
                console.log('loadInstPosts')
                this.postsLoading = true
                let instUsername = this.instUsername
                const uri = '/api/profile/instagram/loadInstPosts'

                axios.post(uri, instUsername, configJson).then(response => {
                    this.totalPosts = response.data.count
                    this.instPosts = response.data.nodes
                    this.postsLoading = false
                    //console.log(this.instPosts)
                })
            },

            loadInstFollows() {
                console.log('loadInstFollows')
                this.followsLoading = true
                //this.postsLoading = true
                let instUsername = this.instUsername
                const uri = '/api/profile/instagram/loadInstFollows'

                axios.post(uri, instUsername, configJson).then(response => {
                    //this.totalPosts = response.data.count
                    //this.instPosts = response.data.nodes
                    //this.postsLoading = false
                    this.followsLoading = false
                    //console.log(response.data)
                })

            },


            /*instProfile() {
                axios.post('/api/data/instProfile').then(response => {
                    //this.loadSpendingData(response.data)
                })
            },
            instFollowers() {
                axios.post('/api/data/instFollowers').then(response => {
                    //this.loadSpendingData(response.data)
                })
            }*/
        }
    }
</script>

<style scoped>
</style>