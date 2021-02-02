<template>
  <div>

<!--<app-main-drawer>-->

    <v-navigation-drawer v-model="drawer" :clipped="$vuetify.breakpoint.lgAndUp" app absolute > <!--v-if="userAuth"-->
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

    <!--NAV-->
    <v-app-bar
        :clipped-left="$vuetify.breakpoint.lgAndUp"
        app
        color="#660091"
        dark
    >

      <div style="width: 5rem; margin-left: -1rem; cursor: pointer;" @click="goTo('/')">
        <v-img @click.stop="drawer = !drawer" src="https://i.pinimg.com/originals/c0/c8/76/c0c876ca5ddde13f55fd2b3d19a5e53d.jpg" height="64"></v-img>
      </div>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer" /> <!--v-if="userAuth"-->
      <v-toolbar-title style="width: 300px" class="ml-0 pl-4">
      </v-toolbar-title>
      <v-spacer/>

      <div v-if="userAuth" class="mr-3">
        <v-row>
          <v-col>
            <div class="body-1 font-weight-regular" style="padding-top: 10px;">{{ userName }}</div>
          </v-col>
          <v-col @click="goTo('/cabinet')" style="cursor: pointer;">
            <v-avatar>
              <img :src="userPic" alt="John">
            </v-avatar>
          </v-col>
        </v-row>
      </div>
      <!--//userName-->
      <v-btn icon @click="goTo('/cabinet')" v-else>
        <v-icon>mdi-login</v-icon>
      </v-btn>
    </v-app-bar>

    <router-view></router-view>

  </div>
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
      drawer: true,
      menuLinks: [
        {icon: 'mdi-airplay', text: 'Main page', link: '/'},
        {icon: 'mdi-account-circle-outline', text: 'Profile', link: '/cabinet'},
        {icon: 'mdi-instagram', text: 'Instagram', link: '/instagram'},
        {icon: 'mdi-currency-usd', text: 'Spending', link: '/spending'},
        {icon: 'mdi-menu', text: 'The Logs', link: '/thelogs'},
        {icon: 'mdi-menu', text: 'Goals', link: '/goals'},
      ]
    }
  },
  async created() {
    await this.loadUser()
    if (!this.userAuth) {
      this.$router.push('/cabinet')
    }
  },
  methods: {
    goTo(link) {
      this.$router.push(link)
    },
    async loadUser() {
      let user = await axios.post('/api/user/getUser')

      if (user) {
        await this.$store.dispatch("loadUser", user.data)
        this.user = user.data
      }
    },
  },
  computed: {
    userPic() {
      return this.$store.state.instProfile.profile_picture_url
    },
    userAuth() {
      return this.$store.state.instProfile
    },
    userName() {
      return this.$store.state.instProfile.username
    }
  }
}


</script>

<style scoped>

</style>