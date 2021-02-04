<template>
    <v-content>
        <v-container fluid>
            <v-row v-if="user">
                <v-col>
                    <div class="display-2 font-weight-thin">myBase Main Page for <span class="font-weight-light">{{user.name}}</span></div>
                </v-col>
            </v-row>
            <v-row v-else>
                <v-col>
                    <div class="display-2 font-weight-light">MyBase</div>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-card max-width="434" tile>
                        <v-img height="100%" src="https://cdn.steemitimages.com/DQmPgK575YnZP6uWG3iDMqPicEqrrwgVgSGTPmo1SqSpxS8/3402463-KNVHBTVS-7.jpg">
                            <v-row align="end" class="fill-height">
                                <v-col class="py-0">
                                    <v-list-item dark>
                                        <v-list-item-content>
                                            <v-list-item-title class="title black--text">Main Home Page</v-list-item-title>
                                            <v-list-item-subtitle class="black--text">what was that?</v-list-item-subtitle>
                                        </v-list-item-content>
                                    </v-list-item>
                                </v-col>
                            </v-row>
                        </v-img>
                    </v-card>
                </v-col>
                <v-col > <!--v-if="!user"-->
                    <v-card
                            height="600"
                            max-width="400"
                            color="#9dbedf"
                            hover
                            @click="goTo('/auth')"
                    >
                        <v-img class="white--text align-end" height="200px" src="https://vistapointe.net/images/art-6.jpg">
                            <v-card-title>LOGIN</v-card-title>
                        </v-img>
                        <v-card-title>
                            <v-icon class="display-4 mx-auto">mdi-login</v-icon>
                        </v-card-title>

                    </v-card>
                </v-col>
            </v-row>
        </v-container>
    </v-content>
</template>

<script>
    import axios from "axios";
    const config = {
        headers: {
            'Content-Type': 'application/json'
        }
    }
    export default {
        computed: {
            user() {
                return this.$store.state.currentUser
            }
        },
        data() {
            return {
                instUsername: 'romarioagrow'
            }
        },
        methods: {
            test() {
                axios.post('/api/profile/instagram/test', this.instUsername, config).then(response => {
                    console.log(response.data)
                })
            },
            goTo(link) {
                this.$router.push(link)
            }
        }
    }
</script>

<style scoped></style>