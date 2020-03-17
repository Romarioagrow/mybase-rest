<template>
    <!--<v-expansion-panel v-for="(item, i) in 4" :key="i" style="border-bottom: 1px solid #e0e0e0;">
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
                                </v-expansion-panel>-->

    <!--&lt;!&ndash;OPEN API&ndash;&gt;
           <v-row v-if="false"> &lt;!&ndash;!!!!!!&ndash;&gt;
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
                       &lt;!&ndash;PROFILE&ndash;&gt;
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
                       &lt;!&ndash;POSTS&ndash;&gt;
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
                                                   &lt;!&ndash;<v-img contain :src="instAccountPicFull"></v-img>&ndash;&gt;
                                                   &lt;!&ndash; <v-img height="200" width="200" :src="instPost.displayUrl" @click="postDialog = true"></v-img>&ndash;&gt;
                                                   &lt;!&ndash;<v-dialog v-model="postDialog" max-width="800">
                                                       <v-card>
                                                           <v-img contain :src="instPost.displayUrl"></v-img>
                                                           <v-card-actions>
                                                               <v-btn color="error" block outlined @click="postDialog = false">
                                                                   Save
                                                               </v-btn>
                                                           </v-card-actions>
                                                       </v-card>
                                                   </v-dialog>&ndash;&gt;
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
           </v-row>-->
</template>

<script>
    export default {
        name: "InstOpenApi"
    }
</script>

<style scoped>

</style>