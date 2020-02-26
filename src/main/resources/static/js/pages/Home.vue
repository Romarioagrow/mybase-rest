<template>
    <v-content>
        <v-container fluid>
            <v-row v-if="user">
                <v-col>
                    <div class="display-2 font-weight-thin">myBase Main Page for <span class="font-weight-light">{{user.name}}</span></div>
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
            </v-row>
        </v-container>
    </v-content>
</template>

<script>
    export default {
        computed: {
            user() {
                return this.$store.state.currentUser
            }
        }
    }
</script>

<style scoped>
</style>