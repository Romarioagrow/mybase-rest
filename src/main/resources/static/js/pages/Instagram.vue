<template>
    <v-content>
        <b-container class="fill-height">
            <v-row>
                <v-col>
                    <v-card wight="950" outlined tile>
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
                                <v-col cols="2">
                                    <v-card width="150" height="150" @click.stop="dialogGraph = true" hover class="ml-12">
                                        <v-img contain :src="instProfile.profile_picture_url"></v-img>
                                    </v-card>
                                    <v-dialog v-model="dialogGraph" max-width="800">
                                        <v-card>
                                            <v-img contain :src="instProfile.profile_picture_url"></v-img>
                                            <v-card-actions>
                                                <v-btn color="error" block @click="dialogGraph = false">
                                                    Save photo
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
                                            <div>Followers:&nbsp;<span class="font-weight-medium">{{instProfile.followers_count}}</span></div>
                                        </v-col>
                                    </v-row>
                                    <v-row>
                                        <v-col>
                                            <div>Following:&nbsp;<span class="font-weight-medium">{{instProfile.follows_count}}</span></div>
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
                                            <v-btn text outlined small>{{instProfile.website}}</v-btn>
                                        </v-col>
                                    </v-row>
                                </v-col>
                            </v-row>
                        </v-card-actions>
                        <v-divider/>
                        <v-card-actions>
                            <v-btn outlined @click="loadInstFollowersLists()" class="ml-12">
                                <div>Last Update: <span>{{calculateLastUpdate(followersData.lastUpdate)}}</span></div>
                            </v-btn>
                        </v-card-actions>
                        <v-divider/>
                        <v-card-actions class="p-0">
                            <v-expansion-panels accordion multiple tile flat>
                                <v-expansion-panel style="border-bottom: 1px solid #e0e0e0;">
                                    <v-expansion-panel-header>
                                        <v-row>
                                            <v-col cols="1">
                                                <strong>Followers</strong>
                                            </v-col>
                                            <v-col>
                                                <div>New:&nbsp;<span class="font-weight-medium">{{followersData.newFollowersAmount}}</span></div>
                                            </v-col>
                                            <v-col>
                                                <div>Lost:&nbsp;<span class="font-weight-medium">{{followersData.lostFollowersAmount}}</span></div>
                                            </v-col>
                                            <v-col>
                                                <div>Not follows you:&nbsp;<span class="font-weight-medium">{{followersData.notYouAmount}}</span></div>
                                            </v-col>
                                            <v-col>
                                                <div>You not follow:&nbsp;<span class="font-weight-medium">{{followersData.youNotAmount}}</span></div>
                                            </v-col>
                                        </v-row>
                                    </v-expansion-panel-header>
                                    <v-expansion-panel-content>
                                        <v-row>
                                            <v-col >
                                                <div class="font-weight-thin headline">New followers</div>
                                                <follower-chip :followersList="followersData.newFollowers"/>
                                            </v-col>
                                            <v-divider vertical/>

                                            <v-col >
                                                <div class="font-weight-thin headline">Lost followers</div>
                                                <follower-chip :followersList="followersData.lostFollowers"/>

                                            </v-col>
                                            <v-divider vertical/>

                                            <v-col >
                                                <div class="font-weight-thin headline">Not following you back</div>
                                                <follower-chip :followersList="followersData.notFollowsYouBack"/>
                                            </v-col>
                                            <v-divider vertical/>

                                            <v-col>
                                                <div class="font-weight-thin headline">You not follow back</div>
                                                <follower-chip :followersList="followersData.youNotFollowBack"/>
                                            </v-col>
                                        </v-row>
                                    </v-expansion-panel-content>
                                </v-expansion-panel>
                                <v-expansion-panel v-for="(item, i) in 4" :key="i" style="border-bottom: 1px solid #e0e0e0;">
                                    <v-expansion-panel-header>
                                        <v-row>
                                            <v-col>
                                                Item
                                            </v-col>
                                            <v-col>
                                                Property
                                            </v-col>
                                            <v-col>
                                                Amount
                                            </v-col>
                                        </v-row>
                                    </v-expansion-panel-header>
                                    <v-expansion-panel-content>
                                        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                                    </v-expansion-panel-content>
                                </v-expansion-panel>
                            </v-expansion-panels>
                        </v-card-actions>
                    </v-card>

                </v-col>
            </v-row>
            <v-spacer/>
            <!--OPEN API-->
            <v-row v-if="false"> <!--!!!!!!-->
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
                                                <v-btn :disabled="false" outlined>Followers:
                                                    {{instAccount.followedBy}}
                                                    <v-icon v-if="!followsLoading">mdi-arrow-up</v-icon>
                                                </v-btn>
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col>
                                                <v-btn :disabled="false" outlined>Following:
                                                    {{instAccount.follows}}
                                                    <v-icon v-if="!followsLoading">mdi-arrow-up</v-icon>
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
        </b-container>
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
    import FollowerChip from "components/FollowerChip.vue";
    export default {
        components: {FollowerChip},
        data() {
            return {

                instAccount: {},
                instUsername: 'romarioagrow',
                scrapperInstUsername: 'romarioagrow',
                sessionid: 'sessionid=1038252798%3AHugtwm4FEOdmCZ%3A9;',
                instAccountPic: '',
                instAccountPicFull: '',
                loading: true,
                instPosts: [],
                dialogGraph: false,
                dialog: false,
                postsLoading: true,
                totalPosts: '',

                followsLoading: true,
                postDialog: false,
                followersDialog: false,

                followersData: {
                }
            }
        },
        created() {
            this.loadInstagramPage()
        },
        computed: {
            instProfile() {
                return this.$store.state.instProfile
            },
            newFollowersData() {
                return this.$store.state.newFollowersData
            }
        },
        methods: {
            async loadInstagramPage() {
                //this.loadCrapperInstAccount()
                await this.loadInstAccountGraphAPI()

                await this.loadInstFollowersListsDB()
            },
            /**/

            calculateLastUpdate(lastUpdate) {
                let dataTime = lastUpdate.substr(0, lastUpdate.lastIndexOf('.'))
                return dataTime.replace('-','.').replace('-','.').replace('T',' at ')
            },


            loadInstAccountGraphAPI() {
                this.instAccount = this.$store.state.instProfile /// GETTER()!
            },
            async loadInstFollowersListsDB() {
                const instFollowers = this.$store.state.instFollowers
                if (instFollowers !== null)
                {
                    this.followersData = instFollowers
                    console.log('followersData LOADED FROM STORAGE: ' + this.followersData)
                }
                else
                {
                    console.log('followersData LOADING FROM API')
                    const url = '/api/social/instagram/graph/loadInstFollowersListsDB'
                    this.loadInstFollowersLists(url)
                }
            },
            loadInstFollowersLists(url) {
                let requestURL = url ? url : '/api/social/instagram/graph/loadInstFollowersData'
                console.log('loadInstFollowersLists')
                console.log('url: ' + requestURL)

                /*SEND DATA TO SERVER AND GET FOLLOWERS LIST*/
                let dataToServer = {
                    'username': this.instUsername,
                    'sessionid': this.sessionid
                }
                axios.post(requestURL, dataToServer, configJson).then(response => {
                    console.log(response.data.lostFollowersAmount)

                    this.followersData = response.data
                    this.$store.dispatch('loadInstDataToStorage', response.data)
                })
            },
            /**/

            loadCrapperInstAccount() {
                console.log('loadInstAccount')
                this.loading = true
                let instUsername = this.scrapperInstUsername
                const uri = '/api/social/instagram/scraper/loadInstProfile'

                axios.post(uri, this.scrapperInstUsername, configJson).then(response => {
                    console.log(response)
                    console.log(response.data)

                    this.instAccount = response.data
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