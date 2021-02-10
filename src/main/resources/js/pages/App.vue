<template>
    <v-app id="my-base-app">

      <app-template></app-template>

    </v-app>
</template>

<script>
    import AppTemplate from "components/AppTemplate.vue";
    import axios from "axios";

    export default {
      components: {AppTemplate},
      data() {
        return {

        }
      },
      beforeCreate() {


      },
      created() {
        this.initApp()
        this.loadUserAuthorization()
      },
      methods: {
        loadUserAuthorization() {
          console.log('loadUserAuthorization()')

          axios.post('/api/user/auth/getUser').then(userResponse => {
            //userResponse

            const userResponseData = userResponse.data

            if (userResponseData) {
              console.log('has userResponseData', userResponseData)
              this.$store.dispatch("setUserAuthorization", userResponseData)
              /*await*/
              //this.user = user.data
            }
            else {
              console.log('no userResponseData, clearUserAuthorization')
              this.$store.dispatch("clearUserAuthorization")
            }

          })

          /*let user = await axios.post('/api/user/auth/getUser')
          console.log('async user',user)

          if (user) {
            console.log('if (user)', user)
            await this.$store.dispatch("loadUser", user.data)
            this.user = user.data
          }*/
        },

        initApp() {
          console.log('initApp')

          //this.$store.dispatch('hasAuth')
        }
      },
      computed: {

      }
    }
</script>
