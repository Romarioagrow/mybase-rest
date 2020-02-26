<template>
    <v-app id="inspire">
        <div v-if="userAuth">
            <v-navigation-drawer v-model="drawer" :clipped="$vuetify.breakpoint.lgAndUp" app>
                <v-list dense>
                    <template v-for="item in menuLinks">
                        <v-list-item :key="item.text" link @click="goTo(item.link)">
                            <v-list-item-action>
                                <v-icon>{{ item.icon }}</v-icon>
                            </v-list-item-action>
                            <v-list-item-content>
                                <v-list-item-title>
                                    {{ item.text }}
                                </v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                    </template>
                </v-list>
            </v-navigation-drawer>
        </div>

        <!--NAV-->
        <v-app-bar
                :clipped-left="$vuetify.breakpoint.lgAndUp"
                app
                color="#660091"
                dark
        >
            <v-app-bar-nav-icon @click.stop="drawer = !drawer" v-if="userAuth"/>
            <v-toolbar-title style="width: 300px" class="ml-0 pl-4">
                <span class="hidden-sm-and-down">myBase</span>
            </v-toolbar-title>
            <v-spacer />
            <div v-if="userAuth">
                {{userName}}
            </div>
            <v-btn icon @click="goTo('/cabinet')">
                <v-icon v-if="userAuth">mdi-account</v-icon>
                <v-icon v-else>mdi-login</v-icon>
            </v-btn>
        </v-app-bar>

        <!--ROUTER-->
        <router-view></router-view>

    </v-app>
</template>

<script>
    import axios from "axios";
    export default {
        props: {
            source: String,
        },
        data() {
            return {
                user: null,
                dialog: false,
                drawer: null,
                menuLinks: [
                    { icon: 'mdi-airplay', text: 'Main page', link: '/'},
                    { icon: 'mdi-account-circle-outline', text: 'Profile', link: '/cabinet' },
                    { icon: 'mdi-currency-usd', text: 'Spending', link: '/spending' },
                    { icon: 'mdi-account-box-multiple', text: 'Posts', link: '/posts' },
                    { icon: 'mdi-account-network', text: 'Networks', link: '/networks' }
                ]
            }
        },
        methods: {
            goTo(link) {
                this.$router.push(link)
            },
            async loadUser() {
                let user = await axios.post('/api/user/getUser')
                await this.$store.dispatch("loadUser", user.data)
                this.user = user.data
            },
        },
        async created() {
            await this.loadUser()

            //console.log(this.userAuth)

            if (!this.userAuth) {
                this.$router.push('/cabinet')
            }
        },
        computed: {
            userAuth() {
                return this.$store.state.currentUser
            },
            userName() {
                return this.$store.state.currentUser.name
            }
        }
    }
</script>
