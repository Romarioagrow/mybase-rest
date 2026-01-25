<template>
  <div class="magic-shell">

<!--<app-main-drawer>-->

    <v-navigation-drawer
      v-model="drawer"
      :clipped="$vuetify.breakpoint.lgAndUp"
      :temporary="$vuetify.breakpoint.smAndDown"
      app
      class="magic-drawer"
    >
      <div class="magic-drawer-header">
        <div class="magic-rune">MyBase</div>
        <div class="magic-tag">Astral navigation</div>
      </div>
      <v-list dense>
        <template v-for="item in visibleLinks">
          <v-list-item :key="item.text" link @click="goTo(item.link)" class="magic-drawer-item">
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
        color="#1d1a37"
        dark
        class="magic-bar"
    >

      <div style="width: 5rem; margin-left: -1rem; cursor: pointer;" @click="goTo('/')">
        <v-img @click.stop="drawer = !drawer" src="https://i.pinimg.com/originals/c0/c8/76/c0c876ca5ddde13f55fd2b3d19a5e53d.jpg" height="64"></v-img>
      </div>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer" />
      <v-toolbar-title style="width: 300px" class="ml-0 pl-4 magic-title">
        MyBase
      </v-toolbar-title>
      <v-spacer/>

      <div v-if="userAuth" class="mr-3">
        <v-row align="center">
          <v-col>
            <div class="body-1 font-weight-regular magic-username" style="padding-top: 10px;">{{ userName }}</div>
          </v-col>
          <v-col @click="goTo('/cabinet')" style="cursor: pointer;">
            <v-avatar color="#2b2550">
              <img v-if="userPic" :src="userPic" alt="User">
              <v-icon v-else color="#f7f4ff">mdi-account-circle</v-icon>
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
      user: {},
      dialog: false,
      drawer: true,
      menuLinks: [
        {icon: 'mdi-airplay', text: 'Main page', link: '/'},
        {icon: 'mdi-fingerprint', text: 'MyBase', link: '/mybase'},
        {icon: 'mdi-account-circle-outline', text: 'Profile', link: '/auth'},
        {icon: 'mdi-instagram', text: 'Instagram', link: '/instagram'},
        {icon: 'mdi-currency-usd', text: 'Spending', link: '/spending'},
        {icon: 'mdi-menu', text: 'The Logs', link: '/thelogs'},
        {icon: 'mdi-menu', text: 'Goals', link: '/goals'},
      ]
    }
  },
  async beforeCreate() {
    console.log('async beforeCreate()')
  },
  methods: {
    goTo(link) {
      this.$router.push(link)
    },
  },
  computed: {
    userAuth() {
      return this.$store.getters.userAuth
    },
    visibleLinks() {
      return this.menuLinks
    },

    userPic() {
      const inst = this.$store.state.userAccount && this.$store.state.userAccount.instProfile
      return inst && inst.profile_picture_url ? inst.profile_picture_url : ''
    },

    userName() {
      const profile = this.$store.state.userAccount && this.$store.state.userAccount.currentProfile
      if (!profile) return ''
      const name = profile.username || profile.email || ''
      return String(name).toUpperCase()
    }
  }
}


</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600&family=Space+Grotesk:wght@300;400;600&display=swap');

.magic-shell {
  min-height: 100vh;
  background: radial-gradient(1200px 600px at 20% 0%, rgba(69, 45, 117, 0.6), transparent),
              radial-gradient(800px 500px at 80% 10%, rgba(15, 32, 70, 0.7), transparent),
              #0b0d18;
}

.magic-bar {
  background: linear-gradient(120deg, rgba(35, 24, 63, 0.95), rgba(13, 20, 50, 0.95)) !important;
  backdrop-filter: blur(8px);
  border-bottom: 1px solid rgba(255,255,255,0.08);
}

.magic-title {
  font-family: "Cinzel", serif;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  font-size: 0.85rem;
}

.magic-username {
  font-family: "Cinzel", serif;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: #f7f4ff;
}

.magic-drawer {
  background: rgba(10, 12, 22, 0.98) !important;
  border-right: 1px solid rgba(255,255,255,0.08);
}

.magic-drawer-header {
  padding: 18px 20px 8px 20px;
}

.magic-rune {
  font-family: "Cinzel", serif;
  font-size: 1.2rem;
  color: #f6d365;
  letter-spacing: 0.1em;
}

.magic-tag {
  font-family: "Space Grotesk", sans-serif;
  font-size: 0.75rem;
  color: rgba(230,225,255,0.6);
  text-transform: uppercase;
  letter-spacing: 0.2em;
}

.magic-drawer-item {
  border-radius: 12px;
  margin: 4px 10px;
  transition: background 0.2s ease, transform 0.2s ease;
  color: #f7f4ff;
}

.magic-drawer-item:hover {
  background: rgba(246,211,101,0.12);
  transform: translateX(4px);
}

.magic-drawer-item .v-icon,
.magic-drawer-item .v-list-item__title,
.magic-drawer-item .v-list-item__action {
  color: #f7f4ff !important;
}

.magic-drawer-item .v-list-item__title {
  font-family: "Space Grotesk", sans-serif;
  letter-spacing: 0.02em;
}

</style>
