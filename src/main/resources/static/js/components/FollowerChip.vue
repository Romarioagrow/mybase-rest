<template>
    <v-container>
        <v-row>
            <v-col v-for="(follower, i) in followersList" :key="follower.key">
                <v-chip outlined @click="">
                    <v-img contain max-height="30" max-width="30" :src="getPic(follower)"></v-img>
                    <div class="ml-2">{{getUsername(follower)}}</div>
                </v-chip>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    export default {
        props: ['followersList'],
        data() {
            return {

            }
        },
        methods: {
            getUsername(lostFollower) {
                let array = lostFollower.split(", ")
                return array[1].substr(array[1].indexOf('=') + 1)
            },
            getName(lostFollower) {
                let array = lostFollower.split(", ")
                return array[2].substr(array[2].indexOf('=') + 1)
            },
            getPic(lostFollower) {
                let array = lostFollower.split(", ")
                return array[3].substr(array[3].indexOf('=') + 1)
            },
        }
    }
</script>

<style scoped>

</style>