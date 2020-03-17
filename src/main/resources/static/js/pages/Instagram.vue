<template>
    <v-content>
        <b-container class="fill-height" style="width: 90% !important;">
            <v-row>
                <v-col>
                    <v-card wight="950" outlined tile>
                        <v-card-title>
                            <v-icon class="mr-3">mdi-instagram</v-icon>
                            <div class="headline font-weight-light">Instagram&nbsp;</div><span class="headline font-weight-light">{{instProfile.username}}</span>
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

                                <v-col cols="4" class="pt-0">
                                    <v-row>
                                        <v-col>
                                            <div class="headline font-weight-thin">{{instProfile.name}}</div>
                                        </v-col>
                                    </v-row>
                                    <v-row>
                                        <v-col>
                                            <v-btn text outlined >
                                                <div @click="openFollowersDialog()" class="font-weight-light">Followers:&nbsp;<span class="font-weight-medium">{{instProfile.followers_count}}</span></div>
                                            </v-btn>

                                            <v-dialog v-model="dialogFollowers" max-width="1500">
                                                <v-card>
                                                    <v-card-title class="pb-0">
                                                        <div class="font-weight-light headline">Your&nbsp;<span class="font-weight-medium">{{instProfile.followers_count}}</span> followers from last to first</div>
                                                    </v-card-title>
                                                    <v-card-actions >
                                                        <v-row class="pt-0" style="height: 3rem !important;">
                                                            <v-col cols="1">
                                                                <v-btn text small>
                                                                    <v-icon dark>mdi-wrench</v-icon>
                                                                </v-btn>
                                                            </v-col>
                                                            <v-col cols="1">
                                                                <v-btn text small>
                                                                    <v-icon dark>mdi-cloud-upload</v-icon>
                                                                </v-btn>
                                                            </v-col>
                                                            <v-col cols="3">
                                                                <v-text-field label="Solo" single-line solo dense/>
                                                            </v-col >
                                                        </v-row>
                                                    </v-card-actions>
                                                    <v-card-actions>
                                                        <follower-chip :followersList="followersData.followers"/>
                                                    </v-card-actions>
                                                </v-card>
                                            </v-dialog>
                                        </v-col>
                                    </v-row>
                                    <v-row>
                                        <v-col>
                                            <v-btn text outlined >
                                                <div class="font-weight-light">Following:&nbsp;<span class="font-weight-medium">{{instProfile.follows_count}}</span></div>
                                            </v-btn>
                                        </v-col>
                                    </v-row>
                                </v-col>

                                <v-col class="pt-0">
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
                            <v-btn class="ml-12"
                                   outlined
                                   @click="loadInstFollowersLists()"
                                   :color="buttonColor"
                                   :loading="followersLoading"
                            >
                                <div>Last Update: <span>{{lastUpdate}}</span></div>
                            </v-btn>
                        </v-card-actions>
                        <v-divider/>
                        <v-card-actions class="p-0">
                            <v-expansion-panels accordion multiple tile flat hover focusable>
                                <v-expansion-panel style="border-bottom: 1px solid #e0e0e0;"
                                                   :disabled="followersLoading"
                                >
                                    <v-expansion-panel-header >
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
                                            <v-col>
                                                <div class="font-weight-thin headline">New followers</div>
                                                <follower-chip :followersList="followersData.newFollowers"/>
                                            </v-col>
                                            <v-divider vertical/>

                                            <v-col>
                                                <div class="font-weight-thin headline">Lost followers</div>
                                                <follower-chip :followersList="followersData.lostFollowers"/>

                                            </v-col>
                                            <v-divider vertical/>

                                            <v-col>
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
                            </v-expansion-panels>
                        </v-card-actions>
                    </v-card>
                </v-col>
            </v-row>
            <v-spacer/>
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
        /*!!!!!!!!!
        *
        * ПРИРОСТ FOLLOWERS FOR/SINCE LAST UPDATE
        *
        * */
        components: {FollowerChip},
        data() {
            return {

                dialogFollowers: false,
                followersLoading: true,
                buttonColor: 'error',

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
            lastUpdate() {
                const update = this.followersData.lastUpdate
                return update ? this.calculateLastUpdate(update) : 'X'
            },

            instProfile() {
                return this.$store.state.instProfile
            },
            newFollowersData() {
                return this.$store.state.newFollowersData
            }
        },
        methods: {
            async loadInstagramPage() {
                await this.loadInstAccountGraphAPI()
                await this.loadInstFollowersListsDB()
            },
            /**/

            openFollowersDialog() {
                this.dialogFollowers = true

            },

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
                    this.followersLoading = false
                }
                else
                {
                    console.log('followersData LOADING FROM API')
                    const url = '/api/social/instagram/graph/loadInstFollowersListsDB'
                    this.loadInstFollowersLists(url, 'no_color')
                }
            },
            async loadInstFollowersLists(url, no_color) {
                this.followersLoading = true

                let requestURL = url ? url : '/api/social/instagram/graph/loadInstFollowersData'
                console.log('loadInstFollowersLists')
                console.log('url: ' + requestURL)

                /*SEND DATA TO SERVER AND GET FOLLOWERS LIST*/
                let dataToServer = {
                    'username': this.instUsername,
                    'sessionid': this.sessionid
                }
                await axios.post(requestURL, dataToServer, configJson).then(response => {
                    console.log(response.data.lostFollowersAmount)

                    this.followersData = response.data
                    this.$store.dispatch('loadInstDataToStorage', response.data)
                    this.followersLoading = false
                })

                this.followersLoading = false
                if (!no_color) this.buttonColor = 'success'
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